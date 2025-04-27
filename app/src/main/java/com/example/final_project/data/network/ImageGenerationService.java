package com.example.final_project.data.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageGenerationService {

    // API 请求 URL 和固定参数
    private static final String API_URL = "https://modelslab.com/api/v6/images/text2img";
    private static final String API_KEY = "mVoGu99mawr6lj9lxHe0VFO6JF3sjYlUXoyABzvL1QoSJtPvEON714TOgokt";

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间为 30 秒
            .readTimeout(30, TimeUnit.SECONDS)    // 设置读取超时时间为 30 秒
            .writeTimeout(30, TimeUnit.SECONDS)   // 设置写入超时时间为 30 秒
            .build();
    public interface ImageGenerationCallback {
        void onSuccess(String imagePath, String fileName); // 图片保存路径和文件名
        void onFailure(String errorMessage);
    }

    // 发送 POST 请求生成图片
    public void generateImage(Context context, String prompt, ImageGenerationCallback callback) {
        // 创建请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("key", API_KEY)
                .add("model_id", "flux")
                .add("prompt", prompt)
                .add("width", "240")
                .add("height", "320")
                .add("samples", "1")
                .add("num_inference_steps", "31")
                .add("safety_checker", "no")
                .add("enhance_prompt", "yes")
                .add("seed", "null")
                .add("guidance_scale", "7.5")
                .add("tomesd", "yes")
                .add("clip_skip", "2")
                .add("vae", "null")
                .add("webhook", "null")
                .add("track_id", "null")
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Accept", "*/*")
                .addHeader("Host", "modelslab.com")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
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
                        // 提取图片 URL
                        String responseBody = response.body().string();
//                        String imageUrl = extractImageUrl(responseBody);
                        //静态调试
                        String imageUrl="https://modelslab-bom.s3.amazonaws.com/generations/51f52058-bcff-4e73-9346-ecb6e0042525-0.jpg";

                        // 动态生成文件名
                        String fileName = "generated_image_" + System.currentTimeMillis() + ".jpg";

                        // 下载图片并保存到本地
                        String imagePath = downloadAndSaveImage(context, imageUrl, fileName);

                        // 回调成功，返回图片路径和文件名
                        callback.onSuccess(imagePath, fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure("Error parsing response: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Failed to generate image. HTTP Status: " + response.code());
                }
            }
        });
    }

    // 从响应中提取图片 URL
    private String extractImageUrl(String responseBody) throws Exception {
        JSONObject jsonResponse = new JSONObject(responseBody);
        return jsonResponse.getJSONArray("output").getString(0); // 获取第一个图片 URL
    }

    // 下载图片并保存到本地
    private String downloadAndSaveImage(Context context, String imageUrl, String fileName) throws Exception {
        InputStream inputStream = new URL(imageUrl).openStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);



        //静态调试
        // 保存图片到本地
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "GeneratedImages");
        if (!directory.exists()) {
            directory.mkdirs(); // 创建目录
        }

        File imageFile = new File(directory, fileName);
        FileOutputStream outputStream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // 保存为 JPEG 格式
        outputStream.close();

        return imageFile.getAbsolutePath(); // 返回图片完整路径
    }
}