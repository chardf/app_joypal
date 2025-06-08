package com.example.final_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.example.final_project.data.network.JoyTextGenerationService;
import com.example.final_project.data.network.KimiChatApiService;
import com.example.final_project.data.model.ChatMessage;
import com.example.final_project.data.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.bumptech.glide.Glide;
import com.airbnb.lottie.LottieAnimationView;
import com.example.final_project.data.database.AppDatabase;
import com.example.final_project.data.model.Entity.ImageRoleEntity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class joypal_chat extends AppCompatActivity {

    private static final String PREFS_NAME = "RolePreferences";
    private static final String KEY_ROLE_NAME = "roleName";
    private static final String KEY_IMAGE_PATH = "imagePath";
    private static final String TAG = "joypal_chat";
    private static final String KEY_CHARACTER_NAME = "character_name";
    private static final String KEY_CHAT_HISTORY = "chat_history";

    private ImageView ocImageView;
    private TextView chatTextView;
    private EditText messageInput;
    private ImageView sendButton;
    private ScrollView scrollView;
    private LottieAnimationView loadingAnimation;
    private AtomicReference<String> characterName = new AtomicReference<>();
    private AtomicReference<String> imagePath = new AtomicReference<>();
    private DatabaseHelper databaseHelper;
    private List<ChatMessage> chatHistory;
    private Handler mainHandler;
    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);
    private final AtomicBoolean isPaused = new AtomicBoolean(false);
    private final AtomicBoolean isFinishing = new AtomicBoolean(false);
    private final AtomicBoolean isWaitingForResponse = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joypal_chat);

        try {
            // 初始化导航栏
            setupBottomNavigationView();

            // 初始化视图
            ocImageView = findViewById(R.id.oc_image_container);
            chatTextView = findViewById(R.id.feedback_text);
            messageInput = findViewById(R.id.userInput);
            sendButton = findViewById(R.id.sendButton);
            scrollView = findViewById(R.id.scrollView);
            loadingAnimation = findViewById(R.id.loading_gif_view);

            // 检查必要的视图是否都找到了
            if (ocImageView == null || chatTextView == null || messageInput == null ||
                    sendButton == null || scrollView == null || loadingAnimation == null) {
                throw new IllegalStateException("Some required views are missing");
            }

            // 初始化服务
            databaseHelper = new DatabaseHelper(this);
            mainHandler = new Handler(Looper.getMainLooper());
            chatHistory = new ArrayList<>();

            // 恢复保存的状态
            if (savedInstanceState != null) {
                characterName.set(savedInstanceState.getString(KEY_CHARACTER_NAME));
                imagePath.set(savedInstanceState.getString(KEY_IMAGE_PATH));
            }

            // 如果没有保存的状态，从Intent获取
            if (characterName.get() == null || imagePath.get() == null) {
                Intent intent = getIntent();
                if (intent != null) {
                    characterName.set(intent.getStringExtra("roleName"));
                    imagePath.set(intent.getStringExtra("imagePath"));
                }
            }

            // 如果仍然没有角色信息，尝试从SharedPreferences获取
            if (characterName.get() == null || imagePath.get() == null) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                characterName.set(prefs.getString(KEY_ROLE_NAME, null));
                imagePath.set(prefs.getString(KEY_IMAGE_PATH, null));
            }

            // 如果仍然没有角色信息，尝试从数据库获取最新的角色
            if (characterName.get() == null || imagePath.get() == null) {
                loadRoleInfo();
                return;
            }

            // 保存当前角色信息
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString(KEY_ROLE_NAME, characterName.get());
            editor.putString(KEY_IMAGE_PATH, imagePath.get());
            editor.apply();

            // 加载角色信息
            loadRoleInfo();

            // 加载聊天历史
            loadChatHistory();

            // 初始化动画
            try {
                loadingAnimation.setAnimation("chat_load.json");
                loadingAnimation.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e(TAG, "Failed to load animation", e);
                loadingAnimation.setVisibility(View.GONE);
            }

            // 设置发送按钮点击事件
            sendButton.setOnClickListener(v -> sendUserMessage());

            // 设置输入框回车发送
            messageInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendUserMessage();
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "初始化失败，请重试", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putString(KEY_CHARACTER_NAME, characterName.get());
            outState.putString(KEY_IMAGE_PATH, imagePath.get());
        } catch (Exception e) {
            Log.e(TAG, "Error saving instance state", e);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            if (savedInstanceState != null) {
                characterName.set(savedInstanceState.getString(KEY_CHARACTER_NAME));
                imagePath.set(savedInstanceState.getString(KEY_IMAGE_PATH));
                loadRoleInfo();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error restoring instance state", e);
        }
    }

    @Override
    protected void onDestroy() {
        isDestroyed.set(true);
        super.onDestroy();
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        isWaitingForResponse.set(false);
    }

    @Override
    protected void onPause() {
        isPaused.set(true);
        super.onPause();
        if (isFinishing.get()) {
            if (mainHandler != null) {
                mainHandler.removeCallbacksAndMessages(null);
            }
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        isWaitingForResponse.set(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFinishing.get()) {
            return;
        }
        isPaused.set(false);
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(this);
        }
        loadChatHistory();
        isWaitingForResponse.set(false);
        setInputEnabled(true);
        // 设置底部导航栏 Joypal 图标高亮
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_joypal);
        }
    }

    @Override
    public void finish() {
        isFinishing.set(true);
        super.finish();
    }

    /**
     * 加载角色信息
     */
    private void loadRoleInfo() {
        // 从SharedPreferences获取角色信息
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String roleName = prefs.getString(KEY_ROLE_NAME, "");
        String savedImagePath = prefs.getString(KEY_IMAGE_PATH, "");

        Log.d(TAG, "开始加载角色信息");
        Log.d(TAG, "从SharedPreferences获取 - 角色名: " + roleName + ", 图片路径: " + savedImagePath);

        if (!roleName.isEmpty() && !savedImagePath.isEmpty()) {
            // 如果有角色信息，从数据库获取完整信息
            AppDatabase db = AppDatabase.getDatabase(this);
            new Thread(() -> {
                try {
                    Log.d(TAG, "开始查询数据库 - 角色名: " + roleName);
                    ImageRoleEntity role = db.imageRoleDao().getRoleByName(roleName);
                    
                    if (role != null) {
                        Log.d(TAG, "数据库查询成功 - 角色信息: " + role.toString());
                        // 使用数据库中的信息更新UI
                        runOnUiThread(() -> {
                            TextView ocNameText = findViewById(R.id.oc_name_text);
                            if (ocNameText != null) {
                                ocNameText.setText(role.getRoleName());
                                Log.d(TAG, "更新UI - 角色名: " + role.getRoleName());
                            }
                            Glide.with(this)
                                .load(new File(role.getImagePath()))
                                .placeholder(R.drawable.default_avatar)
                                .error(R.drawable.default_avatar)
                                .into(ocImageView);
                            messageInput.setEnabled(true);
                            sendButton.setEnabled(true);
                        });
                    } else {
                        Log.w(TAG, "数据库查询失败 - 未找到角色: " + roleName);
                        // 如果数据库中没有找到角色，使用 SharedPreferences 中的信息
                        runOnUiThread(() -> {
                            TextView ocNameText = findViewById(R.id.oc_name_text);
                            if (ocNameText != null) {
                                ocNameText.setText(roleName);
                                Log.d(TAG, "使用SharedPreferences信息更新UI - 角色名: " + roleName);
                            }
                            Glide.with(this)
                                .load(new File(savedImagePath))
                                .placeholder(R.drawable.default_avatar)
                                .error(R.drawable.default_avatar)
                                .into(ocImageView);
                            messageInput.setEnabled(true);
                            sendButton.setEnabled(true);
                        });
                        
                        // 尝试重新保存到数据库
                        try {
                            ImageRoleEntity newRole = new ImageRoleEntity(savedImagePath, roleName, "", "", "", "");
                            db.imageRoleDao().insert(newRole);
                            Log.d(TAG, "尝试重新保存角色到数据库");
                        } catch (Exception e) {
                            Log.e(TAG, "重新保存角色失败", e);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "加载角色信息时发生错误", e);
                    // 发生错误时使用 SharedPreferences 中的信息
                    runOnUiThread(() -> {
                        TextView ocNameText = findViewById(R.id.oc_name_text);
                        if (ocNameText != null) {
                            ocNameText.setText(roleName);
                            Log.d(TAG, "发生错误后使用SharedPreferences信息 - 角色名: " + roleName);
                        }
                        Glide.with(this)
                            .load(new File(savedImagePath))
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(ocImageView);
                        messageInput.setEnabled(true);
                        sendButton.setEnabled(true);
                    });
                }
            }).start();
        } else {
            Log.d(TAG, "SharedPreferences中没有角色信息，尝试从数据库获取最新角色");
            // 如果没有角色信息，尝试从数据库获取最新的角色
            AppDatabase db = AppDatabase.getDatabase(this);
            new Thread(() -> {
                try {
                    List<ImageRoleEntity> roles = db.imageRoleDao().getAllSync();
                    Log.d(TAG, "数据库中共有 " + (roles != null ? roles.size() : 0) + " 个角色");
                    
                    if (roles != null && !roles.isEmpty()) {
                        ImageRoleEntity latestRole = roles.get(roles.size() - 1);
                        Log.d(TAG, "使用最新角色: " + latestRole.toString());
                        
                        // 使用 AtomicReference 更新值
                        characterName.set(latestRole.getRoleName());
                        imagePath.set(latestRole.getImagePath());
                        
                        runOnUiThread(() -> {
                            TextView ocNameText = findViewById(R.id.oc_name_text);
                            if (ocNameText != null) {
                                ocNameText.setText(latestRole.getRoleName());
                                Log.d(TAG, "更新UI - 最新角色名: " + latestRole.getRoleName());
                            }
                            Glide.with(this)
                                .load(new File(latestRole.getImagePath()))
                                .placeholder(R.drawable.default_avatar)
                                .error(R.drawable.default_avatar)
                                .into(ocImageView);
                            messageInput.setEnabled(true);
                            sendButton.setEnabled(true);

                            // 保存当前角色信息到 SharedPreferences
                            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString(KEY_ROLE_NAME, latestRole.getRoleName());
                            editor.putString(KEY_IMAGE_PATH, latestRole.getImagePath());
                            editor.apply();
                            
                            Log.d(TAG, "更新SharedPreferences - 最新角色名: " + latestRole.getRoleName() + ", 图片路径: " + latestRole.getImagePath());
                        });
                    } else {
                        Log.w(TAG, "数据库中没有找到任何角色");
                        runOnUiThread(() -> {
                            Toast.makeText(this, "未找到任何角色，请先创建一个角色", Toast.LENGTH_SHORT).show();
                            // 跳转到角色创建页面
                            Intent intent = new Intent(this, Create_Joypal.class);
                            startActivity(intent);
                            finish();
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "获取最新角色时发生错误", e);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "加载角色信息失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        // 跳转到角色创建页面
                        Intent intent = new Intent(this, Create_Joypal.class);
                        startActivity(intent);
                        finish();
                    });
                }
            }).start();
        }
    }

    /**
     * 初始化导航栏
     */
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 检查 bottomNavigationView 是否为 null，避免崩溃
        if (bottomNavigationView == null) {
            Log.e(TAG, "BottomNavigationView not found!");
            return;
        }

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
                Intent intent = new Intent(joypal_chat.this, Create_Joypal.class);
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

    private void loadChatHistory() {
        if (isDestroyed.get() || isPaused.get() || isFinishing.get()) return;

        try {
            // 确保使用当前角色的名称加载聊天历史
            if (characterName.get() != null) {
                chatHistory = databaseHelper.getChatHistory(characterName.get());
                updateChatDisplay();
            } else {
                Log.e(TAG, "Character name is null when loading chat history");
                chatHistory = new ArrayList<>();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading chat history", e);
            chatHistory = new ArrayList<>();
        }
    }

    private void setInputEnabled(boolean enabled) {
        if (isDestroyed.get() || isPaused.get() || isFinishing.get()) return;

        mainHandler.post(() -> {
            try {
                if (messageInput != null) {
                    messageInput.setEnabled(enabled);
                    messageInput.setAlpha(enabled ? 1.0f : 0.5f);
                }
                if (sendButton != null) {
                    sendButton.setEnabled(enabled);
                    sendButton.setAlpha(enabled ? 1.0f : 0.5f);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error setting input state", e);
            }
        });
    }

    private void sendUserMessage() {
        if (isDestroyed.get() || isPaused.get() || isFinishing.get()) return;
        
        String message = messageInput.getText().toString().trim();
        if (message.isEmpty()) return;

        try {
            // 检查是否正在等待响应
            if (isWaitingForResponse.get()) {
                Toast.makeText(this, "请等待上一条消息的回复", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查角色名是否有效
            String currentRoleName = characterName.get();
            if (currentRoleName == null || currentRoleName.isEmpty() || currentRoleName.equals("44")) {
                Log.e(TAG, "无效的角色名: " + currentRoleName);
                Toast.makeText(this, "请先选择一个角色", Toast.LENGTH_SHORT).show();
                // 跳转到角色选择页面
                Intent intent = new Intent(this, RolesListActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            // 设置等待状态
            isWaitingForResponse.set(true);
            setInputEnabled(false);
            
            // 清空输入框
            messageInput.setText("");
            
            // 创建用户消息
            long timestamp = System.currentTimeMillis();
            ChatMessage userMessage = new ChatMessage(currentRoleName, message, true, timestamp);
            
            // 保存到数据库
            databaseHelper.saveChatMessage(userMessage);
            
            // 添加到聊天历史
            chatHistory.add(userMessage);
            
            // 更新显示
            updateChatDisplay();
            
            // 显示加载动画
            if (loadingAnimation != null) {
                loadingAnimation.setVisibility(View.VISIBLE);
                loadingAnimation.playAnimation();
            }

            // 从数据库获取角色完整信息
            AppDatabase db = AppDatabase.getDatabase(this);
            new Thread(() -> {
                try {
                    Log.d(TAG, "正在查询角色信息，角色名: " + currentRoleName);
                    ImageRoleEntity role = db.imageRoleDao().getRoleByName(currentRoleName);
                    
                    if (role != null) {
                        Log.d(TAG, "成功获取角色信息: " + role.toString());
                        // 使用 JoyTextGenerationService 发送请求
                        JoyTextGenerationService.generateText(
                            message,
                            role.getRoleName(),
                            role.getGender(),
                            role.getPersonality(),
                            role.getAppearance(),
                            new JoyTextGenerationService.TextGenerationCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    if (isDestroyed.get() || isPaused.get() || isFinishing.get()) return;
                                    
                                    mainHandler.post(() -> {
                                        try {
                                            // 隐藏加载动画
                                            if (loadingAnimation != null) {
                                                loadingAnimation.setVisibility(View.GONE);
                                                loadingAnimation.cancelAnimation();
                                            }
                                            
                                            // 创建AI回复消息
                                            ChatMessage aiMessage = new ChatMessage(currentRoleName, response, false, System.currentTimeMillis());
                                            
                                            // 保存到数据库
                                            databaseHelper.saveChatMessage(aiMessage);
                                            
                                            // 添加到聊天历史
                                            chatHistory.add(aiMessage);
                                            
                                            // 更新显示
                                            updateChatDisplay();

                                            // 重置等待状态
                                            isWaitingForResponse.set(false);
                                            setInputEnabled(true);
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error handling AI response", e);
                                            // 发生错误时也要重置状态
                                            isWaitingForResponse.set(false);
                                            setInputEnabled(true);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(String error) {
                                    if (isDestroyed.get() || isPaused.get() || isFinishing.get()) return;
                                    
                                    mainHandler.post(() -> {
                                        try {
                                            // 隐藏加载动画
                                            if (loadingAnimation != null) {
                                                loadingAnimation.setVisibility(View.GONE);
                                                loadingAnimation.cancelAnimation();
                                            }
                                            Toast.makeText(joypal_chat.this, "发送失败: " + error, Toast.LENGTH_SHORT).show();
                                            
                                            // 重置等待状态
                                            isWaitingForResponse.set(false);
                                            setInputEnabled(true);
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error showing error toast", e);
                                            // 发生错误时也要重置状态
                                            isWaitingForResponse.set(false);
                                            setInputEnabled(true);
                                        }
                                    });
                                }
                            }
                        );
                    } else {
                        Log.e(TAG, "未找到角色信息，角色名: " + currentRoleName);
                        // 尝试从数据库获取所有角色
                        List<ImageRoleEntity> allRoles = db.imageRoleDao().getAllSync();
                        Log.d(TAG, "数据库中的所有角色: " + (allRoles != null ? allRoles.size() : 0));
                        if (allRoles != null) {
                            for (ImageRoleEntity r : allRoles) {
                                Log.d(TAG, "角色: " + r.toString());
                            }
                        }
                        
                        mainHandler.post(() -> {
                            Toast.makeText(this, "请先创建一个角色", Toast.LENGTH_SHORT).show();
                            // 跳转到创建角色页面
                            Intent intent = new Intent(this, Create_Joypal.class);
                            startActivity(intent);
                            finish();
                            isWaitingForResponse.set(false);
                            setInputEnabled(true);
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error getting role info", e);
                    mainHandler.post(() -> {
                        Toast.makeText(this, "获取角色信息失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        isWaitingForResponse.set(false);
                        setInputEnabled(true);
                    });
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Error sending message", e);
            Toast.makeText(this, "发送失败，请重试", Toast.LENGTH_SHORT).show();
            // 发生错误时也要重置状态
            isWaitingForResponse.set(false);
            setInputEnabled(true);
        }
    }

    private void updateChatDisplay() {
        if (isDestroyed.get() || isPaused.get() || isFinishing.get()) return;

        StringBuilder chatText = new StringBuilder();
        for (ChatMessage message : chatHistory) {
            if (message.isUserMessage()) {
                chatText.append("You: ").append(message.getMessage()).append("\n\n");
            } else {
                chatText.append(characterName.get()).append(": ").append(message.getMessage()).append("\n\n");
            }
        }
        chatTextView.setText(chatText.toString());

        // 滚动到底部
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }
}