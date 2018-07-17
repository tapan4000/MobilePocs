package com.example.tapanj.mapsdemo.models;

public class IntentServiceResult {
    public int ResultCode;
    public String ResultMessage;

    public IntentServiceResult(int resultCode, String resultMessage){
        this.ResultCode = resultCode;
        this.ResultMessage = resultMessage;
    }
}
