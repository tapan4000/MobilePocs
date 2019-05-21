package com.example.tapanj.mapsdemo.repository.interfaces;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.tapanj.mapsdemo.models.backendModels.response.LoginRegister.LoginUserResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import com.example.tapanj.mapsdemo.models.user.User;
import com.example.tapanj.mapsdemo.models.user.UserTable;
import com.example.tapanj.mapsdemo.repository.Resource;
import com.example.tapanj.mapsdemo.repository.ResourceEvent;

public interface IUserRepository {
    LiveData<User> getLoggedInUser(SharedPreferences sharedPreferences);
    LiveData<ResourceEvent<User>> loginUser(SharedPreferences sharedPreferences, String isdCode, String mobileNumber, String password);
    LiveData<ApiResponse<LoginUserResponseModel>> loginUserV2(SharedPreferences sharedPreferences, String isdCode, String mobileNumber, String password);
    void insertUser();
}
