package com.example.final_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_project.R;
import com.example.final_project.data.network.AuthApiService;
import com.example.final_project.utils.PasswordValidator;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView registerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // 初始化视图
        usernameInput = findViewById(R.id.userIdEditText);
        passwordInput = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.button_login);
        registerText = findViewById(R.id.register_text);

        // 添加用户名输入监听器
        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String error = PasswordValidator.getUsernameLengthError(s.toString());
                if (error != null) {
                    usernameInput.setError(error);
                } else {
                    usernameInput.setError(null);
                }
            }
        });

        // 添加密码输入监听器
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String error = PasswordValidator.getPasswordError(s.toString());
                if (error != null) {
                    passwordInput.setError(error);
                } else {
                    passwordInput.setError(null);
                }
            }
        });

        // 设置登录按钮点击事件
        loginButton.setOnClickListener(v -> handleLogin());

        // 设置注册文本点击事件
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // 验证用户名
        String usernameError = PasswordValidator.getUsernameLengthError(username);
        if (usernameError != null) {
            usernameInput.setError(usernameError);
            return;
        }

        // 验证密码
        String passwordError = PasswordValidator.getPasswordError(password);
        if (passwordError != null) {
            passwordInput.setError(passwordError);
            return;
        }

        // 禁用登录按钮，防止重复点击
        loginButton.setEnabled(false);

        // 发送登录请求
        AuthApiService.login(username, password, new AuthApiService.AuthCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String token = jsonResponse.getString("token");
                    JSONObject user = jsonResponse.getJSONObject("user");
                    
                    // 保存认证信息
                    AuthApiService.saveAuthInfo(Login.this, token, 
                        user.getString("username"), 
                        user.getInt("id"), 
                        user.getString("role"));

                    // 登录成功，跳转到 getstart 页面
                    runOnUiThread(() -> {
                        Intent intent = new Intent(Login.this, getstart.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(Login.this, "解析响应失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        loginButton.setEnabled(true);
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show();
                    loginButton.setEnabled(true);
                });
            }
        });
    }
}
