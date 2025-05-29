package com.example.final_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.google.android.material.button.MaterialButton;

public class Register extends AppCompatActivity {
    private EditText userIdEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private MaterialButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // 初始化视图
        userIdEditText = findViewById(R.id.userIdEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.button_register);
        TextView loginText = findViewById(R.id.login_text);

        // 设置注册按钮点击事件
        registerButton.setOnClickListener(view -> {
            String userId = userIdEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (validateInput(userId, password, confirmPassword)) {
                // TODO: 实现实际的注册逻辑
                // 这里应该添加用户注册到数据库的代码
                Toast.makeText(Register.this, "注册成功", Toast.LENGTH_SHORT).show();
                // 注册成功后返回登录页面
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        // 设置返回登录文本点击事件
        loginText.setOnClickListener(v -> {
            // 返回登录页面
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean validateInput(String userId, String password, String confirmPassword) {
        if (userId.isEmpty()) {
            userIdEditText.setError("请输入用户ID");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("请输入密码");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("请确认密码");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("两次输入的密码不一致");
            return false;
        }
        return true;
    }
} 