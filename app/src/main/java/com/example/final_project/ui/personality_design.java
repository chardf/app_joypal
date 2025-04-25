package com.example.final_project.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.final_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

public class personality_design extends AppCompatActivity {

    private EditText nameEditText, lookEditText, genderEditText, personalityEditText;
    private MaterialButton nextButton, skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personality_design);

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

                // 跳转到 oc_loading 页面并传递用户输入
                Intent intent = new Intent(personality_design.this, oc_loading.class);
                intent.putExtra("userInput", userInput); // 将用户输入传递到 oc_loading 页面
                startActivity(intent);

                // 可选：结束当前页面
                finish();
            }
        });
    }

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

    private void checkInputs() {
        boolean isValid = isInputValid(nameEditText) && isInputValid(lookEditText) &&
                isInputValid(genderEditText) && isInputValid(personalityEditText);

        updateNextButtonState(isValid);
    }

    private boolean isInputValid(EditText editText) {
        String input = editText.getText().toString().trim();
        return input.length() > 0 && input.length() <= 30;
    }

    private void updateNextButtonState(boolean isEnabled) {
        if (isEnabled) {
            nextButton.setEnabled(true);
            nextButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#044132")));
        } else {
            nextButton.setEnabled(false);
            nextButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0B0B0")));
        }
    }

    private String combineInputsAsString() {
        String name = nameEditText.getText().toString().trim();
        String look = lookEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();
        String personality = personalityEditText.getText().toString().trim();

        return "Name: " + name + ", Look: " + look + ", Gender: " + gender + ", Personality: " + personality;
    }
}