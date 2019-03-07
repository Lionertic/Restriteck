package com.restri_tech.DB;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

