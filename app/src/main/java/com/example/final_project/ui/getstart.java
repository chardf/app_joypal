package com.example.final_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.final_project.R;
import com.example.final_project.database.AppDatabase;
import com.example.final_project.data.model.Entity.ImageRoleEntity;

import java.util.List;

public class getstart extends AppCompatActivity {

    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);

        // 初始化数据库
        appDatabase = AppDatabase.getDatabase(this);

        // 初始化 "开始" 按钮
        MaterialButton nextButton = findViewById(R.id.button1); // 确保使用正确的按钮ID
        nextButton.setOnClickListener(view -> {
            checkRoleDataAndNavigate();
        });
    }

    private void checkRoleDataAndNavigate() {
        LiveData<List<ImageRoleEntity>> roleListLiveData = appDatabase.imageRoleDao().getAll();
        roleListLiveData.observe(this, roleList -> {
            if (roleList != null && !roleList.isEmpty()) {
                // 如果有角色数据，跳转到 RolesListActivity
                Intent intent = new Intent(getstart.this, RolesListActivity.class);
                startActivity(intent);
            } else {
                // 如果没有角色数据，跳转到 Create_Joypet 页面
                Intent intent = new Intent(getstart.this, Create_Joypet.class);
                startActivity(intent);
            }
        });
    }
}