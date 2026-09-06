package jelena.etfbl.service;

import jelena.etfbl.database.MyConnection;
import jelena.etfbl.database.Timer;
import jelena.etfbl.database.Table;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TableFiller {
    private static final Logger log = LoggerFactory.getLogger(TableFiller.class);

    private final SessionFactory sessionFactory;
    private final Table table;
    private final DataGenerator generator;
    private final int rowCount;
    private final int batchSize;
    private final Consumer<String> progressCallback;
    private final MyConnection.DatabaseType dbType;

    public TableFiller(SessionFactory sessionFactory, Table table,
                       DataGenerator generator, int rowCount, int batchSize,
                       Consumer<String> progressCallback, MyConnection.DatabaseType dbType) {
        this.sessionFactory = sessionFactory;
        this.table = table;
        this.generator = generator;
        this.rowCount = rowCount;
        this.batchSize = batchSize;
        this.progressCallback = progressCallback;
        this.dbType = dbType;
    }

    public Timer fill() {
        Timer result = new Timer(table.getTableName());

        log.info("[{}] Starting fill: {} rows", table.getTableName(), rowCount);
        progressCallback.accept(String.format("[%s] Starting → %,d rows",
                table.getTableName(), rowCount));

        List<Table.Column> insertableCols = table.getInsertableColumns();
        if (insertableCols.isEmpty()) {
            log.warn("[{}] No insertable columns, skipping", table.getTableName());
            result.complete(0);
            return result;
        }

        String sql = buildInsertSql(insertableCols);
        log.debug("[{}] SQL: {}", table.getTableName(), sql);

        boolean hasAutoIncrPk = table.getPrimaryKeys().stream()
                .anyMatch(Table.Column::isAutoIncrement);
        List<Object> cachedPks = new ArrayList<>();
        final long[] totalRef = {0};

        // Session.doWork otvara JDBC konekciju iz Hibernate connection poola
        try {
            sessionFactory.inSession(session -> {
                session.doWork(conn -> {
                    conn.setAutoCommit(false);
                    disableFkChecks(conn);

                    try (PreparedStatement ps = hasAutoIncrPk
                            ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                            : conn.prepareStatement(sql)) {

                        int  batchCount = 0;

                        for (int i = 0; i < rowCount; i++) {
                            setParameters(ps, insertableCols);
                            ps.addBatch();
                            batchCount++;

                            if (batchCount >= batchSize) {
                                int[] counts = ps.executeBatch();
                                totalRef[0] += sumArray(counts);

                                if (hasAutoIncrPk) {
                                    collectGeneratedKeys(ps, cachedPks);
                                }

                                conn.commit();
                                batchCount = 0;

                                if (totalRef[0] % 5000 == 0) {
                                    progressCallback.accept(String.format(
                                            "[%s] Inserted %,d / %,d rows",
                                            table.getTableName(), totalRef[0], rowCount));
                                }
                            }
                        }

                        // Flush ostatka
                        if (batchCount > 0) {
                            int[] counts = ps.executeBatch();
                            totalRef[0] += sumArray(counts);
                            if (hasAutoIncrPk) collectGeneratedKeys(ps, cachedPks);
                            conn.commit();
                        }

                    } finally {
                        enableFkChecks(conn);
                        conn.setAutoCommit(true);
                    }

                    // Ako nema auto-inc PK, dohvati PK-ove SELECT-om
                    if (!hasAutoIncrPk) {
                        List<String> pkCols = table.getPrimaryKeys().stream()
                                .map(Table.Column::getName).toList();
                        if (!pkCols.isEmpty()) {
                            fetchPks(conn, pkCols.get(0), cachedPks);
                        }
                    }
                });
            });
        } catch (Exception e) {
            log.error("[{}] Failed: {}", table.getTableName(), e.getMessage(), e);
            result.fail(e.getMessage());
            progressCallback.accept(String.format("[%s] FAILED: %s",
                    table.getTableName(), e.getMessage()));
            return result;
        }

        // Registruje PK-ove u DataGenerator kes za tabele koje FK-om zavise od ove
        generator.cacheKeys(table.getTableName(),
                cachedPks.isEmpty() ? generateFallbackKeys(totalRef[0]) : cachedPks);

        result.complete(totalRef[0]);
        log.info("[{}] Done: {} rows in {} ms ({} rows/s)",
                table.getTableName(), totalRef[0],
                result.getDurationMS(),
                String.format("%.0f", result.getRowsPerSecond()));
        progressCallback.accept(String.format("[%s] ✓ Done: %,d rows in %,d ms",
                table.getTableName(), totalRef[0], result.getDurationMS()));
        return result;
    }

    private String buildInsertSql(List<Table.Column> cols) {
        boolean useMysqlQuotes = (dbType == MyConnection.DatabaseType.MYSQL);

        String colList = cols.stream()
                .map(c -> useMysqlQuotes ? "`" + c.getName() + "`" : "\"" + c.getName() + "\"")
                .collect(Collectors.joining(", "));

        String placeholders = cols.stream().map(c -> "?")
                .collect(Collectors.joining(", "));

        String tblName = useMysqlQuotes
                ? "`" + table.getTableName() + "`"
                : "\"" + table.getTableName() + "\"";

        return "INSERT INTO " + tblName + " (" + colList + ") VALUES (" + placeholders + ")";
    }

    /** Postavlja parametre u PreparedStatement za jedan red. */
    private void setParameters(PreparedStatement ps,
                               List<Table.Column> cols) throws SQLException {
        int idx = 1;
        for (Table.Column col : cols) {
            Object val = generator.generateValue(
                    col.getName(),
                    col.getDataType(),
                    col.getMaxLength(),
                    col.isForeignKey() ? col.getReferencedTable() : null
            );
            ps.setObject(idx++, val);
        }
    }

    private void disableFkChecks(Connection conn) throws SQLException {
        switch (dbType) {
            case MYSQL      -> execute(conn, "SET FOREIGN_KEY_CHECKS=0");
            case POSTGRESQL -> execute(conn, "ALTER TABLE \"" + table.getTableName() + "\" DISABLE TRIGGER ALL");
        }
    }

    private void enableFkChecks(Connection conn) throws SQLException {
        switch (dbType) {
            case MYSQL      -> execute(conn, "SET FOREIGN_KEY_CHECKS=1");
            case POSTGRESQL -> execute(conn, "ALTER TABLE \"" + table.getTableName() + "\" ENABLE TRIGGER ALL");
        }
    }

    private void execute(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement()) { st.execute(sql); }
    }

    private void collectGeneratedKeys(PreparedStatement ps, List<Object> out) {
        try (ResultSet keys = ps.getGeneratedKeys()) {
            while (keys.next()) out.add(keys.getObject(1));
        } catch (SQLException e) {
            log.warn("[{}] Could not collect generated keys: {}", table.getTableName(), e.getMessage());
        }
    }

    private void fetchPks(Connection conn, String pkCol, List<Object> out) {
        boolean mysql = (dbType == MyConnection.DatabaseType.MYSQL);
        String q = mysql
                ? "SELECT `" + pkCol + "` FROM `" + table.getTableName() + "` LIMIT 50000"
                : "SELECT \"" + pkCol + "\" FROM \"" + table.getTableName() + "\" LIMIT 50000";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(q)) {
            while (rs.next())
                out.add(rs.getObject(1));
        } catch (SQLException e) {
            log.warn("[{}] Could not fetch PKs: {}", table.getTableName(), e.getMessage());
        }
    }

    private long sumArray(int[] arr) {
        long sum = 0;
        for (int v : arr) {
            if(v >= 0) sum += v;
            else if (v == Statement.SUCCESS_NO_INFO) sum++; // driver ne prijavljuje broj
        }
        return sum;
    }

    private List<Object> generateFallbackKeys(long count) {
        List<Object> keys = new ArrayList<>();
        for (long i = 1; i <= Math.min(count, 50_000); i++) keys.add(i);
        return keys;
    }
}