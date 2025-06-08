package com.example.final_project.utils;

import android.text.TextUtils;
import java.util.regex.Pattern;

public class PasswordValidator {
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\u4e00-\u9fa5]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9\\p{Punct}]+$");

    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }

        // 检查密码长度是否在6-18之间
        if (password.length() < 6 || password.length() > 18) {
            return false;
        }

        // 检查是否只包含合法字符（英文、数字、英文标点符号）
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return false;
        }

        return true;
    }

    public static boolean isValidUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            return false;
        }

        // 检查是否只包含中英文字符
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return false;
        }

        // 计算中英文字符长度
        int totalLength = 0;
        for (char c : username.toCharArray()) {
            if (CHINESE_PATTERN.matcher(String.valueOf(c)).find()) {
                totalLength += 2; // 中文字符算2个长度
            } else {
                totalLength += 1; // 英文字符算1个长度
            }
        }

        // 检查总长度是否超过14
        return totalLength <= 14;
    }

    public static String getUsernameLengthError(String username) {
        if (TextUtils.isEmpty(username)) {
            return "用户名不能为空";
        }

        // 检查是否只包含中英文字符
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return "用户名只能包含中英文字符";
        }

        // 计算中英文字符长度
        int totalLength = 0;
        for (char c : username.toCharArray()) {
            if (CHINESE_PATTERN.matcher(String.valueOf(c)).find()) {
                totalLength += 2; // 中文字符算2个长度
            } else {
                totalLength += 1; // 英文字符算1个长度
            }
        }

        // 检查总长度是否超过14
        if (totalLength > 14) {
            return "用户名不能超过14个英文字符（7个中文字符）";
        }

        return null;
    }

    public static String getPasswordError(String password) {
        if (TextUtils.isEmpty(password)) {
            return "密码不能为空";
        }

        if (password.length() < 6 || password.length() > 18) {
            return "密码长度必须在6-18个字符之间";
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return "密码只能包含英文、数字和英文标点符号";
        }

        return null;
    }
} 