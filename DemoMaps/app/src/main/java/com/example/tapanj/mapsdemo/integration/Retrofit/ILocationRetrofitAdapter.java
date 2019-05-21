package com.example.tapanj.mapsdemo.integration.Retrofit;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.models.backendModels.request.location.InitiateEmergencySessionForSelfRequestModel;
import com.example.tapanj.mapsdemo.models.backendModels.request.location.ReportOneTimeLocationRequestModel;
import com.example.tapanj.mapsdemo.models.backendModels.response.ServiceResponseModel;
import com.example.tapanj.mapsdemo.models.backendModels.response.location.InitiateLocationCaptureResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ILocationRetrofitAdapter {
    @PUT("location/reportLocation")
    LiveData<ApiResponse<ServiceResponseModel>> reportLocation(@Body ReportOneTimeLocationRequestModel oneTimeLocationRequestModel);

    @POST("location/initiateEmergency")
    LiveData<ApiResponse<InitiateLocationCaptureResponseModel>> initiateEmergencyForSelf(@Body InitiateEmergencySessionForSelfRequestModel requestModel);
}
