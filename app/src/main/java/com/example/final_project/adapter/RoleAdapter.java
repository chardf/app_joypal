package com.example.final_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.final_project.R;
import com.example.final_project.data.model.Entity.ImageRoleEntity;

import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import android.util.Log;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.RoleViewHolder> {

    private final List<ImageRoleEntity> roleList;

    public RoleAdapter(List<ImageRoleEntity> roleList) {
        this.roleList = roleList;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_role_pager, parent, false);
        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        ImageRoleEntity role = roleList.get(position);
        Log.d("RoleAdapter", "绑定角色 - 位置: " + position +
                ", 角色名: " + role.getName() +
                ", 图片路径: " + role.getImagePath());

        // 加载图片
        if (role.getImagePath() != null && !role.getImagePath().isEmpty()) {
            File imgFile = new File(role.getImagePath());
            if (imgFile.exists()) {
                Glide.with(holder.itemView.getContext())
                        .load(imgFile)
                        .into(holder.roleImage);
            } else {
                Log.e("RoleAdapter", "图片文件不存在: " + role.getImagePath());
                holder.roleImage.setImageResource(R.drawable.default_avatar); // 设置默认图片
            }
        } else {
            Log.e("RoleAdapter", "图片路径为空");
            holder.roleImage.setImageResource(R.drawable.default_avatar); // 设置默认图片
        }
    }

    @Override
    public int getItemCount() {
        return roleList.size();
    }

    /**
     * Get the ImageRoleEntity at a specific position.
     */
    public ImageRoleEntity getRoleAt(int position) {
        if (position >= 0 && position < roleList.size()) {
            return roleList.get(position);
        } else {
            throw new IndexOutOfBoundsException("Invalid position: " + position);
        }
    }

    static class RoleViewHolder extends RecyclerView.ViewHolder {
        ImageView roleImage; // Role image

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            roleImage = itemView.findViewById(R.id.role_image_view);
        }
    }
} 