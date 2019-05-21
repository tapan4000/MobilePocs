package com.example.tapanj.mapsdemo.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.tapanj.mapsdemo.common.AppOperationStatusCodes;
import com.example.tapanj.mapsdemo.enums.OperationStateEnum;


// A generic class that describes the data with a operationState.
// This class has been taken from the Android guide to app architecture.
public class Resource<T> {
    @NonNull public final OperationStateEnum operationState;
    @Nullable public final int serviceOperationStatus;
    @Nullable public final int appOperationStatus;
    @Nullable public final T data;
    @Nullable public final String appUnhandledErrorMessage;
    @Nullable public final int httpStatus;
    private boolean hasBeenHandled = false;
    public Resource(@NonNull OperationStateEnum operationState, @Nullable T data, @Nullable int httpStatus, @Nullable int serviceOperationStatus, @Nullable int appOperationStatus, @Nullable String appUnhandledErrorMessage){
        this.operationState = operationState;
        this.data = data;
        this.httpStatus = httpStatus;
        this.serviceOperationStatus = serviceOperationStatus;
        this.appOperationStatus = appOperationStatus;
        this.appUnhandledErrorMessage = appUnhandledErrorMessage;
    }

    public T getData(){
        return this.data;
    }
}
