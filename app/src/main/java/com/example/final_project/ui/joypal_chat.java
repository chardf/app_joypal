package com.example.final_project.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;

import java.io.File;

public class joypal_chat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joypal_chat);

        // 获取传递的图片路径
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");

        // 显示图片
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                ImageView imageView = findViewById(R.id.oc_image_container);
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "Image file does not exist!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Image path not found!", Toast.LENGTH_SHORT).show();
        }
    }
}