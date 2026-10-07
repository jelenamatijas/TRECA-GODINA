package jelena.etfbl.service;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    private static final Logger log = LoggerFactory.getLogger(DataGenerator.class);

    private final Faker faker = new Faker(Locale.ENGLISH);
    private final Random rng = ThreadLocalRandom.current();

    private final ConcurrentHashMap<String, List<Object>> insertedPkCache = new ConcurrentHashMap<>();

    public void cacheKeys(String table, List<Object> keys) {
        insertedPkCache.put(table.toLowerCase(), new ArrayList<>(keys));
    }

    public Object getRandomFkValue(String referencedTable) {
        List<Object> keys = insertedPkCache.get(referencedTable.toLowerCase());
        if (keys == null || keys.isEmpty()) {
            log.warn("Nema keširanih PK za '{}', koristim 1 kao fallback", referencedTable);
            return 1;
        }
        return keys.get(rng.nextInt(keys.size()));
    }

    public Object generateValue(String columnName, String dataType, int maxLen, String referencedTable) {
        if (referencedTable != null) {
            return getRandomFkValue(referencedTable);
        }

        String type = dataType.toUpperCase();

        if (type.contains("INT") || type.equals("BIGINT") || type.equals("SMALLINT") || type.equals("TINYINT")) {
            return rng.nextInt(100_000);
        }

        if (type.contains("DECIMAL") || type.contains("NUMERIC") || type.contains("FLOAT") || type.contains("DOUBLE") || type.equals("REAL")) {
            return BigDecimal.valueOf(rng.nextDouble() * 1000).setScale(2, RoundingMode.HALF_UP);
        }

        if (type.equals("BIT") || type.equals("BOOLEAN") || type.equals("BOOL")) {
            return rng.nextBoolean();
        }

        if (type.equals("DATE")) {
            return Date.valueOf(LocalDate.now().minusDays(rng.nextInt(3650)));
        }
        if (type.equals("DATETIME") || type.equals("TIMESTAMP")) {
            return Timestamp.valueOf(LocalDateTime.now().minusDays(rng.nextInt(3650)));
        }

        if (type.contains("CHAR") || type.contains("TEXT") || type.contains("VARCHAR")) {
            String raw = faker.lorem().word() + "_" + rng.nextInt(999999);
            if (maxLen > 0 && raw.length() > maxLen) {
                return raw.substring(0, maxLen);
            }
            return raw;
        }

        return "DATA_" + rng.nextInt(10000);
    }
}