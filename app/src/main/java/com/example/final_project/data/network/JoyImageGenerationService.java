package com.example.final_project.data.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JoyImageGenerationService {

    // API 请求 URL 和固定参数
    private static final String API_URL = "http://joypal.natapp1.cc/generate/text2img";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // 设置连接超时时间为 30 秒
            .readTimeout(60, TimeUnit.SECONDS)    // 设置读取超时时间为 30 秒
            .writeTimeout(60, TimeUnit.SECONDS)   // 设置写入超时时间为 30 秒
            .build();

    public interface ImageGenerationCallback {
        void onSuccess(String characterName, String imagePath); // 返回角色名称和图片路径
        void onFailure(String errorMessage);
    }

    // 发送 POST 请求生成图片
    public void JoygenerateImage(Context context, String inputString, ImageGenerationCallback callback) {
        try {
            // 将输入字符串转换为符合 API 要求的 JSON 格式
            JSONObject requestBody = convertToJson(inputString);

            RequestBody body = RequestBody.create(requestBody.toString(), JSON);

            // 创建请求
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            // 异步执行请求
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    callback.onFailure("Error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            // 解析响应 JSON
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);

                            String characterName = jsonResponse.getString("character_name");
                            String imagePath = jsonResponse.getString("image_path");

                            // 下载图片并保存到本地（可选）
                            String localImagePath = downloadAndSaveImage(context, imagePath, characterName);

                            // 回调成功，返回角色名称和本地图片路径
                            callback.onSuccess(characterName, localImagePath);
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.onFailure("Error parsing response: " + e.getMessage());
                        }
                    } else {
                        callback.onFailure("Failed to generate image. HTTP Status: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure("Error creating request: " + e.getMessage());
        }
    }

    // 将输入字符串转换为符合 API 要求的 JSON 格式
    private JSONObject convertToJson(String inputString) throws Exception {
        JSONObject requestBody = new JSONObject();
        JSONArray descriptionArray = new JSONArray();

        // 拆分输入字符串
        String[] parts = inputString.split(", ");
        for (String part : parts) {
            String[] keyValue = part.split(": ", 2);
            if (keyValue.length == 2) {
                JSONObject descriptionItem = new JSONObject();
                descriptionItem.put(keyValue[0].trim(), keyValue[1].trim());
                descriptionArray.put(descriptionItem);
            }
        }

        requestBody.put("description", descriptionArray);
        return requestBody;
    }

    // 下载图片并保存到本地
    private String downloadAndSaveImage(Context context, String imageUrl, String fileName) throws Exception {
        InputStream inputStream = new URL(imageUrl).openStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        // 保存图片到本地
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "GeneratedImages");
        if (!directory.exists()) {
            directory.mkdirs(); // 创建目录
        }

        File imageFile = new File(directory, fileName + ".jpg"); // 使用角色名作为文件名
        FileOutputStream outputStream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // 保存为 JPEG 格式
        outputStream.close();

        return imageFile.getAbsolutePath(); // 返回图片完整路径
    }
}