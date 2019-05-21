package com.example.tapanj.mapsdemo.integration.Retrofit;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.integration.Retrofit.LiveDataCallAdapter;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class LiveDataAdapterFactory extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if(getRawType(returnType) != LiveData.class){
            return null;
        }

        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if(rawObservableType != ApiResponse.class){
            throw new IllegalArgumentException("Type must be a resource.");
        }

        if(!(observableType instanceof ParameterizedType)){
            throw new IllegalArgumentException("Resource must be parameterized.");
        }

        Type bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
        return new LiveDataCallAdapter<>(bodyType);
    }
}
