package com.example.tapanj.mapsdemo.models.backendModels.response.location;

import com.example.tapanj.mapsdemo.models.backendModels.response.ServiceResponseModel;
import com.google.gson.annotations.SerializedName;

public class InitiateLocationCaptureResponseModel extends ServiceResponseModel {
    @SerializedName("locationRequestId")
    public int LocationRequestId;
}
