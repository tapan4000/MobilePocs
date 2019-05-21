package com.example.tapanj.mapsdemo.repository.interfaces;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.models.backendModels.response.ServiceResponseModel;
import com.example.tapanj.mapsdemo.models.dao.UserLocationSession;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import com.example.tapanj.mapsdemo.repository.ResourceEvent;

import java.util.List;

public interface ILocationRepository {
    LiveData<ApiResponse<ServiceResponseModel>> reportOneTimeLocation(String latitude, String longitude, String altitude, String speed);

    LiveData<ResourceEvent<List<UserLocationSession>>> getAllLocationSessionsByUserId(int userId);
}
