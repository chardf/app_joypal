package com.example.final_project.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.database.Cursor;

import com.example.final_project.data.DAO.ImageRoleDao;
import com.example.final_project.data.model.Entity.ImageRoleEntity;

@Database(entities = {ImageRoleEntity.class}, version = 2, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "joypal_database";
    private static volatile AppDatabase instance;

    public abstract ImageRoleDao imageRoleDao();

    // 数据库迁移策略
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 检查列是否存在
            Cursor cursor = database.query("PRAGMA table_info(image_role)");
            boolean hasName = false;
            boolean hasGender = false;
            boolean hasPersonality = false;
            boolean hasAppearance = false;
            
            while (cursor.moveToNext()) {
                String columnName = cursor.getString(cursor.getColumnIndex("name"));
                if ("name".equals(columnName)) hasName = true;
                if ("gender".equals(columnName)) hasGender = true;
                if ("personality".equals(columnName)) hasPersonality = true;
                if ("appearance".equals(columnName)) hasAppearance = true;
            }
            cursor.close();

            // 只添加不存在的列
            if (!hasName) {
                database.execSQL("ALTER TABLE image_role ADD COLUMN name TEXT");
            }
            if (!hasGender) {
                database.execSQL("ALTER TABLE image_role ADD COLUMN gender TEXT");
            }
            if (!hasPersonality) {
                database.execSQL("ALTER TABLE image_role ADD COLUMN personality TEXT");
            }
            if (!hasAppearance) {
                database.execSQL("ALTER TABLE image_role ADD COLUMN appearance TEXT");
            }
        }
    };

    public static synchronized AppDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME
            )
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
} 