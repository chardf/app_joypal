package com.example.final_project.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ImageView;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.final_project.R;
import com.example.final_project.database.AppDatabase;
import com.example.final_project.data.model.Entity.ImageRoleEntity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class getstart extends AppCompatActivity {

    private AppDatabase appDatabase;
    private ImageView gifImage;
    private MaterialButton startButton;
    private BottomNavigationView bottomNavigationView;
    private ImageView loadingGifView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);

        // 初始化数据库
        appDatabase = AppDatabase.getDatabase(this);

        // 初始化 UI 元素
        startButton = findViewById(R.id.button1);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        gifImage = findViewById(R.id.gif_image);
        loadingGifView = findViewById(R.id.loading_gif_view);

        // 初始状态：隐藏按钮（因为要等数据库检查）
        if (startButton != null) startButton.setVisibility(View.GONE);

        // 设置底部导航栏 Home 图标高亮
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_home);
            setupBottomNavigation(bottomNavigationView);
        }

        // 加载 GIF
        loadGif();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在 Activity 每次可见时检查角色数据并决定是否跳转
        checkRoleDataAndNavigate();
    }

    private void checkRoleDataAndNavigate() {
        appDatabase.imageRoleDao().getAll().observe(this, new Observer<List<ImageRoleEntity>>() {
            @Override
            public void onChanged(List<ImageRoleEntity> roleList) {
                appDatabase.imageRoleDao().getAll().removeObserver(this);

                if (roleList != null && !roleList.isEmpty()) {
                    // 如果有角色数据，跳转到 RolesListActivity
                    Intent intent = new Intent(getstart.this, RolesListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    // 如果没有角色数据，停留在 getstart 页面
                    if (startButton != null) startButton.setVisibility(View.VISIBLE);

                    // 设置按钮点击事件，跳转到 Create_Joypet 页面
                    if (startButton != null) {
                        startButton.setOnClickListener(view -> {
                            Intent intent = new Intent(getstart.this, Create_Joypet.class);
                            startActivity(intent);
                        });
                    }
                }
            }
        });
    }

    // 设置底部导航监听器的方法
    private void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                return true;
            } else if (itemId == R.id.menu_create) {
                Intent intent = new Intent(this, Create_Joypet.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_joypal) {
                Intent intent = new Intent(this, joypal_chat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_settings) { // 注意这里的 ID，确保和你的菜单文件一致
                Intent intent = new Intent(this, settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    // loadGif 方法如果用到了，需要确保 gifImage 视图存在于 R.layout.launch 中
    private void loadGif() {
        if (gifImage != null) {
            Glide.with(this)
                .asGif()
                .load(R.drawable.getstart) // 确保有这个drawable资源
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(gifImage);
        }
    }

    // 如果需要处理 Activity 销毁时的清理工作，可以重写 onDestroy
    // @Override
    // protected void onDestroy() {
    //     super.onDestroy();
    //     if (gifImage != null) {
    //         Glide.with(this).clear(gifImage);
    //     }
    // }
}