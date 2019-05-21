package com.example.tapanj.mapsdemo.models.backendModels.request.location;

import com.example.tapanj.mapsdemo.models.backendModels.request.location.LocationRequestModel;
import com.google.gson.annotations.SerializedName;

public class ReportOneTimeLocationRequestModel {
    @SerializedName("location")
    public LocationRequestModel Location;
}
