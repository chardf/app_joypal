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


        MaterialButton nextButton = findViewById(R.id.button_design); // 确保使用正确的按钮ID

        nextButton.setOnClickListener(view -> {
            // 创建 Intent 跳转到 PersonalityDesign 页面
            Intent intent = new Intent(Create_Joypet.this, personality_design.class);
            startActivity(intent); // 启动新页面
        });

        // 获取底部导航栏
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置导航栏图标的默认选择项
        bottomNavigationView.setSelectedItemId(R.id.menu_home); // 确保 home 图标被选中

        // 为导航栏的每个选项设置监听器
//        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.menu_home:
//                    // 如果点击的是 home 图标，不需要做任何操作
//                    return true;
//                case R.id.menu_create:
//                    // 处理 create 图标的点击逻辑
//                    Intent createIntent = new Intent(Create_Joypet_button.this, personality_design.class); // 修改为你的目标页面
//                    startActivity(createIntent);
//                    return true;
//                case R.id.menu_joypal:
//                    // 处理 joypal 图标的点击逻辑
//                    return true;
//                case R.id.menu_settings:
//                    // 处理 settings 图标的点击逻辑
//                    return true;
//                default:
//                    return false;
//            }
//        });
    }
}
