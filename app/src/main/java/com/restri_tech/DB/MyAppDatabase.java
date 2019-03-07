package com.restri_tech.DB;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Package.class}, version = 2 ,exportSchema = false)
public abstract class MyAppDatabase extends RoomDatabase {
    public abstract MyDao myDao();
}
