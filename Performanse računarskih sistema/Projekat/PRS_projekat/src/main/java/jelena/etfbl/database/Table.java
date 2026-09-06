package jelena.etfbl.database;

import java.util.*;

public class Table {
    public static class Column{
        private String name;
        private String dataType;
        private int maxLength;
        private boolean nullable;
        private boolean autoIncrement;
        private boolean primaryKey;
        private String referencedTable; // null ako nije FK
        private String referencedColumn;

        public Column(String name, String dataType, int maxLength, boolean nullable, boolean autoIncrement, boolean primaryKey){
            this.name = name;
            this.dataType = dataType;
            this.maxLength = maxLength;
            this.nullable = nullable;
            this.autoIncrement = autoIncrement;
            this.primaryKey = primaryKey;
        }

        public String getName(){
            return  name;
        }

        public String getDataType(){
            return dataType;
        }

        public int getMaxLength(){
            return maxLength;
        }

        public boolean isNullable(){
            return nullable;
        }

        public boolean isAutoIncrement(){
            return autoIncrement;
        }

        public boolean isPrimaryKey(){
            return primaryKey;
        }

        public boolean isForeignKey(){
            return referencedTable!=null;
        }
        public String getReferencedTable(){
            return referencedTable;
        }

        public String getReferencedColumn(){
            return referencedColumn;
        }

        public void setReferencedTable(String t){
            referencedTable = t;
        }

        public void setReferencedColumn(String c){
            referencedColumn = c;
        }

        @Override
        public String toString(){
            return name + " [" + dataType +
                    (isForeignKey() ? " -> " + referencedTable + "." + referencedColumn : "") + "]";
        }
    }

    private String tableName;
    private List<Column> columns = new ArrayList<>();
    private Set<String> dependencies = new LinkedHashSet<>();

    public Table(String tableName){
        this.tableName = tableName;
    }

    public String getTableName(){
        return tableName;
    }

    public List<Column> getColumns(){
        return Collections.unmodifiableList(columns);
    }

    public void addColumn(Column c){
        columns.add(c);
        if(c.isForeignKey()){
            dependencies.add(c.getReferencedTable());
        }
    }

    public Set<String> getDependencies(){
        return Collections.unmodifiableSet(dependencies);
    }

    public List<Column> getInsertableColumns(){
        return columns.stream().filter(c -> !c.isAutoIncrement()).toList();
    }

    public List<Column> getPrimaryKeys(){
        return columns.stream().filter(Column::isPrimaryKey).toList();
    }

    @Override
    public String toString(){
        return tableName + " (depends on: " + dependencies + ")";
    }
}
