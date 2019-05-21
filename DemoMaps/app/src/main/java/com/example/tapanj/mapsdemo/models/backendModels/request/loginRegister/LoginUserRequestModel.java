package com.example.tapanj.mapsdemo.models.backendModels.request.loginRegister;

import com.google.gson.annotations.SerializedName;

public class LoginUserRequestModel {
    @SerializedName("isdCode")
    public String IsdCode;

    @SerializedName("mobileNumber")
    public String MobileNumber;

    @SerializedName("password")
    public String UserPasswordHash;
}
