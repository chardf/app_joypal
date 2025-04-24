package com.example.final_project.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.final_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

public class settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings); // 确保布局文件名为 settings.xml

        // 初始化导航栏
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupBottomNavigationView(bottomNavigationView);

        // 设置默认选中项为 Settings
        bottomNavigationView.setSelectedItemId(R.id.menu_settings);
    }

    private void setupBottomNavigationView(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                // 跳转到 Home 页面
                intent = new Intent(settings.this, getstart.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_create) {
                // 跳转到 Create 页面
                intent = new Intent(settings.this, Create_Joypet.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_joypal) {
                // 跳转到 Joypal 页面
                intent = new Intent(settings.this, joypal_chat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_settings) {
                // 当前已经是 Settings 页面，无需跳转
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 确保导航栏状态与页面一致
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_settings);
    }
}