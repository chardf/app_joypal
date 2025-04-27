package com.example.final_project.ui;

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

import java.util.List;
public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.RoleViewHolder> {

    private final List<ImageRoleEntity> roleList;

    public RoleAdapter(List<ImageRoleEntity> roleList) {
        this.roleList = roleList;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in ViewPager2
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_role_pager, parent, false);
        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        // Bind the data for each page in ViewPager2
        ImageRoleEntity role = roleList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(role.getImagePath())
                .into(holder.roleImage);
    }

    @Override
    public int getItemCount() {
        // Return the number of items (pages) in ViewPager2
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
        TextView roleName; // Role name
        ImageView roleImage; // Role image

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            roleName = itemView.findViewById(R.id.role_name_text);
            roleImage = itemView.findViewById(R.id.role_image_view);
        }
    }
}