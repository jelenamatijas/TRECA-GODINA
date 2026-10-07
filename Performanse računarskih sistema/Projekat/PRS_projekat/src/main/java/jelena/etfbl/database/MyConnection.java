package jelena.etfbl.database;

public class MyConnection {
    public enum DatabaseType{
        MYSQL(
                "MySQL",
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://",
                "org.hibernate.dialect.MySQLDialect",
                3306,
                false
        ),
        POSTGRESQL(
                "PostgreSQL",
                "org.postgresql.Driver",
                "jdbc:postgresql://",
                "org.hibernate.dialect.PostgreSQLDialect",
                5432,
                false
        );
        private String displayName;
        private String driverClass;
        private String urlPRefix;
        private String hibernateDialect;
        private int defaultPort;
        private boolean fileBasedDb;

        DatabaseType(String displayName, String driverClass, String urlPRefix, String hibernateDialect, int defaultPort, boolean fileBasedDb){
            this.displayName = displayName;
            this.driverClass = driverClass;
            this.urlPRefix = urlPRefix;
            this.hibernateDialect = hibernateDialect;
            this.defaultPort = defaultPort;
            this.fileBasedDb = fileBasedDb;
        }

        public String getDisplayName(){
            return displayName;
        }

        public String getDriverClass() {
            return driverClass;
        }

        public String getUrlPRefix(){
            return urlPRefix;
        }
        public String getHibernateDialect(){
            return hibernateDialect;
        }

        public int getDefaultPort(){
            return defaultPort;
        }

        public boolean getFileBasedDb(){
            return fileBasedDb;
        }
    }

    private DatabaseType dbType;
    private String host;
    private int port;
    private String username;
    private String database;
    private String password;
    private int rowsPerTable;
    private int threadPoolSize;
    private int batchSize;

    public MyConnection(){
        dbType = DatabaseType.MYSQL;
        host = "localhost";
        port = 3306;
        database = "dbtest";
        username = "root";
        password = "";
        rowsPerTable = 10_000;
        threadPoolSize = 4;
        batchSize = 500;
    }

    public String buildURL(){
        switch(dbType){
            case MYSQL:
                return DatabaseType.MYSQL.urlPRefix + host + ":" + port + "/" + database
                        + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                        + "&rewriteBatchedStatements=true";
            case POSTGRESQL:
                return dbType.getUrlPRefix() + host + ":" + port + "/" + database
                        + "?reWriteBatchedInserts=true";
            default:
                throw new IllegalStateException("Nepoznat tip baze: " + dbType);
        }
    }

    public DatabaseType getDbType(){
        return dbType;
    }

    public void setDbType(DatabaseType dbType){
        this.dbType = dbType;
        this.port = dbType.getDefaultPort();
    }

    public String getHost(){
        return host;
    }

    public void setHost(String h){
        host = h;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int p){
        port = p;
    }

    public String getDatabase(){
        return database;
    }

    public void setDatabase(String db){
        database = db;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String u){
        username = u;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String p){
        password = p;
    }

    public int getRowsPerTable(){
        return rowsPerTable;
    }

    public void setRowsPerTable(int r){
        rowsPerTable = r;
    }

    public int getThreadPoolSize(){
        return threadPoolSize;
    }

    public void setThreadPoolSize(int t){
        threadPoolSize = t;
    }

    public int getBatchSize(){
        return batchSize;
    }

    public void setBatchSize(int b){
        batchSize = b;
    }
}
