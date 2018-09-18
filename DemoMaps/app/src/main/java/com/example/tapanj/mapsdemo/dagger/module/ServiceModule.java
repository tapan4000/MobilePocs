package com.example.tapanj.mapsdemo.dagger.module;

import com.example.tapanj.mapsdemo.dagger.scope.ActivityScope;
import com.example.tapanj.mapsdemo.intentservice.FetchAddressIntentService;
import com.example.tapanj.mapsdemo.intentservice.FetchCurrentLocationIntentService;
import com.example.tapanj.mapsdemo.intentservice.GeofenceTransitionIntentService;
import com.example.tapanj.mapsdemo.service.FetchCurrentLocationService;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceModule {
    @ActivityScope
    @ContributesAndroidInjector
    abstract FetchAddressIntentService contributeFetchAddressIntentServiceInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract GeofenceTransitionIntentService contibuteGeofenceTransitionIntentServiceInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract FetchCurrentLocationIntentService contributeFetchCurrentLocationIntentServiceInjector();

    @ActivityScope
    @ContributesAndroidInjector
    abstract FetchCurrentLocationService contributeFetchCurrentLocationServiceInjector();
}
