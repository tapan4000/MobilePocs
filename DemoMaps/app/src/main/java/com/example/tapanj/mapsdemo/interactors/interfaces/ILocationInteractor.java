package com.example.tapanj.mapsdemo.interactors.interfaces;

import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import com.example.tapanj.mapsdemo.enums.LocationCaptureSessionStateEnum;
import com.example.tapanj.mapsdemo.models.backendModels.request.location.LocationRequestModel;
import com.example.tapanj.mapsdemo.models.backendModels.response.location.InitiateLocationCaptureResponseModel;
import com.example.tapanj.mapsdemo.repository.ResourceEvent;

import java.util.Date;

public interface ILocationInteractor {
    LiveData<ResourceEvent<InitiateLocationCaptureResponseModel>> initiateEmergencyForSelf(SharedPreferences sharedPreferences, String emergencySessionTitle
            , LocationCaptureSessionStateEnum locationCaptureSessionState
            , int groupId
            , Date requestDateTime
            , int locationCapturePeriodInSeconds
            , LocationRequestModel location);
}
