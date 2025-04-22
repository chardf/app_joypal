package com.example.final_project.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.final_project.R;
import com.example.final_project.data.network.ImageGenerationService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

public class personality_design extends AppCompatActivity {

    // 输入框
    private EditText nameEditText, lookEditText, genderEditText, personalityEditText;
    // 按钮
    private MaterialButton nextButton, skip;

    // 网络服务
    private ImageGenerationService imageGenerationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personality_design);

        // 初始化 ImageGenerationService
        imageGenerationService = new ImageGenerationService();

        // 获取底部导航栏
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置导航栏图标的默认选择项
        bottomNavigationView.setSelectedItemId(R.id.menu_create);

        // 初始化视图
        nameEditText = findViewById(R.id.nameEditText);
        lookEditText = findViewById(R.id.look);
        genderEditText = findViewById(R.id.gender);
        personalityEditText = findViewById(R.id.personality);
        nextButton = findViewById(R.id.button_design2);
        skip = findViewById(R.id.skip);

        // 初始时禁用 Next 按钮
        updateNextButtonState(false);

        // 设置输入框的监听器
        setupInputListeners();

        // 设置 Next 按钮的点击事件
        nextButton.setOnClickListener(v -> {
            if (nextButton.isEnabled()) {
                // 获取组合后的字符串
                String userInput = combineInputsAsString();
                // 调用网络层生成图片
                generateImage(userInput);
            }
        });
    }

    // 设置输入框监听器
    private void setupInputListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };

        nameEditText.addTextChangedListener(textWatcher);
        lookEditText.addTextChangedListener(textWatcher);
        genderEditText.addTextChangedListener(textWatcher);
        personalityEditText.addTextChangedListener(textWatcher);
    }

    // 检查所有输入框是否合法
    private void checkInputs() {
        boolean isValid = isInputValid(nameEditText) && isInputValid(lookEditText) &&
                isInputValid(genderEditText) && isInputValid(personalityEditText);

        updateNextButtonState(isValid);
    }

    // 验证输入框内容是否合法
    private boolean isInputValid(EditText editText) {
        String input = editText.getText().toString().trim();
        return input.length() > 0 && input.length() <= 30; // 检查输入是否为空且长度小于等于30
    }

    // 根据输入框的合法性更新 Next 按钮的状态
    private void updateNextButtonState(boolean isEnabled) {
        if (isEnabled) {
            nextButton.setEnabled(true);
            nextButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#044132"))); // 启用时背景为绿色
        } else {
            nextButton.setEnabled(false);
            nextButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0B0B0"))); // 禁用时背景为灰色
        }
    }

    // 组合输入框的内容为一个字符串
    private String combineInputsAsString() {
        String name = nameEditText.getText().toString().trim();
        String look = lookEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();
        String personality = personalityEditText.getText().toString().trim();

        return "Name: " + name + ", Look: " + look + ", Gender: " + gender + ", Personality: " + personality;
    }

    // 调用 ImageGenerationService 并生成图片
    private void generateImage(String prompt) {
        imageGenerationService.generateImage(this, prompt, new ImageGenerationService.ImageGenerationCallback() {
            @Override
            public void onSuccess(String imagePath, String fileName) {
                // 图片生成成功
                runOnUiThread(() -> {
                    Toast.makeText(personality_design.this, "Image saved successfully! File: " + fileName, Toast.LENGTH_LONG).show();

                    // 跳转到 oc_loading 页面，传递生成的文件名
                    Intent intent = new Intent(personality_design.this, oc_loading.class);
                    intent.putExtra("imageName", fileName); // 将文件名传递到 oc_loading
                    startActivity(intent);

                    // 可选：结束当前页面
                    finish();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // 图片生成失败
                runOnUiThread(() -> Toast.makeText(personality_design.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show());
            }
        });
    }
}