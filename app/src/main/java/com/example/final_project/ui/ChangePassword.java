package com.example.final_project.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_project.R;
import com.example.final_project.data.network.AuthApiService;
import com.example.final_project.utils.PasswordValidator;

public class ChangePassword extends AppCompatActivity {
    private EditText oldPasswordInput;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        oldPasswordInput = findViewById(R.id.old_password_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        submitButton = findViewById(R.id.submit_button);

        // 添加新密码输入监听器
        newPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String error = PasswordValidator.getPasswordError(s.toString());
                if (error != null) {
                    newPasswordInput.setError(error);
                } else {
                    newPasswordInput.setError(null);
                }
            }
        });

        // 添加确认密码输入监听器
        confirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String newPassword = newPasswordInput.getText().toString();
                if (!s.toString().equals(newPassword)) {
                    confirmPasswordInput.setError("两次输入的密码不一致");
                } else {
                    confirmPasswordInput.setError(null);
                }
            }
        });

        submitButton.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String oldPassword = oldPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // 验证旧密码
        if (oldPassword.isEmpty()) {
            oldPasswordInput.setError("请输入当前密码");
            return;
        }

        // 验证新密码
        String passwordError = PasswordValidator.getPasswordError(newPassword);
        if (passwordError != null) {
            newPasswordInput.setError(passwordError);
            return;
        }

        // 验证确认密码
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordInput.setError("两次输入的新密码不一致");
            return;
        }

        // 获取当前用户名
        String username = AuthApiService.getUsername(this);
        if (username == null) {
            Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 禁用提交按钮，防止重复点击
        submitButton.setEnabled(false);

        // 发送修改密码请求
        AuthApiService.changePassword(this, username, oldPassword, newPassword, new AuthApiService.AuthCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(ChangePassword.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ChangePassword.this, "密码修改失败: " + error, Toast.LENGTH_SHORT).show();
                    submitButton.setEnabled(true);
                });
            }
        });
    }
} 