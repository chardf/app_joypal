package com.example.final_project.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.final_project.data.DAO.ImageRoleDao;
import com.example.final_project.data.model.Entity.ImageRoleEntity;

@Database(entities = {ImageRoleEntity.class}, version = 2, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ImageRoleDao imageRoleDao();

    // 数据库迁移策略
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 添加新列
            database.execSQL("ALTER TABLE image_role ADD COLUMN name TEXT");
            database.execSQL("ALTER TABLE image_role ADD COLUMN gender TEXT");
            database.execSQL("ALTER TABLE image_role ADD COLUMN personality TEXT");
            database.execSQL("ALTER TABLE image_role ADD COLUMN appearance TEXT");
        }
    };

    // 获取数据库实例
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .addMigrations(MIGRATION_1_2) // 添加迁移策略
                            .fallbackToDestructiveMigration() // 如果迁移失败，重建数据库
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
