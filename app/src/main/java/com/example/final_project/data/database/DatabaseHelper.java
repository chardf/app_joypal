package com.example.final_project.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.final_project.data.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chat_history.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CHAT_MESSAGES = "chat_messages";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CHARACTER_NAME = "character_name";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_IS_USER_MESSAGE = "is_user_message";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CHAT_MESSAGES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CHARACTER_NAME + " TEXT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_IS_USER_MESSAGE + " INTEGER, " +
                COLUMN_TIMESTAMP + " INTEGER" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
        onCreate(db);
    }

    public void saveChatMessage(ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHARACTER_NAME, message.getCharacterName());
        values.put(COLUMN_MESSAGE, message.getMessage());
        values.put(COLUMN_IS_USER_MESSAGE, message.isUserMessage() ? 1 : 0);
        values.put(COLUMN_TIMESTAMP, message.getTimestamp());

        try {
            db.insert(TABLE_CHAT_MESSAGES, null, values);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error saving chat message", e);
        }
    }

    public List<ChatMessage> getChatHistory(String characterName) {
        List<ChatMessage> chatHistory = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
            COLUMN_CHARACTER_NAME,
            COLUMN_MESSAGE,
            COLUMN_IS_USER_MESSAGE,
            COLUMN_TIMESTAMP
        };
        String selection = COLUMN_CHARACTER_NAME + " = ?";
        String[] selectionArgs = {characterName};
        String orderBy = COLUMN_TIMESTAMP + " ASC";

        try (Cursor cursor = db.query(TABLE_CHAT_MESSAGES, columns, selection, selectionArgs, null, null, orderBy)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
                    boolean isUserMessage = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER_MESSAGE)) == 1;
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                    chatHistory.add(new ChatMessage(characterName, message, isUserMessage, timestamp));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting chat history", e);
        }

        return chatHistory;
    }

    public void clearChatHistory(String characterName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_CHARACTER_NAME + " = ?";
        String[] whereArgs = {characterName};
        try {
            db.delete(TABLE_CHAT_MESSAGES, whereClause, whereArgs);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error clearing chat history", e);
        }
    }
} 