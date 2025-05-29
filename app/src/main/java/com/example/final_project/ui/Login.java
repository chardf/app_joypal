package com.example.final_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.final_project.R;
import com.example.final_project.database.AppDatabase;
import com.example.final_project.data.model.Entity.ImageRoleEntity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private AppDatabase appDatabase;
    private EditText userIdEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // 初始化数据库
        appDatabase = AppDatabase.getDatabase(this);

        // 初始化视图
        userIdEditText = findViewById(R.id.userIdEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.button_login);
        TextView registerText = findViewById(R.id.register_text);

        // 设置登录按钮点击事件
        loginButton.setOnClickListener(view -> {
            String userId = userIdEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (validateInput(userId, password)) {
                // 这里应该添加实际的用户验证逻辑
                // 目前为了演示，我们直接进行跳转
                checkRoleDataAndNavigate();
            }
        });

        // 设置注册文本点击事件
        registerText.setOnClickListener(v -> {
            // 跳转到注册界面
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    private boolean validateInput(String userId, String password) {
        if (userId.isEmpty()) {
            userIdEditText.setError("Please enter User ID");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Please enter Password");
            return false;
        }
        return true;
    }

    private void checkRoleDataAndNavigate() {
        // 检查数据库
        LiveData<List<ImageRoleEntity>> roleListLiveData = appDatabase.imageRoleDao().getAll();
        roleListLiveData.observe(this, roleList -> {
            Log.d("Login", "数据库检查 - 角色列表大小: " + (roleList != null ? roleList.size() : 0));
            
            if (roleList != null) {
                for (ImageRoleEntity role : roleList) {
                    Log.d("Login", "数据库中的角色 - ID: " + role.getId() + 
                                  ", 角色名: " + role.getRoleName() + 
                                  ", 图片路径: " + role.getImagePath());
                }
            }

            // 根据数据库中的角色列表决定跳转
            if (roleList != null && !roleList.isEmpty()) {
                Log.d("Login", "跳转到角色列表页面");
                Intent intent = new Intent(Login.this, RolesListActivity.class);
                startActivity(intent);
            } else {
                Log.d("Login", "跳转到开始页面");
                Intent intent = new Intent(Login.this, getstart.class);
                startActivity(intent);
            }
            finish();
        });
    }
}
