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

    // 构造函数、getter 和 setter
    public ImageRoleEntity(String imagePath, String roleName) {
        this.imagePath = imagePath;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    // 实现 Parcelable 接口
    protected ImageRoleEntity(Parcel in) {
        id = in.readInt();
        imagePath = in.readString();
        roleName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imagePath);
        dest.writeString(roleName);
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
}
