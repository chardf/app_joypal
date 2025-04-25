package com.example.final_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.example.final_project.data.network.ImageGenerationService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class oc_loading extends AppCompatActivity {

    private ProgressBar progressBar; // 水平进度条
    private TextView loadTextView; // 显示进度的 TextView
    private String userInput; // 从上一个页面传递的用户输入
    private Handler handler; // 用于更新进度和模拟
    private int progress = 0; // 当前进度值
    private Runnable progressUpdater; // 用于模拟进度更新

    private ImageGenerationService imageGenerationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oc_loading);

        // 初始化视图
        progressBar = findViewById(R.id.static_progress_bar);
        loadTextView = findViewById(R.id.load);

        // 获取从 personality_design 页面传递的用户输入
        Intent intent = getIntent();
        userInput = intent.getStringExtra("userInput");

        // 初始化 Handler 和 ImageGenerationService
        handler = new Handler();
        imageGenerationService = new ImageGenerationService();

        // 设置进度初始值
        loadTextView.setText("0%");

        // 开始模拟进度更新
        startProgressSimulation();

        // 调用图片生成逻辑
        generateImage(userInput);

        setupBottomNavigationView();


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

                    Toast.makeText(oc_loading.this, "Image saved successfully!", Toast.LENGTH_SHORT).show();

                    // 跳转到结果页面
                    Intent intent = new Intent(oc_loading.this, joypal_chat.class);
                    intent.putExtra("imagePath", imagePath); // 传递生成的图片路径
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保在页面销毁时移除回调，避免内存泄漏
        handler.removeCallbacksAndMessages(null);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置导航栏图标的默认选择项为 "create"
        bottomNavigationView.setSelectedItemId(R.id.menu_create);

        // 为导航栏的每个选项设置监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                // 跳转到 Home 页面
                Intent intent = new Intent(oc_loading.this, getstart.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_create) {
                // 跳转到 Create 页面
                Intent intent = new Intent(oc_loading.this, Create_Joypet.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_joypal) {
                // 当前已经是 Joypal 页面，无需跳转
                return true;
            } else if (itemId == R.id.menu_settings) {
                // 跳转到 Settings 页面
                Intent intent = new Intent(oc_loading.this, settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}