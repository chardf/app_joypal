package com.example.final_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.example.final_project.data.network.AuthApiService;
import com.example.final_project.utils.PasswordValidator;
import com.google.android.material.button.MaterialButton;

public class Register extends AppCompatActivity {
    private EditText userIdEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private MaterialButton registerButton;
    private TextView backToLoginText;
    private boolean isRegistering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // 初始化视图
        userIdEditText = findViewById(R.id.userIdEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.button_register);
        backToLoginText = findViewById(R.id.login_text);

        // 设置返回登录文本点击事件
        backToLoginText.setOnClickListener(v -> {
            if (!isRegistering) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        // 添加用户名输入监听器
        userIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String error = PasswordValidator.getUsernameLengthError(s.toString());
                if (error != null) {
                    userIdEditText.setError(error);
                } else {
                    userIdEditText.setError(null);
                }
            }
        });

        // 添加密码输入监听器
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String error = PasswordValidator.getPasswordError(s.toString());
                if (error != null) {
                    passwordEditText.setError(error);
                } else {
                    passwordEditText.setError(null);
                }
            }
        });

        // 添加确认密码输入监听器
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = passwordEditText.getText().toString();
                if (!s.toString().equals(password)) {
                    confirmPasswordEditText.setError("两次输入的密码不一致");
                } else {
                    confirmPasswordEditText.setError(null);
                }
            }
        });

        // 设置注册按钮点击事件
        registerButton.setOnClickListener(v -> {
            String userId = userIdEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (validateInput(userId, password, confirmPassword)) {
                // 禁用注册按钮和返回登录链接，防止重复点击
                registerButton.setEnabled(false);
                isRegistering = true;
                backToLoginText.setTextColor(getResources().getColor(android.R.color.darker_gray));

                // 发送注册请求
                AuthApiService.register(userId, password, new AuthApiService.AuthCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            Toast.makeText(Register.this, "注册成功", Toast.LENGTH_SHORT).show();
                            // 注册成功后返回登录页面
                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(Register.this, "注册失败: " + error, Toast.LENGTH_SHORT).show();
                            registerButton.setEnabled(true);
                            isRegistering = false;
                            backToLoginText.setTextColor(getResources().getColor(R.color.back_to_log_color));
                        });
                    }
                });
            }
        });
    }

    private boolean validateInput(String userId, String password, String confirmPassword) {
        // 验证用户名
        String usernameError = PasswordValidator.getUsernameLengthError(userId);
        if (usernameError != null) {
            userIdEditText.setError(usernameError);
            return false;
        }

        // 验证密码
        String passwordError = PasswordValidator.getPasswordError(password);
        if (passwordError != null) {
            passwordEditText.setError(passwordError);
            return false;
        }

        // 验证确认密码
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("两次输入的密码不一致");
            return false;
        }

        return true;
    }
} 