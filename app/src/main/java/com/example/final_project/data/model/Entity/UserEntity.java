package com.example.final_project.data.model.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey
    @NonNull
    private int userId;

    private String username;

    // Constructors
    public UserEntity(@NonNull int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    // Getters and Setters
    @NonNull
    public int getUserId() {
        return userId;
    }

    public void setUserId(@NonNull int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
} 