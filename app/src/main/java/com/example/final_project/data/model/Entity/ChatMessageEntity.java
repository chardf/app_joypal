package com.example.final_project.data.model.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_messages",
        foreignKeys = {
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "userId",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = ImageRoleEntity.class,
                        parentColumns = "id",
                        childColumns = "role_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "user_id"), @Index(value = "role_id")})
public class ChatMessageEntity {

    @PrimaryKey(autoGenerate = true)
    private int messageId;

    @ColumnInfo(name = "role_id")
    private int roleId;

    @ColumnInfo(name = "user_id")
    private int userId;

    private String message;

    private long timestamp;

    private boolean isUserMessage;

    // Constructor
    public ChatMessageEntity(int roleId, int userId, String message, long timestamp, boolean isUserMessage) {
        this.roleId = roleId;
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
        this.isUserMessage = isUserMessage;
    }

    // Getters and Setters
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }

    public void setUserMessage(boolean userMessage) {
        isUserMessage = userMessage;
    }
} 