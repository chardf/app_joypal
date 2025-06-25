package com.example.final_project.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.final_project.data.DAO.ImageRoleDao;

public class RolesListViewModelFactory implements ViewModelProvider.Factory {

    private final ImageRoleDao imageRoleDao;
    private final String userId;

    public RolesListViewModelFactory(ImageRoleDao imageRoleDao, String userId) {
        this.imageRoleDao = imageRoleDao;
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RolesListViewModel.class)) {
            return (T) new RolesListViewModel(imageRoleDao, userId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}