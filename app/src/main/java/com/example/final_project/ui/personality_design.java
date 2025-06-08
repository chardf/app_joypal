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
                // 获取所有输入字段
                String name = nameEditText.getText().toString().trim();
                String look = lookEditText.getText().toString().trim();
                String gender = genderEditText.getText().toString().trim();
                String personality = personalityEditText.getText().toString().trim();
                String userInput = combineInputsAsString();

                // 跳转到 oc_loading 页面并传递所有信息
                Intent intent = new Intent(personality_design.this, oc_loading.class);
                intent.putExtra("userInput", userInput);
                intent.putExtra("roleName", name);
                intent.putExtra("name", name);
                intent.putExtra("look", look);
                intent.putExtra("gender", gender);
                intent.putExtra("personality", personality);
                startActivity(intent);

                finish();
            }
        });

        setupSkipButton();
        setupBottomNavigationView();
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

        return "Name: " + name + ", Gender: " + gender + ", Personality: " + personality + ", Look: " + look;
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置导航栏图标的默认选择项为 "create"
        bottomNavigationView.setSelectedItemId(R.id.menu_create);

        // 为导航栏的每个选项设置监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                Intent intent = new Intent(personality_design.this, getstart.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_create) {
                // 当前页面就是 Create，不做操作
                return true;
            } else if (itemId == R.id.menu_joypal) {
                Intent intent = new Intent(personality_design.this, joypal_chat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_settings) { // 注意这里的 ID，确保和你的菜单文件一致
                Intent intent = new Intent(personality_design.this, settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupSkipButton() {
        skip.setOnClickListener(v -> {
            // 传递空数据
            String emptyUserInput = "Name: , Gender: , Personality: , Look: ";

            // 跳转到 oc_loading 页面并传递空数据
            Intent intent = new Intent(personality_design.this, oc_loading.class);
            intent.putExtra("userInput", emptyUserInput);
            intent.putExtra("roleName", "");
            intent.putExtra("name", "");
            intent.putExtra("look", "");
            intent.putExtra("gender", "");
            intent.putExtra("personality", "");
            startActivity(intent);

            finish();
        });
    }
}