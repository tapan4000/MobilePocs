package com.example.tapanj.mapsdemo.dagger.module;

import android.app.Application;
import com.example.tapanj.mapsdemo.common.location.LocationProvider;
import com.example.tapanj.mapsdemo.interfaces.location.ILocationProvider;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class LocationModule {
//    private Application application;
//    public LocationModule(Application application){
//        this.application = application;
//    }

    @Provides
    @Singleton
    ILocationProvider locationProvider(Application application){
        return new LocationProvider(application);
    }
}
