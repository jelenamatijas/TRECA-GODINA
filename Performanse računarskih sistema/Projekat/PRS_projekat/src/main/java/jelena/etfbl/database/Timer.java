package jelena.etfbl.database;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Timer {
    private static DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private String tableName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long rowsInserted;
    private String error;
    private boolean success;

    public Timer(String tableName){
        this.tableName = tableName;
        this.startTime = LocalDateTime.now();
    }

    public void complete(long rowsInserted){
        this.endTime = LocalDateTime.now();
        this.rowsInserted = rowsInserted;
        this.success = true;
    }

    public void fail(String error){
        this.endTime = LocalDateTime.now();
        this.error = error;
        this.success = false;
    }

    public String getTableName(){
        return tableName;
    }

    public LocalDateTime getStartTime(){
        return startTime;
    }

    public LocalDateTime getEndTime(){
        return endTime;
    }

    public long getRowsInserted(){
        return  rowsInserted;
    }

    public String getError(){
        return  error;
    }

    public boolean getSuccess(){
        return  success;
    }

    public long getDurationMS(){
        if(endTime==null){
            return -1;
        }
        return Duration.between(startTime, endTime).toMillis();
    }

    public double getRowsPerSecond(){
        long ms = getDurationMS();
        if(ms<0){
            return 0;
        }
        return rowsInserted/(ms/1000.0);
    }

    public boolean isSuccess(){
        return  success;
    }

    @Override
    public String toString(){
        if(success){
            return String.format(
                    "Table: %-30s | Start: %s | End: %s | Duration: %,d ms | Rows: %,d | Rate: %.0f rows/s",
                    tableName,
                    startTime.format(FMT),
                    endTime.format(FMT),
                    getDurationMS(),
                    rowsInserted,
                    getRowsPerSecond()
            );
        }else{
            return String.format("Table: %-30s | FAILED | Error: %s", tableName, error);
        }
    }
}
