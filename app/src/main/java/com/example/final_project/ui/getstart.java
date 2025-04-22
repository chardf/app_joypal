package com.example.final_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.final_project.R;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;

public class getstart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);

        MaterialButton button1 = findViewById(R.id.button1);

        // 按钮点击时跳转到第二个界面
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getstart.this, Create_Joypet.class);
                startActivity(intent);
            }
        });
    }
}
