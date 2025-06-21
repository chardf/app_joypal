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
import com.example.final_project.data.network.JoyImageGenerationService;
import com.example.final_project.data.network.JoyImageToImageService;
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
    private JoyImageGenerationService imageGenerationService;
    private ImageView gifImage;
    private String currentName;
    private String currentLook;
    private String currentGender;
    private String currentPersonality;
    private String currentRoleName;

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

            // 初始化 Handler 和 JoyImageGenerationService
            handler = new Handler();
            imageGenerationService = new JoyImageGenerationService();

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

    private void generateImage() {
        // 获取传递过来的所有信息
        String userInput = getIntent().getStringExtra("userInput");
        String roleName = getIntent().getStringExtra("roleName");
        String name = getIntent().getStringExtra("name");
        String look = getIntent().getStringExtra("look");
        String gender = getIntent().getStringExtra("gender");
        String personality = getIntent().getStringExtra("personality");
        String referenceImagePath = getIntent().getStringExtra("referenceImagePath");

        // 保存到类成员变量中
        currentName = name;
        currentLook = look;
        currentGender = gender;
        currentPersonality = personality;
        currentRoleName = (name != null && !name.isEmpty()) ? name : roleName;

        if (referenceImagePath != null && !referenceImagePath.isEmpty()) {
            // 调用 img2img 服务
            generateWithReferenceImage(referenceImagePath);
        } else {
            // 调用 text2img 服务
            generateWithoutReferenceImage(userInput);
        }
    }

    private void generateWithReferenceImage(String imagePath) {
        JoyImageToImageService img2imgService = new JoyImageToImageService();
        img2imgService.generateImageFromImage(this, currentName, currentGender, currentPersonality, currentLook, imagePath, new JoyImageToImageService.ImageGenerationCallback() {
            @Override
            public void onSuccess(String characterName, String imageUrl) {
                try {
                    String localImagePath = JoyImageGenerationService.downloadAndSaveImage(oc_loading.this, imageUrl, characterName);
                    handleImageGenerationSuccess(characterName, localImagePath);
                } catch (Exception e) {
                    handleImageGenerationFailure("Failed to save generated image: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                handleImageGenerationFailure(errorMessage);
            }
        });
    }

    private void generateWithoutReferenceImage(String userInput) {
        JoyImageGenerationService imageService = new JoyImageGenerationService();
        imageService.JoygenerateImage(this, userInput, new JoyImageGenerationService.ImageGenerationCallback() {
            @Override
            public void onSuccess(String characterName, String imagePath) {
                handleImageGenerationSuccess(characterName, imagePath);
            }

            @Override
            public void onFailure(String errorMessage) {
                handleImageGenerationFailure(errorMessage);
            }
        });
    }

    private void handleImageGenerationSuccess(String characterName, String imagePath) {
        runOnUiThread(() -> {
            handler.removeCallbacks(progressUpdater);
            loadTextView.setText("100%");
            saveToDatabase(imagePath, characterName, currentName, currentGender, currentPersonality, currentLook);
            Toast.makeText(oc_loading.this, "Image saved successfully!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(oc_loading.this, joypal_chat.class);
            intent.putExtra("imagePath", imagePath);
            intent.putExtra("roleName", characterName);
            intent.putExtra("name", currentName);
            intent.putExtra("gender", currentGender);
            intent.putExtra("personality", currentPersonality);
            intent.putExtra("appearance", currentLook);
            startActivity(intent);
            finish();
        });
    }

    private void handleImageGenerationFailure(String errorMessage) {
        runOnUiThread(() -> {
            handler.removeCallbacks(progressUpdater);
            Toast.makeText(oc_loading.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void saveToDatabase(String imagePath, String roleName, String name, String gender, String personality, String appearance) {
        executorService.execute(() -> {
            try {
                Log.d("oc_loading", "开始保存角色信息到数据库");
                Log.d("oc_loading", "角色信息 - 角色名: " + roleName + 
                    ", 图片路径: " + imagePath + 
                    ", 名字: " + name + 
                    ", 性别: " + gender + 
                    ", 性格: " + personality + 
                    ", 外观: " + appearance);
                
                // 获取数据库实例
                AppDatabase db = AppDatabase.getDatabase(oc_loading.this);
                
                // 创建实体并插入
                ImageRoleEntity entity = new ImageRoleEntity(imagePath, roleName, name, gender, personality, appearance);
                db.imageRoleDao().insert(entity);
                
                Log.d("oc_loading", "角色信息保存成功");
                
                // 验证保存是否成功
                ImageRoleEntity savedRole = db.imageRoleDao().getRoleByName(roleName);
                if (savedRole != null) {
                    Log.d("oc_loading", "验证保存成功 - 角色ID: " + savedRole.getId());
                    
                    // 保存到SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("RolePreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("roleName", roleName);
                    editor.putString("imagePath", imagePath);
                    editor.apply();
                    
                    Log.d("oc_loading", "角色信息已同步到SharedPreferences");
                } else {
                    Log.e("oc_loading", "验证保存失败 - 未找到保存的角色");
                    throw new Exception("角色保存验证失败");
                }
            } catch (Exception e) {
                Log.e("oc_loading", "保存角色信息失败", e);
                runOnUiThread(() -> {
                    Toast.makeText(oc_loading.this, "保存角色信息失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
                generateImage();
            } else {
                Toast.makeText(this, "无效的用户输入", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "loadGif error: " + e.getMessage());
            gifImage.setImageResource(R.drawable.logo);
            if (userInput != null && !userInput.isEmpty()) {
                generateImage();
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