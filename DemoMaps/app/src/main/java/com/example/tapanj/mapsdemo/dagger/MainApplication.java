package com.example.tapanj.mapsdemo.dagger;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.support.multidex.MultiDexApplication;
import androidx.work.Worker;
import com.example.tapanj.mapsdemo.dagger.module.worker.HasWorkerInjector;
import com.jakewharton.threetenabp.AndroidThreeTen;
import dagger.android.*;
import dagger.android.support.HasSupportFragmentInjector;

import javax.inject.Inject;

public class MainApplication extends MultiDexApplication implements HasActivityInjector, HasSupportFragmentInjector, HasServiceInjector, HasWorkerInjector, HasBroadcastReceiverInjector {
    private MainApplicationComponent mainApplicationComponent;
    private String baseUrl = "http://10.0.2.2:8085/FrontEndService/api/";

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Inject
    DispatchingAndroidInjector<Worker> dispatchingWorkerInjector;

    @Inject
    DispatchingAndroidInjector<android.support.v4.app.Fragment> dispatchingFragmentInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> dispatchingBroadcastReceiverInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mainApplicationComponent = DaggerMainApplicationComponent.builder().application(this).backendUrl(this.baseUrl).build();
        this.mainApplicationComponent.inject(this);
        //DaggerMainApplicationComponent.builder().application(this).backendUrl(this.baseUrl).build().inject(this);

        AndroidThreeTen.init(this);
        /*mainApplicationComponent = DaggerMainApplicationComponent.builder()
                .netModule(new NetModule(this.baseUrl))
                .loggerModule(new LoggerModule(this))
                .locationModule(new LocationModule(this))
                .dataStoreModule(new DataStoreModule(this))
                .groupManagementModule(new GroupManagementModule()).build();*/
//        mainApplicationComponent = DaggerMainApplicationComponent.builder()
//                .application(this)
//                .netModule(new NetModule(this.baseUrl))
//                .build();
//        mainApplicationComponent.inject(this);
    }

    public MainApplicationComponent getMainApplicationComponent() {
        return mainApplicationComponent;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }

    @Override
    public AndroidInjector<Worker> workerInjector() {
        return dispatchingWorkerInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return dispatchingBroadcastReceiverInjector;
    }

    @Override
    public AndroidInjector<android.support.v4.app.Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }
}
