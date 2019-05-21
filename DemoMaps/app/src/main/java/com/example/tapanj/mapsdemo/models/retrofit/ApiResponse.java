package com.example.tapanj.mapsdemo.models.retrofit;

import com.example.tapanj.mapsdemo.common.AppOperationStatusCodes;
import com.example.tapanj.mapsdemo.common.Constants;
import com.example.tapanj.mapsdemo.common.ServicePublicStatusCodes;
import com.example.tapanj.mapsdemo.enums.OperationStateEnum;
import retrofit2.Response;

public class ApiResponse<T> {
    public T responseData;
    public String errorMessage;
    public int httpStatus;

    public ApiResponse(Response<T> response){
        this.responseData = response.body();
        this.httpStatus = response.code();

        if(null != response.errorBody()){
            this.errorMessage = response.errorBody().toString();
        }
    }

    public ApiResponse(String requestInformation, Throwable throwable){
        this.responseData = null;
        this.errorMessage = requestInformation + "---Cause:" + throwable.getCause().toString() + ". Message: " + throwable.getMessage();

        // Set the http operationState to a value as a known int to indicate there was an error however, http operationState code is not
        // available for the same. In such cases the error appUnhandledErrorMessage can give more information.
        this.httpStatus = AppOperationStatusCodes.APICALLFAILURE_ERRORCODE;
    }

    public boolean isSuccessful(){
        if(this.httpStatus == ServicePublicStatusCodes.Success){
            return true;
        }

        return false;
    }
}
