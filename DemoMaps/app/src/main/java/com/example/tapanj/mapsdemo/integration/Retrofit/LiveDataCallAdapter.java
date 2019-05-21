package com.example.tapanj.mapsdemo.integration.Retrofit;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

final class LiveDataCallAdapter<ApiResponseType> implements CallAdapter<ApiResponseType, LiveData<ApiResponse<ApiResponseType>>> {
    private final Type responseType;
    public LiveDataCallAdapter(Type responseType){
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return this.responseType;
    }

    @Override
    public LiveData<ApiResponse<ApiResponseType>> adapt(final Call<ApiResponseType> call) {
        return new LiveData<ApiResponse<ApiResponseType>>() {
            AtomicBoolean started = new AtomicBoolean(false);
            @Override
            protected void onActive() {
                super.onActive();
                if(started.compareAndSet(false, true)){
                    call.enqueue(new Callback<ApiResponseType>() {
                        @Override
                        public void onResponse(Call<ApiResponseType> call, Response<ApiResponseType> response) {
                            postValue(new ApiResponse<ApiResponseType>(response));
                        }

                        @Override
                        public void onFailure(Call<ApiResponseType> call, Throwable t) {
                            String requestInformation = "Url: " + call.request().url() + ", Body:" + call.request().body().toString() + ", Headers:" + call.request().headers().toString();
                            postValue(new ApiResponse<>(requestInformation, t));
                        }
                    });
                }
            }
        };
    }
}
