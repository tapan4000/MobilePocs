package com.example.tapanj.mapsdemo.viewmodel;

import android.arch.lifecycle.*;
import android.content.SharedPreferences;
import com.example.tapanj.mapsdemo.repository.ResourceEvent;
import com.example.tapanj.mapsdemo.repository.interfaces.IUserRepository;
import com.example.tapanj.mapsdemo.models.user.User;
import com.example.tapanj.mapsdemo.models.viewModelRequest.VmLoginRequest;
import com.example.tapanj.mapsdemo.repository.Resource;

import javax.inject.Inject;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<VmLoginRequest> userLoginTrigger = new MutableLiveData();
    public LiveData<ResourceEvent<User>> loginResponse = Transformations.switchMap(userLoginTrigger, (loginRequest) -> {
        return this.userRepository.loginUser(loginRequest.getSharedPreferences(), loginRequest.getIsdCode(), loginRequest.getMobileNumber(), loginRequest.getPassword());
    });

    IUserRepository userRepository;

/*
    public LiveData<Resource<User>> loginUserPublic(SharedPreferences sharedPreferences, String isdCode, String mobileNumber, String password){

        return Transformations.switchMap(this.loggedInUserDetails, (loggedInUser) ->{
            if(null == this.loggedInUserDetails){
                loginUser(sharedPreferences, isdCode, mobileNumber, password);
            }

            return this.loggedInUserDetails;
        });
    }*/

    @Inject
    public UserViewModel(IUserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void loginUser(SharedPreferences sharedPreferences, String isdCode, String mobileNumber, String password){
        // If an explicit login request is made, it should make a call to the server to perform the login operation
        // and the data from the server should be updated locally once the call succeeds.
        // TODO: Generate the password hash using the password.
        String passwordHash = password;
        userLoginTrigger.setValue(new VmLoginRequest(sharedPreferences, isdCode, mobileNumber, password));
        //this.loggedInUserDetails = this.userRepository.loginUser(sharedPreferences, isdCode, mobileNumber, passwordHash);
        //return this.loggedInUserDetails;
    }
/*
    public LiveData<ApiResponse<LoginUserResponseModel>> loginUserV2(SharedPreferences sharedPreferences, String isdCode, String mobileNumber, String password){
        // If an explicit login request is made, it should make a call to the server to perform the login operation
        // and the data from the server should be updated locally once the call succeeds.
        // TODO: Generate the password hash using the password.
        String passwordHash = password;

        LiveData<ApiResponse<LoginUserResponseModel>> userResource = this.userRepository
                .loginUserV2(sharedPreferences, isdCode, mobileNumber, passwordHash)
                .observe(UserViewModel.this, new Observer<ApiResponse<LoginUserResponseModel>>() {
                    @Override
                    public void onChanged(@Nullable ApiResponse<LoginUserResponseModel> loginUserResponseModelApiResponse) {

                    }
                });
        return userResource;
    }*/
}
