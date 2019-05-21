package com.example.tapanj.mapsdemo.dagger.module;

import android.app.Application;
import com.example.tapanj.mapsdemo.common.location.LocationProvider;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationProvider;
import com.example.tapanj.mapsdemo.datastore.dao.UserLocationSessionDao;
import com.example.tapanj.mapsdemo.datastore.dao.UserLocationSessionDetailDao;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.Interfaces.ISharedPreferenceProvider;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.SharedPreferenceProvider;
import com.example.tapanj.mapsdemo.integration.Retrofit.ILocationRetrofitAdapter;
import com.example.tapanj.mapsdemo.integration.Retrofit.ServiceBuilder;
import com.example.tapanj.mapsdemo.repository.LocationRepository;
import com.example.tapanj.mapsdemo.repository.interfaces.ILocationRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class LocationModule {
//    private Application application;
//    public LocationModule(Application application){
//        this.application = application;
//    }

/*    @Singleton
    @Provides
    ISharedPreferenceProvider sharedPreferenceProvider(){
        return new SharedPreferenceProvider();
    }*/

    @Provides
    @Singleton
    ILocationProvider locationProvider(Application application){
        return new LocationProvider(application);
    }

    @Singleton
    @Provides
    ILocationRetrofitAdapter locationRetrofitAdapter(){
        return ServiceBuilder.buildService(ILocationRetrofitAdapter.class);
    }

    @Singleton
    @Provides
    ILocationRepository locationRepository(ILocationRetrofitAdapter locationRetrofitAdapter, UserLocationSessionDao userLocationSessionDao, UserLocationSessionDetailDao userLocationSessionDetailDao, ISharedPreferenceProvider sharedPreferenceProvider){
        return new LocationRepository(locationRetrofitAdapter, userLocationSessionDao, userLocationSessionDetailDao, sharedPreferenceProvider);
    }
}
