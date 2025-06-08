package com.example.final_project.data.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthApiService {
    private static final String TAG = "AuthApiService";
    private static final String BASE_URL = "http://joypal.natapp1.cc";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";
    private static final String DEFAULT_ROLE = "user";

    public interface AuthCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }

    // 保存认证信息
    public static void saveAuthInfo(Context context, String token, String username, int userId, String role) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USERNAME, username);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    // 获取token
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    // 获取用户名
    public static String getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USERNAME, null);
    }

    // 获取用户ID
    public static int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_USER_ID, -1);
    }

    // 获取用户角色
    public static String getRole(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ROLE, null);
    }

    // 清除认证信息
    public static void clearAuthInfo(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    // 注册
    public static void register(String username, String password, AuthCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("role", DEFAULT_ROLE);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/register")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    callback.onFailure("网络请求失败: " + e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        callback.onSuccess(responseBody);
                    } else if (response.code() == 422) {
                        callback.onFailure("密码必须包含至少两种类型：字母、数字或符号");
                    } else if (response.code() == 500) {
                        callback.onFailure("用户信息已存在");
                    } else if (response.code() == 405) {
                        callback.onFailure("请求格式错误");
                    } else {
                        callback.onFailure("注册失败: " + responseBody);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "注册请求异常", e);
            callback.onFailure("注册请求异常: " + e.getMessage());
        }
    }

    // 登录
    public static void login(String username, String password, AuthCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("role", DEFAULT_ROLE);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/login")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    callback.onFailure("网络请求失败: " + e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if (jsonResponse.has("error")) {
                                callback.onFailure(jsonResponse.getString("error"));
                            } else {
                                callback.onSuccess(responseBody);
                            }
                        } catch (Exception e) {
                            callback.onFailure("解析响应失败: " + e.getMessage());
                        }
                    } else if (response.code() == 422) {
                        callback.onFailure("密码不符合规则");
                    } else if (response.code() == 405) {
                        callback.onFailure("请求格式错误");
                    } else {
                        callback.onFailure("登录失败: " + responseBody);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "登录请求异常", e);
            callback.onFailure("登录请求异常: " + e.getMessage());
        }
    }

    // 修改密码
    public static void changePassword(Context context, String username, String oldPassword, String newPassword, AuthCallback callback) {
        try {
            String token = getToken(context);
            if (token == null) {
                callback.onFailure("未登录");
                return;
            }

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("old_password", oldPassword);
            jsonBody.put("new_password", newPassword);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/change-password")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    callback.onFailure("网络请求失败: " + e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        callback.onSuccess(responseBody);
                    } else if (response.code() == 401) {
                        callback.onFailure("登录已过期，请重新登录");
                    } else if (response.code() == 400) {
                        callback.onFailure("旧密码错误");
                    } else {
                        callback.onFailure("修改密码失败: " + responseBody);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "修改密码请求异常", e);
            callback.onFailure("修改密码请求异常: " + e.getMessage());
        }
    }

    // 登出
    public static void logout(Context context) {
        clearAuthInfo(context);
    }
} 