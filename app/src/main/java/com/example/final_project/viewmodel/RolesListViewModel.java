package com.example.final_project.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_project.data.DAO.ImageRoleDao;
import com.example.final_project.data.model.Entity.ImageRoleEntity;

import java.util.List;

public class RolesListViewModel extends ViewModel {

    private final LiveData<List<ImageRoleEntity>> roleList;

    public RolesListViewModel(ImageRoleDao imageRoleDao) {
        // 初始化角色数据
        roleList = imageRoleDao.getAll();
    }

    public LiveData<List<ImageRoleEntity>> getRoleList() {
        return roleList;
    }
}