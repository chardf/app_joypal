package com.example.final_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.example.final_project.data.network.KimiChatApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class joypal_chat extends AppCompatActivity {

    private static final String PREFS_NAME = "RolePreferences";
    private static final String KEY_ROLE_NAME = "roleName";
    private static final String KEY_IMAGE_PATH = "imagePath";

    private EditText userInput; // 用户输入框
    private ImageView sendButton; // 发送按钮
    private TextView feedbackText; // 显示 Joypal 回复的透明框
    private boolean isProcessing = false; // 是否正在处理用户输入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joypal_chat);
        // 初始化导航栏
        setupBottomNavigationView();

        // 初始化 UI 元素
        userInput = findViewById(R.id.user_input);
        sendButton = findViewById(R.id.send_button);
        feedbackText = findViewById(R.id.feedback_text);
        ImageView imageView = findViewById(R.id.oc_image_container);
        TextView nameTextView = findViewById(R.id.oc_name_text); // 初始化角色名 TextView

        // 加载角色信息
        loadRoleInfo(nameTextView, imageView);

        // 设置发送按钮点击事件
        sendButton.setOnClickListener(v -> {
            if (!isProcessing) {
                String inputText = userInput.getText().toString().trim();
                if (!inputText.isEmpty()) {
                    sendUserMessage(inputText, nameTextView.getText().toString());
                } else {
                    Toast.makeText(joypal_chat.this, "请输入内容后再发送！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 确保导航栏选中状态更新
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_joypal);
    }

    /**
     * 加载角色信息
     */
    private void loadRoleInfo(TextView nameTextView, ImageView imageView) {
        // 获取 SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 检查是否有传入的新角色信息
        Intent intent = getIntent();
        String newRoleName = intent.getStringExtra("roleName");
        String newImagePath = intent.getStringExtra("imagePath");

        if (newRoleName != null && newImagePath != null) {
            // 如果有新角色信息，优先加载新信息并保存到 SharedPreferences
            updateRoleInfo(newRoleName, newImagePath, nameTextView, imageView);
            saveRoleInfo(newRoleName, newImagePath);
        } else {
            // 如果没有新信息，从 SharedPreferences 加载上一次保存的角色信息
            String savedRoleName = preferences.getString(KEY_ROLE_NAME, "Unknown Character");
            String savedImagePath = preferences.getString(KEY_IMAGE_PATH, null);

            // 更新 UI
            updateRoleInfo(savedRoleName, savedImagePath, nameTextView, imageView);
        }
    }

    /**
     * 保存角色信息到 SharedPreferences
     */
    private void saveRoleInfo(String roleName, String imagePath) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ROLE_NAME, roleName);
        editor.putString(KEY_IMAGE_PATH, imagePath);
        editor.apply();
    }

    /**
     * 更新角色信息到 UI
     */
    private void updateRoleInfo(String roleName, String imagePath, TextView nameTextView, ImageView imageView) {
        // 更新角色名
        nameTextView.setText(roleName);

        // 更新角色图片
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "Image file does not exist!", Toast.LENGTH_SHORT).show();
            }
        } else {
            imageView.setImageDrawable(null); // 设置为空白
        }
    }

    /**
     * 初始化导航栏
     */
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置导航栏图标的默认选择项为 "Joypal"
        bottomNavigationView.setSelectedItemId(R.id.menu_joypal);

        // 为导航栏的每个选项设置监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                Intent intent = new Intent(joypal_chat.this, getstart.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_create) {
                Intent intent = new Intent(joypal_chat.this, Create_Joypet.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_joypal) {
                // 保持当前页面，确保 Joypal 图标高亮
                return true;
            } else if (itemId == R.id.menu_settings) {
                Intent intent = new Intent(joypal_chat.this, settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    /**
     * 发送用户消息并处理响应
     */
    private void sendUserMessage(String userMessage, String roleName) {
        isProcessing = true; // 设置为正在处理状态
        sendButton.setVisibility(View.INVISIBLE); // 隐藏发送按钮
        feedbackText.setVisibility(View.VISIBLE); // 显示透明框
        feedbackText.setText(roleName + " 正在思考..."); // 显示思考提示

        // 调用网络请求服务
        KimiChatApiService.sendMessage(userMessage, new KimiChatApiService.KimiChatCallback() {
            @Override
            public void onSuccess(String reply) {
                runOnUiThread(() -> {
                    feedbackText.setText(reply); // 显示 API 返回的内容
                    sendButton.setVisibility(View.VISIBLE); // 重新显示发送按钮
                    isProcessing = false; // 重置处理状态
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    feedbackText.setText("出现错误，请重试！");
                    Toast.makeText(joypal_chat.this, errorMessage, Toast.LENGTH_SHORT).show();
                    sendButton.setVisibility(View.VISIBLE); // 重新显示发送按钮
                    isProcessing = false; // 重置处理状态
                });
            }
        });
    }
}