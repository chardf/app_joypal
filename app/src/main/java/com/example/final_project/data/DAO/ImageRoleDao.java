package com.example.final_project.data.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.final_project.data.model.Entity.ImageRoleEntity;

import java.util.List;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.final_project.data.model.Entity.ImageRoleEntity;

import java.util.List;

@Dao
public interface ImageRoleDao {

    // 插入一条记录
    @Insert
    void insert(ImageRoleEntity imageRole);

    // 查询所有的记录，返回 LiveData
    @Query("SELECT * FROM image_role")
    LiveData<List<ImageRoleEntity>> getAll();

    // 根据ID查询记录
    @Query("SELECT * FROM image_role WHERE id = :id")
    LiveData<ImageRoleEntity> getById(int id);
}