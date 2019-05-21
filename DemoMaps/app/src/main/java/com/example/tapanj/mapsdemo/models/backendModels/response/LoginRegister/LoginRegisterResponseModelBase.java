package com.example.tapanj.mapsdemo.models.backendModels.response.LoginRegister;

import com.example.tapanj.mapsdemo.models.backendModels.response.ServiceResponseModel;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public abstract class LoginRegisterResponseModelBase extends ServiceResponseModel {
    @SerializedName("token")
    public String UserAuthToken;

    @SerializedName("tokenExpiry")
    public Date AuthTokenExpirationDateTime;

    @SerializedName("refreshToken")
    public String RefreshToken;

    @SerializedName("refreshTime")
    public long RefreshTokenCreationDateTime;

    // UserTable profile related data to be shown on user mobile settings page.
    @SerializedName("userId")
    public int UserId;

    @SerializedName("firstName")
    public String FirstName;

    @SerializedName("lastName")
    public String LastName;

    @SerializedName("isdCode")
    public String IsdCode;

    @SerializedName("mobileNumber")
    public String MobileNumber;

    @SerializedName("email")
    public String Email;

    @SerializedName("userStateId")
    public int UserStateId;

    @SerializedName("membershipTierId")
    public int MembershipTierId;
}
