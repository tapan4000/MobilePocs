package com.example.tapanj.mapsdemo.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.Interfaces.ISharedPreferenceProvider;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.SharedPreferenceConstants;
import com.example.tapanj.mapsdemo.integration.Retrofit.IUserRetrofitAdapter;
import com.example.tapanj.mapsdemo.repository.interfaces.IUserRepository;
import com.example.tapanj.mapsdemo.models.backendModels.request.loginRegister.LoginUserRequestModel;
import com.example.tapanj.mapsdemo.models.user.User;
import com.example.tapanj.mapsdemo.models.backendModels.response.LoginRegister.LoginUserResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import com.example.tapanj.mapsdemo.models.user.UserAuthInfo;
import com.example.tapanj.mapsdemo.models.user.UserInfo;
import com.google.gson.reflect.TypeToken;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository implements IUserRepository {
    private final IUserRetrofitAdapter userRetrofitAdapter;
    private final ISharedPreferenceProvider sharedPreferenceProvider;
    private final MutableLiveData<User> userLiveData = new MutableLiveData();
    private final MutableLiveData<User> loggedInUserDetails = new MutableLiveData<>();

    @Inject
    public UserRepository(IUserRetrofitAdapter userRetrofitAdapter, ISharedPreferenceProvider sharedPreferenceProvider){
        this.userRetrofitAdapter = userRetrofitAdapter;
        this.sharedPreferenceProvider = sharedPreferenceProvider;
    }

    @Override
    public LiveData<User> getLoggedInUser(SharedPreferences sharedPreferences) {

        UserInfo userInfo = sharedPreferenceProvider.FetchSharedPreference(sharedPreferences, SharedPreferenceConstants.UserInfoKeyName, new TypeToken<UserInfo>(){});
        UserAuthInfo userAuthInfo = sharedPreferenceProvider.FetchSharedPreference(sharedPreferences, SharedPreferenceConstants.UserAuthInfoKeyName, new TypeToken<UserAuthInfo>(){});
        User user = new User(userInfo, userAuthInfo);
        loggedInUserDetails.postValue(user);
        return loggedInUserDetails;
    }

    @Override
    public LiveData<ApiResponse<LoginUserResponseModel>> loginUserV2(SharedPreferences sharedPreferences, String isdCode, String mobileNumber, String passwordHash) {
        LoginUserRequestModel loginRequest = new LoginUserRequestModel();
        loginRequest.IsdCode = isdCode;
        loginRequest.MobileNumber = mobileNumber;
        loginRequest.UserPasswordHash = passwordHash;
        return userRetrofitAdapter.loginUser(loginRequest);
    }

    @Override
    public LiveData<ResourceEvent<User>> loginUser(SharedPreferences sharedPreferences, String isdCode, String mobileNumber, String passwordHash) {
        return new NetworkBoundResource<User, LoginUserResponseModel>(){

            @Override
            protected void saveCallResultToDataStore(@NonNull LoginUserResponseModel response) {
                // Map the LoginUserResponseModel to UserInfo object and store the object in shared preferences.
                UserInfo userInfo = new UserInfo();
                userInfo.FirstName = response.FirstName;
                userInfo.LastName = response.LastName;
                userInfo.IsdCode = response.IsdCode;
                userInfo.MobileNumber = response.MobileNumber;
                userInfo.Email = response.Email;
                userInfo.UserStateId = response.UserStateId;
                userInfo.MembershipTierId = response.MembershipTierId;
                userInfo.UserId = response.UserId;
                sharedPreferenceProvider.StoreSharedPreference(sharedPreferences, SharedPreferenceConstants.UserInfoKeyName, userInfo);

                // Map the LoginUserResponseModel to the UserAuthInfo object and store the object in shared prederences.
                UserAuthInfo userAuthInfo = new UserAuthInfo();
                userAuthInfo.UserAuthToken = response.UserAuthToken;
                userAuthInfo.AuthTokenExpirationDateTime = response.AuthTokenExpirationDateTime;
                userAuthInfo.RefreshToken = response.RefreshToken;
                userAuthInfo.RefreshTokenCreationDateTime = response.RefreshTokenCreationDateTime;
                sharedPreferenceProvider.StoreSharedPreference(sharedPreferences, SharedPreferenceConstants.UserAuthInfoKeyName, userAuthInfo);
            }

            @Override
            protected boolean shouldFetch(@Nullable User localData) {
                // On an explicit login request, we always need to fetch data from the server.
                return true;
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDataStore() {
                // Fetch the userInfo and UserAuth details from the shared preferences
                UserInfo userInfo = sharedPreferenceProvider.FetchSharedPreference(sharedPreferences, SharedPreferenceConstants.UserInfoKeyName, new TypeToken<UserInfo>(){});
                UserAuthInfo userAuthInfo = sharedPreferenceProvider.FetchSharedPreference(sharedPreferences, SharedPreferenceConstants.UserAuthInfoKeyName, new TypeToken<UserAuthInfo>(){});
                User user = new User(userInfo, userAuthInfo);
                userLiveData.postValue(user);
                return userLiveData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<LoginUserResponseModel>> createApiCall() {
                LoginUserRequestModel loginRequest = new LoginUserRequestModel();
                loginRequest.IsdCode = isdCode;
                loginRequest.MobileNumber = mobileNumber;
                loginRequest.UserPasswordHash = passwordHash;
                return userRetrofitAdapter.loginUser(loginRequest);
            }
        }.getAsLiveData();
    }

    @Override
    public void insertUser() {

    }
}
