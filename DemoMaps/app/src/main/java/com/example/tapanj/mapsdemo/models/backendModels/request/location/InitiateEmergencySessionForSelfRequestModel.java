package com.example.tapanj.mapsdemo.models.backendModels.request.location;

import com.example.tapanj.mapsdemo.enums.LocationCaptureSessionStateEnum;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class InitiateEmergencySessionForSelfRequestModel {
    @SerializedName("captureTitle")
    public String EmergencySessionTitle;

    @SerializedName("captureState")
    public LocationCaptureSessionStateEnum LocationCaptureSessionState;

    @SerializedName("groupId")
    public int GroupId;

    @SerializedName("time")
    public Date RequestDateTime;

    @SerializedName("captureDuration")
    public int LocationCapturePeriodInSeconds;

    @SerializedName("location")
    public LocationRequestModel Location;
}
