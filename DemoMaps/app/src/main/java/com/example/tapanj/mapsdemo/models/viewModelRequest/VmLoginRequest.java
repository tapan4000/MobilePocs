package com.example.tapanj.mapsdemo.models.viewModelRequest;

import android.content.SharedPreferences;

public class VmLoginRequest {
    public VmLoginRequest(SharedPreferences sharedPreferences, String isdCode, String mobileNumber, String password){
        this.sharedPreferences = sharedPreferences;
        this.isdCode = isdCode;
        this.mobileNumber = mobileNumber;
        this.password = password;
    }

    private SharedPreferences sharedPreferences;
    private String isdCode;
    private String mobileNumber;
    private String password;

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public String getIsdCode() {
        return isdCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getPassword() {
        return password;
    }
}
