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
    private String name;
    private String gender;
    private String personality;
    private String appearance;

    // 构造函数
    public ImageRoleEntity(String imagePath, String roleName, String name, String gender, String personality, String appearance) {
        this.imagePath = imagePath != null ? imagePath : "";
        this.roleName = roleName != null ? roleName : "";
        this.name = name != null ? name : "";
        this.gender = gender != null ? gender : "";
        this.personality = personality != null ? personality : "";
        this.appearance = appearance != null ? appearance : "";
    }

    // Parcelable 构造函数
    protected ImageRoleEntity(Parcel in) {
        id = in.readInt();
        imagePath = in.readString();
        roleName = in.readString();
        name = in.readString();
        gender = in.readString();
        personality = in.readString();
        appearance = in.readString();
    }

    // Parcelable 实现
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imagePath != null ? imagePath : "");
        dest.writeString(roleName != null ? roleName : "");
        dest.writeString(name != null ? name : "");
        dest.writeString(gender != null ? gender : "");
        dest.writeString(personality != null ? personality : "");
        dest.writeString(appearance != null ? appearance : "");
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

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public String getGender() {
        return gender != null ? gender : "";
    }

    public void setGender(String gender) {
        this.gender = gender != null ? gender : "";
    }

    public String getPersonality() {
        return personality != null ? personality : "";
    }

    public void setPersonality(String personality) {
        this.personality = personality != null ? personality : "";
    }

    public String getAppearance() {
        return appearance != null ? appearance : "";
    }

    public void setAppearance(String appearance) {
        this.appearance = appearance != null ? appearance : "";
    }

    // 用于调试的toString方法
    @Override
    public String toString() {
        return "ImageRoleEntity{" +
                "id=" + id +
                ", imagePath='" + imagePath + '\'' +
                ", roleName='" + roleName + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", personality='" + personality + '\'' +
                ", appearance='" + appearance + '\'' +
                '}';
    }
}
