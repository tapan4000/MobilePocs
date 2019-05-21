package com.example.tapanj.mapsdemo.integration.Retrofit;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.models.backendModels.request.loginRegister.LoginUserRequestModel;
import com.example.tapanj.mapsdemo.models.backendModels.response.LoginRegister.LoginUserResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface IUserRetrofitAdapter {
    @PUT("anon/loginUser")
    LiveData<ApiResponse<LoginUserResponseModel>> loginUser(@Body LoginUserRequestModel loginUserRequest);
}
