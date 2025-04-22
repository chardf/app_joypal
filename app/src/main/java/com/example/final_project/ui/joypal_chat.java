package com.example.final_project.ui;

import android.content.Intent;
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

import java.io.File;

public class joypal_chat extends AppCompatActivity {

    private EditText userInput; // 用户输入框
    private ImageView sendButton; // 发送按钮
    private TextView feedbackText; // 显示 Joypal 回复的透明框
    private boolean isProcessing = false; // 是否正在处理用户输入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joypal_chat);

        // 初始化 UI 元素
        userInput = findViewById(R.id.user_input);
        sendButton = findViewById(R.id.send_button);
        feedbackText = findViewById(R.id.feedback_text);
        ImageView imageView = findViewById(R.id.oc_image_container);

        // 获取传递的图片路径
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");

        // 显示图片
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "Image file does not exist!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Image path not found!", Toast.LENGTH_SHORT).show();
        }

        // 设置发送按钮点击事件
        sendButton.setOnClickListener(v -> {
            if (!isProcessing) {
                String inputText = userInput.getText().toString().trim();
                if (!inputText.isEmpty()) {
                    sendUserMessage(inputText);
                } else {
                    Toast.makeText(joypal_chat.this, "请输入内容后再发送！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 发送用户消息并处理响应
     */
    private void sendUserMessage(String userMessage) {
        isProcessing = true; // 设置为正在处理状态
        sendButton.setVisibility(View.INVISIBLE); // 隐藏发送按钮
        feedbackText.setVisibility(View.VISIBLE); // 显示透明框
        feedbackText.setText("Joypal 正在思考..."); // 显示思考提示

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