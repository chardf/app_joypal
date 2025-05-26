package com.example.final_project.data.model.Entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_role")
public class ImageRoleEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id; // 唯一的ID，用于标识每一条记录
    private String imagePath; // 图片路径
    private String roleName;  // 角色名称

    // 构造函数
    public ImageRoleEntity(String imagePath, String roleName) {
        this.imagePath = imagePath != null ? imagePath : "";
        this.roleName = roleName != null ? roleName : "";
    }

    // Parcelable 构造函数
    protected ImageRoleEntity(Parcel in) {
        id = in.readInt();
        imagePath = in.readString();
        roleName = in.readString();
    }

    // Parcelable 实现
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imagePath != null ? imagePath : "");
        dest.writeString(roleName != null ? roleName : "");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageRoleEntity> CREATOR = new Creator<ImageRoleEntity>() {
        @Override
        public ImageRoleEntity createFromParcel(Parcel in) {
            return new ImageRoleEntity(in);
        }

        @Override
        public ImageRoleEntity[] newArray(int size) {
            return new ImageRoleEntity[size];
        }
    };

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath != null ? imagePath : "";
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath != null ? imagePath : "";
    }

    public String getRoleName() {
        return roleName != null ? roleName : "";
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName != null ? roleName : "";
    }

    // 用于调试的toString方法
    @Override
    public String toString() {
        return "ImageRoleEntity{" +
                "id=" + id +
                ", imagePath='" + imagePath + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
