package com.example.final_project.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class RolePreferences {

    private static final String PREF_NAME = "role_prefs";
    private static final String KEY_ROLE_NAME = "role_name";
    private static final String KEY_IMAGE_PATH = "image_path";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_PERSONALITY = "personality";
    private static final String KEY_APPEARANCE = "appearance";

    /**
     * 保存当前选择角色的所有信息
     */
    public static void saveRoleInfo(Context context, String name, String imagePath, String gender, String personality, String appearance) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ROLE_NAME, name);
        editor.putString(KEY_IMAGE_PATH, imagePath);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_PERSONALITY, personality);
        editor.putString(KEY_APPEARANCE, appearance);
        editor.apply();
    }

    /**
     * 获取角色名称
     */
    public static String getRoleName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ROLE_NAME, null);
    }

    /**
     * 获取角色图片路径
     */
    public static String getImagePath(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_IMAGE_PATH, null);
    }
    
    /**
     * 获取角色性别
     */
    public static String getGender(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_GENDER, null);
    }
    
    /**
     * 获取角色性格
     */
    public static String getPersonality(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PERSONALITY, null);
    }

    /**
     * 获取角色外观
     */
    public static String getAppearance(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_APPEARANCE, null);
    }


    /**
     * 清除所有角色相关的缓存信息
     */
    public static void clearRoleInfo(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
} 