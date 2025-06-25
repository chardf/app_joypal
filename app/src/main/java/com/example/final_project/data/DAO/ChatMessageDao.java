package com.example.final_project.data.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.final_project.data.model.Entity.ChatMessageEntity;
import java.util.List;

@Dao
public interface ChatMessageDao {

    @Insert
    void insertMessage(ChatMessageEntity message);

    @Query("SELECT * FROM chat_messages WHERE user_id = :userId AND role_id = :roleId ORDER BY timestamp ASC")
    List<ChatMessageEntity> getChatHistory(int userId, int roleId);
} 