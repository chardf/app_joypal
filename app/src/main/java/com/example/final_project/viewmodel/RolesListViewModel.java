package com.example.final_project.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_project.data.DAO.ImageRoleDao;
import com.example.final_project.data.model.Entity.ImageRoleEntity;

import java.util.List;

public class RolesListViewModel extends ViewModel {

    private final LiveData<List<ImageRoleEntity>> roleList;

    public RolesListViewModel(ImageRoleDao imageRoleDao, String userId) {
        // 初始化角色数据，只查当前用户
        roleList = imageRoleDao.getRolesByUserId(userId);
    }

    public LiveData<List<ImageRoleEntity>> getRoleList() {
        return roleList;
    }
}