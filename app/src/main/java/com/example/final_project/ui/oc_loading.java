package com.example.final_project.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.os.Environment;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.final_project.R;
import com.example.final_project.data.model.Entity.ImageRoleEntity;
import com.example.final_project.data.network.ImageGenerationService;
import com.example.final_project.database.AppDatabase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class oc_loading extends AppCompatActivity {
    private static final String TAG = "oc_loading";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String[] REQUIRED_PERMISSIONS = {
        Manifest.permission.READ_MEDIA_IMAGES
    };

    private ProgressBar progressBar;
    private TextView loadTextView;
    private String userInput;
    private String roleName;
    private Handler handler;
    private int progress = 0;
    private Runnable progressUpdater;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ImageGenerationService imageGenerationService;
    private ImageView gifImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oc_loading);

        try {
            // 检查网络连接
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "请检查网络连接", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // 初始化视图
            progressBar = findViewById(R.id.static_progress_bar);
            loadTextView = findViewById(R.id.load);
            gifImage = findViewById(R.id.gif_image);

            // 获取从 personality_design 页面传递的用户输入和角色名称
            Intent intent = getIntent();
            userInput = intent.getStringExtra("userInput");
            roleName = intent.getStringExtra("roleName");

            // 初始化 Handler 和 ImageGenerationService
            handler = new Handler();
            imageGenerationService = new ImageGenerationService();

            // 设置进度初始值
            loadTextView.setText("0%");

            // 开始模拟进度更新
            startProgressSimulation();

            // 检查权限并加载GIF
            checkPermissionsAndLoadGif();
        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage());
            Toast.makeText(this, "初始化失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void startProgressSimulation() {
        progressUpdater = new Runnable() {
            @Override
            public void run() {
                if (progress < 80) { // 模拟进度达到 80% 后停止（其余部分由图片生成完成后更新）
                    progress += 5; // 每次增加 5%
                    progressBar.setProgress(progress); // 更新进度条的值
                    loadTextView.setText(progress + "%"); // 动态更新 TextView 的文本
                    handler.postDelayed(this, 500); // 每 500ms 更新一次
                }
            }
        };
        handler.post(progressUpdater);
    }

    private void generateImage(String prompt) {
        // 调用 ImageGenerationService 开始生成图片
        imageGenerationService.generateImage(this, prompt, new ImageGenerationService.ImageGenerationCallback() {
            @Override
            public void onSuccess(String imagePath, String fileName) {
                // 图片生成成功
                runOnUiThread(() -> {
                    // 停止模拟进度并设置为 100%
                    handler.removeCallbacks(progressUpdater);
                    progressBar.setProgress(100);
                    loadTextView.setText("100%"); // 动态更新 TextView
                    // 保存到数据库
                    saveToDatabase(imagePath, roleName);

                    Toast.makeText(oc_loading.this, "Image saved successfully!", Toast.LENGTH_SHORT).show();

                    // 跳转到结果页面
                    Intent intent = new Intent(oc_loading.this, joypal_chat.class);
                    intent.putExtra("imagePath", imagePath); // 传递生成的图片路径
                    intent.putExtra("roleName", roleName); // 传递角色名称
                    startActivity(intent);

                    // 结束当前页面
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // 图片生成失败
                runOnUiThread(() -> {
                    handler.removeCallbacks(progressUpdater); // 停止模拟进度
                    Toast.makeText(oc_loading.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }
    private void saveToDatabase(String imagePath, String roleName) {
        executorService.execute(() -> {
            try {
                // 获取数据库实例
                AppDatabase db = AppDatabase.getDatabase(oc_loading.this);
                
                // 创建实体并插入
                ImageRoleEntity entity = new ImageRoleEntity(imagePath, roleName);
                db.imageRoleDao().insert(entity);
                
                Log.d("oc_loading", "保存到数据库 - 角色名: " + roleName + ", 图片路径: " + imagePath);
            } catch (Exception e) {
                Log.e("oc_loading", "保存数据时出错: " + e.getMessage());
            }
        });
    }

    private void checkPermissionsAndLoadGif() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13及以上版本使用 READ_MEDIA_IMAGES 权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) 
                    == PackageManager.PERMISSION_GRANTED) {
                loadGif();
            } else {
                ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 
                    PERMISSION_REQUEST_CODE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11-12 使用 READ_EXTERNAL_STORAGE 权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                    == PackageManager.PERMISSION_GRANTED) {
                loadGif();
            } else {
                ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 
                    PERMISSION_REQUEST_CODE);
            }
        } else {
            // Android 10及以下版本使用传统存储权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                    == PackageManager.PERMISSION_GRANTED) {
                loadGif();
            } else {
                ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 
                    PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                loadGif();
            } else {
                Toast.makeText(this, "需要存储权限才能继续", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void loadGif() {
        try {
            RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);

            Glide.with(this)
                .asGif()
                .load(R.drawable.getstart)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .error(R.drawable.logo)
                .into(gifImage);

            // 开始生成图片
            if (userInput != null && !userInput.isEmpty()) {
                generateImage(userInput);
            } else {
                Toast.makeText(this, "无效的用户输入", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "loadGif error: " + e.getMessage());
            gifImage.setImageResource(R.drawable.logo);
            if (userInput != null && !userInput.isEmpty()) {
                generateImage(userInput);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}