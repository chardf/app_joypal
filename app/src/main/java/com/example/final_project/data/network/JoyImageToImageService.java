package com.example.final_project.data.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JoyImageToImageService {
    private static final String TAG = "JoyImg2ImgService";
    private static final String API_URL = "http://joypal.natapp1.cc/generate/img2img";

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS) // Increased timeout for image upload
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();

    public interface ImageGenerationCallback {
        void onSuccess(String characterName, String imageUrl);
        void onFailure(String errorMessage);
    }

    public void generateImageFromImage(Context context, String name, String gender, String personality, String appearance, String imageUriString, ImageGenerationCallback callback) {
        try {
            String descriptionJson = buildDescriptionJson(name, gender, personality, appearance);

            Uri imageUri = Uri.parse(imageUriString);
            String mimeType = context.getContentResolver().getType(imageUri);
            if (mimeType == null || (!mimeType.equals("image/jpeg") && !mimeType.equals("image/png"))) {
                callback.onFailure("Unsupported image type. Please select a JPG or PNG image.");
                return;
            }

            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            byte[] fileBytes = getBytesFromInputStream(inputStream);

            String filename = "reference_image." + (mimeType.equals("image/jpeg") ? "jpg" : "png");
            RequestBody fileRequestBody = RequestBody.create(fileBytes, MediaType.parse(mimeType));

            RequestBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("description", descriptionJson)
                    .addFormDataPart("character_name", "") // Per API docs, keep this empty
                    .addFormDataPart("file", filename, fileRequestBody)
                    .build();

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(multipartBody)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Network request failed", e);
                    callback.onFailure("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if (jsonResponse.has("error")) {
                                callback.onFailure(jsonResponse.getString("error"));
                                return;
                            }
                            String characterName = jsonResponse.getString("character_name");
                            String imageUrl = jsonResponse.getString("image_url");
                            callback.onSuccess(characterName, imageUrl);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to parse successful response", e);
                            callback.onFailure("Error parsing response: " + e.getMessage());
                        }
                    } else {
                        Log.e(TAG, "Server error: " + response.code() + " Body: " + responseBody);
                        callback.onFailure("Failed to generate image. Status: " + response.code());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error creating img2img request", e);
            callback.onFailure("Error creating request: " + e.getMessage());
        }
    }

    private String buildDescriptionJson(String name, String gender, String personality, String appearance) throws Exception {
        JSONArray descriptionArray = new JSONArray();
        descriptionArray.put(new JSONObject().put("Name", name));
        descriptionArray.put(new JSONObject().put("Gender", gender));
        descriptionArray.put(new JSONObject().put("Personality", personality));
        descriptionArray.put(new JSONObject().put("Appearance", appearance));
        return descriptionArray.toString();
    }

    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
} 