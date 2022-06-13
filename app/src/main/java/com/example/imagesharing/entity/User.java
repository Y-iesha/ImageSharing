package com.example.imagesharing.entity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

//定义用户类继承BmobUser类
public class User extends BmobUser {

    private BmobFile avatar;                      //添加用户头像的信息

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }
}