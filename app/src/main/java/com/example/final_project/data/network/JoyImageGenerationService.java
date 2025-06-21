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
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public interface ImageGenerationCallback {
        void onSuccess(String characterName, String imagePath);
        void onFailure(String errorMessage);
    }

    public void JoygenerateImage(Context context, String inputString, ImageGenerationCallback callback) {
        try {
            JSONObject requestBody = convertToJson(inputString);
            RequestBody body = RequestBody.create(requestBody.toString(), JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

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
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);

                            String characterName = jsonResponse.getString("character_name");
                            String imageUrl = jsonResponse.getString("image_url");

                            String localImagePath = downloadAndSaveImage(context, imageUrl, characterName);
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

    private JSONObject convertToJson(String inputString) throws Exception {
        JSONObject requestBody = new JSONObject();
        JSONArray descriptionArray = new JSONArray();

        // 拆分输入字符串
        String[] parts = inputString.split(", ");
        for (String part : parts) {
            String[] keyValue = part.split(": ", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                
                // 根据不同的键创建对应的JSON对象
                JSONObject descriptionItem = new JSONObject();
                switch (key) {
                    case "Name":
                        descriptionItem.put("Name", value);
                        break;
                    case "Gender":
                        descriptionItem.put("Gender", value);
                        break;
                    case "Personality":
                        descriptionItem.put("Personality", value);
                        break;
                    case "Look":
                        descriptionItem.put("Appearance", value);
                        break;
                }
                descriptionArray.put(descriptionItem);
            }
        }

        requestBody.put("description", descriptionArray);
        return requestBody;
    }

    public static String downloadAndSaveImage(Context context, String imageUrl, String fileName) throws Exception {
        InputStream inputStream = new URL(imageUrl).openStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "GeneratedImages");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File imageFile = new File(directory, fileName + ".jpg");
        FileOutputStream outputStream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.close();

        return imageFile.getAbsolutePath();
    }
}