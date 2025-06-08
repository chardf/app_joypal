package com.example.final_project.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.final_project.data.DAO.ImageRoleDao;
import com.example.final_project.data.model.Entity.ImageRoleEntity;

@Database(entities = {ImageRoleEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "joypal_database";
    private static volatile AppDatabase instance;

    public abstract ImageRoleDao imageRoleDao();

    public static synchronized AppDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME
            ).build();
        }
        return instance;
    }
} 