package com.example.tapanj.mapsdemo.repository;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import com.example.tapanj.mapsdemo.models.backendModels.response.ServiceResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;

public abstract class NetworkBoundResource<ResourceType, ApiResponseType extends ServiceResponseModel> {
    private final MediatorLiveData<ResourceEvent<ResourceType>> result = new MediatorLiveData<>();

    @MainThread
    NetworkBoundResource(){
        result.setValue(ResourceEvent.<ResourceType>loading(null));
        // TODO: Handle the errors coming our of making a call to the database and set the appOperationStatus code accordingly.
        final LiveData<ResourceType> dataStoreContent = this.loadFromDataStore();
        result.addSource(dataStoreContent, new Observer<ResourceType>() {
            @Override
            public void onChanged(@Nullable ResourceType data) {
                result.removeSource(dataStoreContent);
                if(shouldFetch(data)){
                    fetchFromNetwork(dataStoreContent);
                }
                else{
                    result.addSource(dataStoreContent, new Observer<ResourceType>() {
                        @Override
                        public void onChanged(@Nullable ResourceType newData) {
                            result.setValue(ResourceEvent.successAppOperation(newData));
                        }
                    });
                }
            }
        });
    }

    private void fetchFromNetwork(final LiveData<ResourceType> dataStoreContent) {
        final LiveData<ApiResponse<ApiResponseType>> apiResponse = createApiCall();

        // We reattach dbsource as the new source, it will dispatch its latest value quickly.
        result.addSource(dataStoreContent, new Observer<ResourceType>() {
            @Override
            public void onChanged(@Nullable ResourceType newData) {
                result.setValue(ResourceEvent.loading(newData));
            }
        });

        result.addSource(apiResponse, new Observer<ApiResponse<ApiResponseType>>() {
            @Override
            public void onChanged(@Nullable final ApiResponse<ApiResponseType> response) {
                result.removeSource(apiResponse);
                result.removeSource(dataStoreContent);

                if(response.isSuccessful()){
                    saveResultAndReInitPostSuccess(response);
                }
                else{
                    onFetchFailed();
                    result.addSource(dataStoreContent, new Observer<ResourceType>() {
                        @Override
                        public void onChanged(@Nullable ResourceType newData) {
                            int serviceOperationStatus = 0;
                            if(null != response.responseData){
                                serviceOperationStatus = response.responseData.Status;
                            }
                            result.setValue(ResourceEvent.error(newData, response.httpStatus, serviceOperationStatus, response.errorMessage));
                        }
                    });
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    private void saveResultAndReInitPostSuccess(final ApiResponse<ApiResponseType> response){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                saveCallResultToDataStore(response.responseData);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // We specially request a new live data, otherwise we will get immidiately last cached value,
                // which may not be updated with the latest results received from the network.
                result.addSource(loadFromDataStore(), new Observer<ResourceType>() {
                    @Override
                    public void onChanged(@Nullable ResourceType newData) {
                        result.setValue(ResourceEvent.success(newData, response.httpStatus, response.responseData.Status));
                    }
                });
            }
        }.execute();
    }

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResultToDataStore(@NonNull ApiResponseType response);

    // Called with the data in the database to decide whether it should be fetched from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResourceType localData);

    // Called to get the cached data from the database
    @NonNull
    @MainThread
    protected abstract LiveData<ResourceType> loadFromDataStore();

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<ApiResponseType>> createApiCall();

    // Called when the fetch fails. The child class may want to reset components like rate limiter.
    @MainThread
    protected void onFetchFailed(){
    }

    // Returns the live data that represents the resource, implemented in the base class.
    public final LiveData<ResourceEvent<ResourceType>> getAsLiveData(){
        return result;
    }
}
