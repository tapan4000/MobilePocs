package com.example.tapanj.mapsdemo.integration.adapters;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.integration.Retrofit.IUserRetrofitAdapter;
import com.example.tapanj.mapsdemo.integration.adapters.interfaces.IUserAdapter;
import com.example.tapanj.mapsdemo.models.backendModels.response.LoginRegister.LoginUserResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;

import javax.inject.Inject;

public class UserRestAdapter implements IUserAdapter {
    private IUserRetrofitAdapter userRetrofitAdapter;

    @Inject
    public UserRestAdapter(IUserRetrofitAdapter userRetrofitAdapter){
        this.userRetrofitAdapter = userRetrofitAdapter;
    }

    @Override
    public LiveData<ApiResponse<LoginUserResponseModel>> LoginUser(String isdCode, String mobileNumber, String passwordHash) {
        /*Call<LoginUserResponseModel> userLoginResponse = this.userRetrofitAdapter.loginUser(isdCode, mobileNumber, passwordHash);
        userLoginResponse.enqueue(new Callback<LoginUserResponseModel>() {
            @Override
            public void onResponse(Call<LoginUserResponseModel> call, Response<LoginUserResponseModel> response) {
                int statusCode = response.code();
                LoginUserResponseModel responseData = response.body();
                return new LiveData<ApiResponse<LoginUserResponseModel>>()
            }

            @Override
            public void onFailure(Call<LoginUserResponseModel> call, Throwable t) {

            }
        });

        return userLoginResponse;*/
        return null;
    }

    @Override
    public LiveData<ApiResponse<LoginUserResponseModel>> LoginUserByToken(String token) {
        return null;
    }
}
