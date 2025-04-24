package com.example.final_project.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class Create_Joypet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_joypet);

        // 获取 "设计" 按钮并设置点击事件
        MaterialButton nextButton = findViewById(R.id.button_design); // 确保使用正确的按钮ID
        nextButton.setOnClickListener(view -> {
            // 创建 Intent 跳转到 PersonalityDesign 页面
            Intent intent = new Intent(Create_Joypet.this, personality_design.class);
            startActivity(intent); // 启动新页面
        });

        // 获取底部导航栏
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置导航栏图标的默认选择项为 "create"
        bottomNavigationView.setSelectedItemId(R.id.menu_create);

        // 为导航栏的每个选项设置监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                // 跳转到 Home 页面
                intent = new Intent(Create_Joypet.this, getstart.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_create) {
                // 当前已经是 Create 页面，无需跳转
                return true;
            } else if (itemId == R.id.menu_joypal) {
                // 跳转到 Joypal 页面
                intent = new Intent(Create_Joypet.this, joypal_chat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_settings) {
                // 跳转到 Settings 页面
                intent = new Intent(Create_Joypet.this, settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}