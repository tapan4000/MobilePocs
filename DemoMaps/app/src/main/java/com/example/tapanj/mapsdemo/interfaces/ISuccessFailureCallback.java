package com.example.tapanj.mapsdemo.interfaces;

public interface ISuccessFailureCallback<T> {
    void onSuccess(T data);
    void onFailure(String failureMessage);
}
