package com.example.tapanj.mapsdemo.models.retrofit;

import com.example.tapanj.mapsdemo.enums.Status;

public class ApiResponse<T> {
    public Status status;
    public T responseData;
    public String errorMessage;

    public boolean isSuccessful(){
        if(this.status == Status.SUCCESS){
            return true;
        }

        return false;
    }
    // TODO: Add a method to convert API response to liver data
}
