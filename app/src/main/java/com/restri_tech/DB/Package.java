package com.restri_tech.DB;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "app")
public class Package {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "rtime")
    private long rtime;

    @ColumnInfo(name = "ftime")
    private long ftime;

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public long getRtime() {
        return rtime;
    }

    public void setRtime(long rtime) {
        this.rtime = rtime;
    }

    public long getFtime() {
        return ftime;
    }

    public void setFtime(long ftime) {
        this.ftime = ftime;
    }
}
