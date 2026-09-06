package jelena.etfbl.service;

import jelena.etfbl.database.MyConnection;
import jelena.etfbl.database.Timer;
import jelena.etfbl.database.Table;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class FillerCoordinator {
    private static Logger log = LoggerFactory.getLogger(FillerCoordinator.class);
    private static DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private  MyConnection config;
    private Consumer<String> logCallback;
    private Consumer<List<Timer>> completionCallback;

    private volatile boolean cancelled = false;

    public FillerCoordinator(MyConnection config,
                              Consumer<String> logCallback,
                              Consumer<List<Timer>> completionCallback) {
        this.config = config;
        this.logCallback = logCallback;
        this.completionCallback = completionCallback;
    }

    public void cancel() { cancelled = true; }

    public void execute() {
        List<Timer> results = new ArrayList<>();
        LocalDateTime overallStart = LocalDateTime.now();

        emit("=".repeat(70));
        emit("DATABASE TEST DATA FILLER");
        emit("Start time: " + overallStart.format(FMT));
        emit("Database:   " + config.getDbType() + " @ " + config.buildURL());
        emit("=".repeat(70));

        SessionFactory sessionFactory = null;
        try {
            emit("\n[PHASE 1] Building Hibernate SessionFactory...");
            sessionFactory = buildSessionFactory();
            emit("Hibernate SessionFactory created");

            String dbVersion = getDbVersion(sessionFactory);
            emit("Connected: " + dbVersion);

            if (cancelled) return;

            emit("\n[PHASE 2] Introspecting schema...");
            Map<String, Table> tables;
            SessionFactory finalSf = sessionFactory;
            tables = introspectThroughHibernate(finalSf);
            emit("✓ Found " + tables.size() + " tables");
            tables.forEach((name, tm) -> emit("  • " + tm));

            if (tables.isEmpty()) {
                emit("No tables found. Make sure the database schema exists and is not empty.");
                completionCallback.accept(results);
                return;
            }

            if (cancelled) return;

            emit("\n[PHASE 3] Analysing dependency graph...");
            GraphAnalyzer analyzer = new GraphAnalyzer(tables);
            List<List<String>> levels = analyzer.findSameLevels();
            emit(analyzer.renderGraph());

            emit("\n[PHASE 4] Filling tables...");
            emit("Rows per table : " + String.format("%,d", config.getRowsPerTable()));
            emit("Batch size     : " + config.getBatchSize());
            emit("Thread pool    : " + config.getThreadPoolSize());
            emit("-".repeat(70));

            DataGenerator  generator = new DataGenerator();
            ExecutorService executor = Executors.newFixedThreadPool(config.getThreadPoolSize());

            for (int lvl = 0; lvl < levels.size() && !cancelled; lvl++) {
                List<String> levelTables = levels.get(lvl);
                emit(String.format("\n--- Level %d: %s (parallel) ---", lvl, levelTables));

                List<Future<Timer>> futures = new ArrayList<>();
                for (String tableName : levelTables) {
                    Table tm = tables.get(tableName);
                    if (tm == null) continue;

                    TableFiller filler = new TableFiller(
                            sessionFactory, tm, generator,
                            config.getRowsPerTable(), config.getBatchSize(),
                            this::emit, config.getDbType()
                    );
                    futures.add(executor.submit(filler::fill));
                }

                // Čekamo da se cijeli level završi prije nego što pređemo na sljedeći
                for (Future<Timer> f : futures) {
                    try {
                        Timer r = f.get();
                        results.add(r);
                        emit(r.toString());
                    } catch (ExecutionException e) {
                        log.error("Fill failed", e.getCause());
                        emit("ERROR: " + e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            executor.shutdown();

            LocalDateTime overallEnd = LocalDateTime.now();
            long totalMs = java.time.Duration.between(overallStart, overallEnd).toMillis();

            emit("\n" + "=".repeat(70));
            emit("SUMMARY");
            emit("=".repeat(70));
            emit("End time:   " + overallEnd.format(FMT));
            emit(String.format("Total time: %,d ms (%.2f s)", totalMs, totalMs / 1000.0));
            emit("Tables filled: " + results.size());

            long totalRows = results.stream()
                    .filter(Timer::isSuccess)
                    .mapToLong(Timer::getRowsInserted)
                    .sum();
            emit(String.format("Total rows inserted: %,d", totalRows));
            emit(String.format("Overall rate: %.0f rows/s",
                    totalRows / Math.max(totalMs / 1000.0, 0.001)));
            emit("-".repeat(70));
            emit("Per-table breakdown:");
            results.forEach(r -> emit("  " + r));
            emit("=".repeat(70));

        } catch (Exception e) {
            log.error("Orchestration failed", e);
            emit("FATAL ERROR: " + e.getMessage());
        } finally {
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                sessionFactory.close();
            }
            completionCallback.accept(results);
        }
    }

    private SessionFactory buildSessionFactory() {
        Configuration cfg = new Configuration();

        cfg.setProperty("hibernate.connection.driver_class", config.getDbType().getDriverClass());
        cfg.setProperty("hibernate.connection.url",          config.buildURL());
        cfg.setProperty("hibernate.connection.username",     config.getUsername());
        cfg.setProperty("hibernate.connection.password",     config.getPassword());

        cfg.setProperty("hibernate.dialect", config.getDbType().getHibernateDialect());

        cfg.setProperty("hibernate.connection.pool_size",
                String.valueOf(config.getThreadPoolSize() + 2));

        cfg.setProperty("hibernate.jdbc.batch_size", String.valueOf(config.getBatchSize()));
        cfg.setProperty("hibernate.order_inserts",   "true");
        cfg.setProperty("hibernate.order_updates",   "true");

        cfg.setProperty("hibernate.show_sql", "false");
        return cfg.buildSessionFactory();
    }


    private String getDbVersion(SessionFactory sf) {
        final String[] version = {"Unknown"};
        sf.inSession(session -> {
            session.doWork(conn -> {
                version[0] = conn.getMetaData().getDatabaseProductName()
                        + " " + conn.getMetaData().getDatabaseProductVersion();
            });
        });
        return version[0];
    }

    private Map<String, Table> introspectThroughHibernate(SessionFactory sf) {
        final Map<String, Table>[] result = new Map[]{new LinkedHashMap<>()};
        sf.inSession(session -> {
            session.doWork(conn -> {
                DatabaseGraph analyzer = new DatabaseGraph(conn, config);
                result[0] = analyzer.analyze();
            });
        });
        return result[0];
    }

    private void emit(String message) {
        log.info(message);
        logCallback.accept(message);
    }
}