package com.example.tapanj.mapsdemo.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;

public abstract class NetworkBoundResource<ResourceType, ApiResponseType> {
    private final MediatorLiveData<Resource<ResourceType>> result = new MediatorLiveData<>();

    @MainThread
    NetworkBoundResource(){
        result.setValue(Resource.<ResourceType>loading(null));
        final LiveData<ResourceType> dbSource = this.loadFromDb();
        result.addSource(dbSource, new Observer<ResourceType>() {
            @Override
            public void onChanged(@Nullable ResourceType data) {
                result.removeSource(dbSource);
                if(shouldFetch(data)){
                    fetchFromNetwork(dbSource);
                }
                else{
                    result.addSource(dbSource, new Observer<ResourceType>() {
                        @Override
                        public void onChanged(@Nullable ResourceType newData) {
                            result.setValue(Resource.success(newData));
                        }
                    });
                }
            }
        });
    }

    private void fetchFromNetwork(final LiveData<ResourceType> dbSource) {
        final LiveData<ApiResponse<ApiResponseType>> apiResponse = createApiCall();

        // We reattach dbsource as the new source, it will dispatch its latest value quickly.
        result.addSource(dbSource, new Observer<ResourceType>() {
            @Override
            public void onChanged(@Nullable ResourceType newData) {
                result.setValue(Resource.loading(newData));
            }
        });

        result.addSource(apiResponse, new Observer<ApiResponse<ApiResponseType>>() {
            @Override
            public void onChanged(@Nullable final ApiResponse<ApiResponseType> response) {
                result.removeSource(apiResponse);
                result.removeSource(dbSource);

                if(response.isSuccessful()){
                    saveResultAndReInit(response);
                }
                else{
                    onFetchFailed();
                    result.addSource(dbSource, new Observer<ResourceType>() {
                        @Override
                        public void onChanged(@Nullable ResourceType newData) {
                            result.setValue(Resource.error(response.errorMessage, newData));
                        }
                    });
                }
            }
        });
    }

    @MainThread
    private void saveResultAndReInit(final ApiResponse<ApiResponseType> response){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                saveCallResultToDatabase(response.responseData);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // We specially request a new live data, otherwise we will get immidiately last cached value,
                // which may not be updated with the latest results received from the network.
                result.addSource(loadFromDb(), new Observer<ResourceType>() {
                    @Override
                    public void onChanged(@Nullable ResourceType newData) {
                        result.setValue(Resource.success(newData));
                    }
                });
            }
        }.execute();
    }

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResultToDatabase(@NonNull ApiResponseType item);

    // Called with the data in the database to decide whether it should be fetched from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResourceType data);

    // Called to get the cached data from the database
    @NonNull
    @MainThread
    protected abstract LiveData<ResourceType> loadFromDb();

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<ApiResponseType>> createApiCall();

    // Called when the fetch fails. The child class may want to reset components like rate limiter.
    @MainThread
    protected void onFetchFailed(){
    }

    // Returns the live data that represents the resource, implemented in the base class.
    public final LiveData<Resource<ResourceType>> getAsLiveData(){
        return result;
    }
}
