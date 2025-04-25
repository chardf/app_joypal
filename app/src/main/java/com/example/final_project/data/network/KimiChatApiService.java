package com.example.final_project.data.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;
public class KimiChatApiService {

    private static final String API_URL = "https://api.moonshot.cn/v1/chat/completions";
    private static final String API_KEY = "sk-KoHw8fj2KltOEEVuszta3ISUq9X9OSbMjQGEuB74ZU80HzEy";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间为 30 秒
            .readTimeout(30, TimeUnit.SECONDS)    // 设置读取超时时间为 30 秒
            .writeTimeout(30, TimeUnit.SECONDS)   // 设置写入超时时间为 30 秒
            .build();
    /**
     * 发送用户消息到 Kimi Chat API
     */
    public static void sendMessage(String userMessage, KimiChatCallback callback) {
        new Thread(() -> {
            try {
                // 创建请求体
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", "moonshot-v1-8k");

                JSONArray messages = new JSONArray();
                messages.put(new JSONObject()
                        .put("role", "system")
                        .put("content", "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。"));
                messages.put(new JSONObject()
                        .put("role", "user")
                        .put("content", userMessage));

                requestBody.put("messages", messages);
                requestBody.put("temperature", 0.3);

                // 将请求体转换为字符串并创建 RequestBody
                RequestBody body = RequestBody.create(requestBody.toString(), JSON);

                // 创建请求
                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .build();

                // 异步调用
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailure(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            try {
                                // 解析响应 JSON
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                String reply = jsonResponse
                                        .getJSONArray("choices")
                                        .getJSONObject(0)
                                        .getJSONObject("message")
                                        .getString("content");

                                // 成功回调
                                callback.onSuccess(reply);
                            } catch (Exception e) {
                                callback.onFailure("Failed to parse response: " + e.getMessage());
                            }
                        } else {
                            // 错误回调
                            callback.onFailure("Error: " + response.code() + " - " + response.message());
                        }
                    }
                });

            } catch (Exception e) {
                callback.onFailure(e.getMessage());
            }
        }).start();
    }

    /**
     * 回调接口
     */
    public interface KimiChatCallback {
        void onSuccess(String reply);

        void onFailure(String errorMessage);
    }
}