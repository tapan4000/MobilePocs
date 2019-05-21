package com.example.tapanj.mapsdemo.models.user;

public class User {
    public User(UserInfo userInfo, UserAuthInfo userAuthInfo){
        this.UserInfo = userInfo;
        this.UserAuthInfo = userAuthInfo;
    }

    public UserInfo UserInfo;
    public UserAuthInfo UserAuthInfo;
}
