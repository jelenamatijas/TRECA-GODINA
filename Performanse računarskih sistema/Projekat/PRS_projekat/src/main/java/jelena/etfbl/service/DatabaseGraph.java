package jelena.etfbl.service;

import jelena.etfbl.database.Table;
import jelena.etfbl.database.MyConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.*;

public class DatabaseGraph {
    private static Logger log = LoggerFactory.getLogger(DatabaseGraph.class);
    private Connection connection;
    private String schema;
    private MyConnection.DatabaseType dbType;

    public DatabaseGraph(Connection connection, MyConnection config){
        this.connection = connection;
        this.schema = config.getDatabase();
        this.dbType = config.getDbType();
    }

    public Map<String, Table> analyze() throws SQLException{
        log.info("Analyzing graph " + schema + " on " + dbType);
        Map<String, Table> tables = new LinkedHashMap<>();
        DatabaseMetaData meta = connection.getMetaData();

        String catalogPattern = null;
        String schemaPattern = null;

        switch (dbType){
            case MYSQL -> catalogPattern = schema;
            case POSTGRESQL -> schemaPattern = "public";
        }

        List<String> tableNames = new ArrayList<>();
        try(ResultSet rs = meta.getTables(catalogPattern, schemaPattern, "%", new String[]{"TABLE"})){
            while(rs.next()){
                String name = rs.getString("TABLE_NAME");
                tableNames.add(name);
                tables.put(name.toLowerCase(), new Table(name.toLowerCase())); // Koristimo mala slova kao kljuceve
            }
        }
        log.info("Found " + tableNames.size() + " tables: " + tableNames);

        for(Table t : tables.values()){
            String tableName = t.getTableName();

            Set<String> pks = getPrimaryKeys(meta, catalogPattern, schemaPattern, tableName);
            Map<String, String[]> fks = getForeignKeys(meta, catalogPattern, schemaPattern, tableName);

            try(ResultSet cols = meta.getColumns(catalogPattern, schemaPattern, tableName, "%")){
                while(cols.next()){
                    String colName = cols.getString("COLUMN_NAME");
                    String typeName = cols.getString("TYPE_NAME");
                    int colSize = cols.getInt("COLUMN_SIZE");
                    boolean nullable = cols.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                    boolean autoInc = "YES".equalsIgnoreCase(cols.getString("IS_AUTOINCREMENT"));

                    Table.Column ci = new Table.Column(colName, typeName, colSize, nullable, autoInc, pks.contains(colName));

                    if(fks.containsKey(colName)){
                        // Pretvara u mala slova da bi se poklopilo sa kljucevima u mapi 'tables'
                        ci.setReferencedTable(fks.get(colName)[0].toLowerCase());
                        ci.setReferencedColumn(fks.get(colName)[1].toLowerCase());
                    }
                    t.addColumn(ci);
                }
            }
            log.debug("Table '{}': {} columns, depends on: {}",
                    tableName, t.getColumns().size(), t.getDependencies());

        }
        return tables;
    }

    private Set<String> getPrimaryKeys(DatabaseMetaData meta, String catalog, String schema, String table) throws SQLException{
        Set<String> pks = new HashSet<>();
        try(ResultSet rs = meta.getPrimaryKeys(catalog, schema, table)){
            while(rs.next()){
                pks.add(rs.getString("COLUMN_NAME"));
            }
        }
        return pks;
    }

    private Map<String, String[]> getForeignKeys(DatabaseMetaData meta, String catalog, String schema, String table)throws SQLException{
        Map<String, String[]> fks = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        try(ResultSet rs = meta.getImportedKeys(null, null, table)){
            while(rs.next()){
                String fkCol = rs.getString("FKCOLUMN_NAME");
                String refTable = rs.getString("PKTABLE_NAME");
                String refCol = rs.getString("PKCOLUMN_NAME");

                if (fkCol != null && refTable != null) {
                    fks.put(fkCol, new String[]{refTable, refCol});
                }
            }
        }
        return fks;
    }
}