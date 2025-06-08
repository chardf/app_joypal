package com.example.final_project.data.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JoyTextGenerationService {
    private static final String TAG = "JoyTextGenerationService";
    private static final String BASE_URL = "http://joypal.natapp1.cc";
    private static final String GENERATE_TEXT_ENDPOINT = "/generate-text";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public interface TextGenerationCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }

    public static void generateText(String message, String name, String gender, 
                                  String personality, String appearance, 
                                  TextGenerationCallback callback) {
        try {
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            
            // 添加对话
            JSONArray dialogues = new JSONArray();
            dialogues.put(new JSONObject().put("Input", message));
            requestBody.put("dialogues", dialogues);

            // 添加角色描述
            JSONArray description = new JSONArray();
            description.put(new JSONObject().put("Name", name));
            description.put(new JSONObject().put("Gender", gender));
            description.put(new JSONObject().put("Personality", personality));
            description.put(new JSONObject().put("Appearance", appearance));
            requestBody.put("description", description);

            // 打印请求体用于调试
            Log.d(TAG, "Request body: " + requestBody.toString());

            // 创建请求
            String url = BASE_URL + GENERATE_TEXT_ENDPOINT;
            Log.d(TAG, "Request URL: " + url);
            
            RequestBody body = RequestBody.create(requestBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            // 发送请求
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Network error", e);
                    callback.onFailure("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "No error body";
                        Log.e(TAG, "Server error: " + response.code() + ", Body: " + errorBody);
                        callback.onFailure("Server error: " + response.code() + ", Body: " + errorBody);
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Response body: " + responseBody);
                        
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONObject result = jsonResponse.getJSONObject("result");
                        String reply = result.getString("SampleSpeech");
                        Log.d(TAG, "Generated text: " + reply);
                        
                        callback.onSuccess(reply);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response", e);
                        callback.onFailure("Error parsing response: " + e.getMessage());
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error creating request", e);
            callback.onFailure("Error creating request: " + e.getMessage());
        }
    }
} 