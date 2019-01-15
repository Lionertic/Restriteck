package com.restri_tech.DB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MyDao {

    @Insert
    void addApps(Package packages);

    @Query("select * from app")
    List<Package> getApps();

    @Query("select rtime from app where name = :app")
    long getRTime(String app);

    @Update
    void setTime(Package p);

    @Query("select ftime from app where name = :app")
    long getFTime(String app);

    @Delete
    void delete(Package p);
}

