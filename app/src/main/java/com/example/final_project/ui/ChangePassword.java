package com.example.final_project.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.google.android.material.button.MaterialButton;

public class ChangePassword extends AppCompatActivity {
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmNewPasswordEditText;
    private MaterialButton changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        // 初始化视图
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        changePasswordButton = findViewById(R.id.button_change_password);

        // 设置修改密码按钮点击事件
        changePasswordButton.setOnClickListener(view -> {
            String currentPassword = currentPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();

            if (validateInput(currentPassword, newPassword, confirmNewPassword)) {
                // TODO: 实现实际的密码修改逻辑
                // 这里应该添加验证当前密码和更新新密码的代码
                Toast.makeText(ChangePassword.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean validateInput(String currentPassword, String newPassword, String confirmNewPassword) {
        if (currentPassword.isEmpty()) {
            currentPasswordEditText.setError("请输入当前密码");
            return false;
        }
        if (newPassword.isEmpty()) {
            newPasswordEditText.setError("请输入新密码");
            return false;
        }
        if (confirmNewPassword.isEmpty()) {
            confirmNewPasswordEditText.setError("请确认新密码");
            return false;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            confirmNewPasswordEditText.setError("两次输入的新密码不一致");
            return false;
        }
        if (newPassword.equals(currentPassword)) {
            newPasswordEditText.setError("新密码不能与当前密码相同");
            return false;
        }
        return true;
    }
} 