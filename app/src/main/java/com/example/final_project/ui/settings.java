package com.example.final_project.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.example.final_project.database.AppDatabase;
import com.example.final_project.data.model.Entity.ImageRoleEntity;
import com.example.final_project.data.network.AuthApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class settings extends AppCompatActivity {
    private Button logoutButton;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // 初始化底部导航栏
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // 设置导航栏图标的默认选择项为 "settings"
        bottomNavigationView.setSelectedItemId(R.id.menu_settings);
        setupBottomNavigationView(bottomNavigationView);

        // 初始化并绑定清除按钮
        Button buttonClear = findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(v -> clearAllRecords());

        // 初始化并绑定测试按钮
        Button testButton = findViewById(R.id.test_button);
        testButton.setOnClickListener(v -> checkStoredData());

        logoutButton = findViewById(R.id.logout_button);
        changePasswordButton = findViewById(R.id.button_change_password);

        logoutButton.setOnClickListener(v -> handleLogout());
        changePasswordButton.setOnClickListener(v -> openChangePassword());

        // 在onCreate方法中添加
        checkAndRequestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 设置底部导航栏 Settings 图标高亮
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_settings);
        }
    }

    private void setupBottomNavigationView(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                intent = new Intent(settings.this, getstart.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // 结束当前Activity
                return true;
            } else if (itemId == R.id.menu_create) {
                intent = new Intent(settings.this, Create_Joypal.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // 结束当前Activity
                return true;
            } else if (itemId == R.id.menu_joypal) {
                intent = new Intent(settings.this, joypal_chat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // 结束当前Activity
                return true;
            } else if (itemId == R.id.menu_settings) {
                // 当前已经在Settings页面，不需要跳转
                return true;
            }
            return false;
        });
    }

    private void clearAllRecords() {
        new AlertDialog.Builder(this)
            .setTitle("确认清除")
            .setMessage("确定要清除所有角色数据吗？此操作不可恢复。")
            .setPositiveButton("确定", (dialog, which) -> {
                new Thread(() -> {
                    AppDatabase db = AppDatabase.getDatabase(settings.this);
                    db.imageRoleDao().deleteAll();
                    
                    // 清除SharedPreferences中的数据
                    SharedPreferences prefs = getSharedPreferences("RolePreferences", MODE_PRIVATE);
                    prefs.edit().clear().apply();
                    
                    runOnUiThread(() -> {
                        Toast.makeText(settings.this, "所有数据已清除", Toast.LENGTH_SHORT).show();
                    });
                }).start();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void checkStoredData() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(settings.this);
            List<ImageRoleEntity> roles = db.imageRoleDao().getAllSync();
            
            SharedPreferences prefs = getSharedPreferences("RolePreferences", MODE_PRIVATE);
            String savedRoleName = prefs.getString("roleName", null);
            String savedImagePath = prefs.getString("imagePath", null);
            
            StringBuilder message = new StringBuilder();
            message.append("数据库中的角色数量: ").append(roles.size()).append("\n\n");
            
            for (ImageRoleEntity role : roles) {
                message.append("角色ID: ").append(role.getId()).append("\n");
                message.append("角色名: ").append(role.getRoleName()).append("\n");
                message.append("图片路径: ").append(role.getImagePath()).append("\n\n");
            }
            
            message.append("SharedPreferences数据:\n");
            message.append("角色名: ").append(savedRoleName).append("\n");
            message.append("图片路径: ").append(savedImagePath);
            
            runOnUiThread(() -> {
                new AlertDialog.Builder(this)
                    .setTitle("存储的数据")
                    .setMessage(message.toString())
                    .setPositiveButton("确定", null)
                    .show();
            });
        }).start();
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void handleLogout() {
        // 直接跳转到登录页面
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openChangePassword() {
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }
}