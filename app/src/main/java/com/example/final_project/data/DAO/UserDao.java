package com.example.final_project.data.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.final_project.data.model.Entity.UserEntity;
import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    UserEntity getUserById(int userId);

    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();
} 