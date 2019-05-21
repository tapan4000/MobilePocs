package com.example.tapanj.mapsdemo.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.tapanj.mapsdemo.common.AppOperationStatusCodes;
import com.example.tapanj.mapsdemo.enums.OperationStateEnum;

public class ResourceEvent<T> {
    private boolean hasBeenHandled = false;

    private Resource<T> content;

    public ResourceEvent(Resource<T> content){
        this.content = content;
    }

    public Resource<T> getContentIfNotHandled(){
        if(this.hasBeenHandled){
            return null;
        }
        else{
            return this.content;
        }
    }

    public Resource<T> peekContent(){
        return this.content;
    }

    // Set the status as complete for an operation that is peformed while making a call to the server after it has succeeded.
    public static <T> ResourceEvent<T> success(@NonNull T data, int httpStatus, int serviceOperationStatus){
        Resource<T> content = new Resource<>(OperationStateEnum.COMPLETE, data, httpStatus, serviceOperationStatus, 0, null);
        return new ResourceEvent<>(content);
    }

    // Set the status as complete for an operation that is peformed while making a call to the server after it has failed. Additionally
    // store any unhandled exception message to be sent to the server for further analysis.
    public static <T> ResourceEvent<T> error(@Nullable T data, int httpStatus, int serviceOperationStatus, String appUnhandledErrorMessage){
        Resource<T> content = new Resource<>(OperationStateEnum.COMPLETE, data, httpStatus, serviceOperationStatus, 0, appUnhandledErrorMessage);
        return new ResourceEvent<>(content);
    }

    // Set the status as complete for an operation that is peformed while making a in-app call.
    public static <T> ResourceEvent<T> successAppOperation(@NonNull T data){
        Resource<T> content = new Resource<>(OperationStateEnum.COMPLETE, data, 0, 0, AppOperationStatusCodes.Success, null);
        return new ResourceEvent<>(content);
    }

    // Set the status as complete for an operation that is peformed while making a in-app call. If there is any unhandled exception
    // it needs to be captured for sending to the server.
    public static <T> ResourceEvent<T> errorAppOperation(@Nullable T data, int appOperationStatus, String appUnhandledErrorMessage){
        Resource<T> content = new Resource<>(OperationStateEnum.COMPLETE, data, 0, 0, appOperationStatus, appUnhandledErrorMessage);
        return new ResourceEvent<>(content);
    }

    // The loading operation is assigned a data as at times we may want to indicate the operation is not yet complete, however, still in
    // background the UI should be bound to the data from the data store while the network operation continues.
    public static <T> ResourceEvent<T> loading(@Nullable T data){
        Resource<T> content = new Resource<>(OperationStateEnum.LOADING, null, 0, 0, 0, null);
        return new ResourceEvent<>(content);
    }
}
