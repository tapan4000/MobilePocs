package com.example.tapanj.mapsdemo.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import com.example.tapanj.mapsdemo.models.backendModels.response.ServiceResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import com.example.tapanj.mapsdemo.models.viewModelRequest.VmLocation;
import com.example.tapanj.mapsdemo.repository.interfaces.ILocationRepository;

import javax.inject.Inject;

public class LocationViewModel extends ViewModel {
    private final MutableLiveData<VmLocation> locationTrigger = new MutableLiveData<>();

    private ILocationRepository locationRepository;

    @Inject
    public LocationViewModel(ILocationRepository locationRepository){
        this.locationRepository = locationRepository;
    }

    // We are using switchmap here as if we use map, then we have to return a value coming out of locationTrigger (i.e. it cannot be another livedata).
    // However, if we use the swithmap we can return another livedata (as a backing store for response live data). In this case, the backing
    // store is the repository method named reportOneTimeLocation
    public LiveData<ApiResponse<ServiceResponseModel>> reportOneTimeLocationResponse = Transformations.switchMap(locationTrigger, (locationRequest) -> {
        return this.locationRepository.reportOneTimeLocation(locationRequest.latitude, locationRequest.longitude, locationRequest.altitude, locationRequest.speed);
    });

    public void reportOneTimeLocation(String latitude, String longitude, String altitude, String speed){
        if(null == latitude){
            return;
        }

        locationTrigger.setValue(new VmLocation());
    }
}
