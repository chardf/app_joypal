package com.example.final_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;

import java.io.File;

public class oc_loading extends AppCompatActivity {

    private String imageName;
    private Handler handler;
    private Runnable fileChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oc_loading);

        // 获取传递过来的图片名称
        Intent intent = getIntent();
        imageName = intent.getStringExtra("imageName");

        // 初始化 Handler 和定时任务
        handler = new Handler();
        fileChecker = new Runnable() {
            @Override
            public void run() {
                checkForImageFile();
            }
        };

        // 开始轮询
        handler.postDelayed(fileChecker, 1000); // 每隔 1 秒检查一次
    }

    private void checkForImageFile() {
        // 检查文件是否存在
        File imageFile = new File(getExternalFilesDir("Pictures/GeneratedImages"), imageName);
        if (imageFile.exists()) {
            // 如果文件存在，跳转到 joypal_chat 页面
            Intent intent = new Intent(oc_loading.this, joypal_chat.class);
            intent.putExtra("imagePath", imageFile.getAbsolutePath()); // 传递图片路径
            startActivity(intent);

            // 停止轮询并结束当前页面
            handler.removeCallbacks(fileChecker);
            finish();
        } else {
            // 如果文件不存在，继续检查
            handler.postDelayed(fileChecker, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保在页面销毁时移除回调，避免内存泄漏
        handler.removeCallbacks(fileChecker);
    }
}