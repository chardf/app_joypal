package com.example.final_project.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ImageView;
import android.view.View;
import android.os.CountDownTimer;
import android.widget.Button;
import android.view.ViewGroup;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.final_project.R;
import com.example.final_project.database.AppDatabase;
import com.example.final_project.data.model.Entity.ImageRoleEntity;
import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

public class getstart extends AppCompatActivity {

    private AppDatabase appDatabase;
    private LottieAnimationView gifImage;
    private Button startButton;
    private BottomNavigationView bottomNavigationView;
    private ImageView loadingGifView;
    private CountDownTimer countDownTimer;
    private static final int COUNTDOWN_TIME = 3000; // 3秒倒计时

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

        // 初始状态：显示加载中
        if (startButton != null) {
            startButton.setVisibility(View.VISIBLE);
            startButton.setText("Loading...");
            startButton.setTextColor(getResources().getColor(android.R.color.white));
            startButton.setEnabled(false); // 禁用按钮
            // 设置按钮宽度为match_parent
            ViewGroup.LayoutParams params = startButton.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            startButton.setLayoutParams(params);
        }

        // 设置底部导航栏 Home 图标高亮
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_home);
            setupBottomNavigation(bottomNavigationView);
        }

        // 初始化动画
        if (gifImage != null) {
            gifImage.setAnimation("homeAnimation.lottie");
            gifImage.playAnimation();
        }

        // 开始检查角色数据
        checkRoleDataAndNavigate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在 Activity 每次可见时检查角色数据并决定是否跳转
        checkRoleDataAndNavigate();
        
        // 设置底部导航栏 Home 图标高亮
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_home);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void checkRoleDataAndNavigate() {
        appDatabase.imageRoleDao().getAll().observe(this, new Observer<List<ImageRoleEntity>>() {
            @Override
            public void onChanged(List<ImageRoleEntity> roleList) {
                appDatabase.imageRoleDao().getAll().removeObserver(this);

                if (roleList != null && !roleList.isEmpty()) {
                    // 如果有角色数据，显示倒计时并自动跳转
                    if (startButton != null) {
                        startButton.setText("Redirecting in 3s...");
                        startButton.setTextColor(getResources().getColor(android.R.color.white));
                        startButton.setEnabled(false);
                        startCountDown();
                    }
                } else {
                    // 如果没有角色数据，显示 getstart 按钮
                    if (startButton != null) {
                        startButton.setText("Get Start");
                        startButton.setTextColor(getResources().getColor(android.R.color.white));
                        startButton.setEnabled(true);
                        // 设置按钮点击事件，跳转到 Create_Joypal 页面
                        startButton.setOnClickListener(view -> {
                            Intent intent = new Intent(getstart.this, Create_Joypal.class);
                            startActivity(intent);
                        });
                    }
                }
            }
        });
    }

    private void startCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(COUNTDOWN_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (startButton != null) {
                    int secondsRemaining = (int) (millisUntilFinished / 1000);
                    startButton.setText("Redirecting in " + secondsRemaining + "s...");
                    startButton.setTextColor(getResources().getColor(android.R.color.white));
                    startButton.setEnabled(false);
                }
            }

            @Override
            public void onFinish() {
                // 倒计时结束，跳转到角色列表页面
                Intent intent = new Intent(getstart.this, RolesListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    // 设置底部导航监听器的方法
    private void setupBottomNavigation(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                return true;
            } else if (itemId == R.id.menu_create) {
                Intent intent = new Intent(this, Create_Joypal.class);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (gifImage != null) {
            gifImage.cancelAnimation();
        }
    }
}