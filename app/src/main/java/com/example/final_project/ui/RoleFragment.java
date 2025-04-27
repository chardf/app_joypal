package com.example.final_project.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.final_project.R;
import com.example.final_project.data.model.Entity.ImageRoleEntity;

public class RoleFragment extends Fragment {

    private static final String ARG_ROLE = "role";

    public static RoleFragment newInstance(ImageRoleEntity role) {
        RoleFragment fragment = new RoleFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_from_oclist, container, false);

        ImageRoleEntity role = getArguments().getParcelable(ARG_ROLE);

        TextView roleNameTextView = view.findViewById(R.id.role_name_text);
        ImageView roleImageView = view.findViewById(R.id.role_image_pager);

        roleNameTextView.setText(role.getRoleName());
        Glide.with(this).load(role.getImagePath()).into(roleImageView);

        return view;
    }
}
