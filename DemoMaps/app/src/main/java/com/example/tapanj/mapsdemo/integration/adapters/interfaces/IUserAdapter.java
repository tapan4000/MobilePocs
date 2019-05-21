package com.example.tapanj.mapsdemo.integration.adapters.interfaces;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.models.backendModels.response.LoginRegister.LoginUserResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;

public interface IUserAdapter {
    LiveData<ApiResponse<LoginUserResponseModel>> LoginUser(String isdCode, String mobileNumber, String passwordHash);
    LiveData<ApiResponse<LoginUserResponseModel>> LoginUserByToken(String token);
}
