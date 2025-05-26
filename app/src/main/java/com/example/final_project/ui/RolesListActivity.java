package com.example.final_project.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.final_project.R;
import com.example.final_project.data.DAO.ImageRoleDao;
import com.example.final_project.database.AppDatabase;
import com.example.final_project.data.model.Entity.ImageRoleEntity;
import com.example.final_project.viewmodel.RolesListViewModel;
import com.example.final_project.viewmodel.RolesListViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class RolesListActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "RolePreferences";
    private static final String KEY_ROLE_NAME = "roleName";
    private static final String KEY_IMAGE_PATH = "imagePath";

    private ViewPager2 viewPager2;
    private TextView roleNameTextTop; // Top TextView for role name
    private RoleAdapter roleAdapter;
    private RolesListViewModel rolesListViewModel;
    private ImageButton prevRoleButton, nextRoleButton; // 左右按钮
    private Button beginButton; // Begin 按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_from_oclist);

        // Initialize views
        roleNameTextTop = findViewById(R.id.role_name_text);
        viewPager2 = findViewById(R.id.role_image_pager);
        prevRoleButton = findViewById(R.id.prev_role_button);
        nextRoleButton = findViewById(R.id.next_role_button);
        beginButton = findViewById(R.id.start_chat_button); // 初始化 Begin 按钮

        // 从 SharedPreferences 加载角色信息
        loadRoleInfo();

        setupBottomNavigationView();

        // 设置左右按钮点击事件
        setupNavigationButtons();

        // 设置 Begin 按钮点击事件
        setupBeginButton();
    }

    private void loadRoleInfo() {
        // 直接从数据库加载所有角色
        AppDatabase database = AppDatabase.getDatabase(this);
        ImageRoleDao imageRoleDao = database.imageRoleDao();
        RolesListViewModelFactory factory = new RolesListViewModelFactory(imageRoleDao);
        rolesListViewModel = new ViewModelProvider(this, factory).get(RolesListViewModel.class);
        rolesListViewModel.getRoleList().observe(this, this::setupAdapter);
    }

    /**
     * 初始化导航栏
     */
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置导航栏图标的默认选择项为 "home"
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

        // 为导航栏的每个选项设置监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                // 当前Home 页面

                return true;
            } else if (itemId == R.id.menu_create) {
                // 跳转到 Create 页面
                Intent intent = new Intent(RolesListActivity.this, Create_Joypet.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_joypal) {
                Intent intent = new Intent(RolesListActivity.this, joypal_chat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                return true;
            } else if (itemId == R.id.menu_settings) {
                // 跳转到 Settings 页面
                Intent intent = new Intent(RolesListActivity.this, settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    /**
     * 设置左右按钮的点击事件
     */
    private void setupNavigationButtons() {
        prevRoleButton.setOnClickListener(v -> {
            int currentItem = viewPager2.getCurrentItem();
            if (currentItem > 0) {
                viewPager2.setCurrentItem(currentItem - 1, true); // 向左滑动
            }
        });

        nextRoleButton.setOnClickListener(v -> {
            int currentItem = viewPager2.getCurrentItem();
            if (currentItem < roleAdapter.getItemCount() - 1) {
                viewPager2.setCurrentItem(currentItem + 1, true); // 向右滑动
            }
        });
    }

    /**
     * 设置 Begin 按钮的点击事件
     */
    private void setupBeginButton() {
        beginButton.setOnClickListener(v -> {
            int currentItem = viewPager2.getCurrentItem();
            if (roleAdapter != null && roleAdapter.getItemCount() > 0) {
                ImageRoleEntity selectedRole = roleAdapter.getRoleAt(currentItem);
                
                // 保存到 SharedPreferences
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_ROLE_NAME, selectedRole.getRoleName());
                editor.putString(KEY_IMAGE_PATH, selectedRole.getImagePath());
                editor.apply();

                // 跳转到聊天界面
                Intent intent = new Intent(RolesListActivity.this, joypal_chat.class);
                intent.putExtra("roleName", selectedRole.getRoleName());
                intent.putExtra("imagePath", selectedRole.getImagePath());
                startActivity(intent);
            }
        });
    }

    private void setupAdapter(List<ImageRoleEntity> roleList) {
        Log.d("RolesListActivity", "设置适配器 - 角色列表大小: " + (roleList != null ? roleList.size() : 0));
        
        if (roleList != null && !roleList.isEmpty()) {
            roleAdapter = new RoleAdapter(roleList);
            viewPager2.setAdapter(roleAdapter);
            
            // 设置初始角色名
            ImageRoleEntity firstRole = roleList.get(0);
            Log.d("RolesListActivity", "设置初始角色名: " + firstRole.getRoleName());
            roleNameTextTop.setText(firstRole.getRoleName());
            
            // 设置页面切换监听
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    ImageRoleEntity currentRole = roleList.get(position);
                    Log.d("RolesListActivity", "页面切换 - 当前角色: " + currentRole.getRoleName());
                    roleNameTextTop.setText(currentRole.getRoleName());
                }
            });
        } else {
            Log.e("RolesListActivity", "角色列表为空或为null");
        }
    }
}