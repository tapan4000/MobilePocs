package com.example.tapanj.mapsdemo.models.backendModels.request.location;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class LocationRequestModel {
    @SerializedName("latitude")
    public String EncryptedLatitude;

    @SerializedName("longitude")
    public String EncryptedLongitude;

    @SerializedName("speed")
    public String EncryptedSpeed;

    @SerializedName("altitude")
    public String EncryptedAltitude;

    @SerializedName("timestamp")
    public Date TimeStamp;
}
