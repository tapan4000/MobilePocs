package com.example.tapanj.mapsdemo.dagger;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.support.multidex.MultiDexApplication;
import androidx.work.Worker;
import com.example.tapanj.mapsdemo.dagger.module.*;
import com.example.tapanj.mapsdemo.dagger.module.worker.HasWorkerInjector;
import com.example.tapanj.mapsdemo.workmanager.PeriodicLocationFetchWorker;
import dagger.android.*;

import javax.inject.Inject;

public class MainApplication extends MultiDexApplication implements HasActivityInjector, HasServiceInjector, HasWorkerInjector, HasBroadcastReceiverInjector {
    private MainApplicationComponent mainApplicationComponent;
    private String baseUrl = "http://10.0.2.2:8080/FrontEndService/api/";

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Inject
    DispatchingAndroidInjector<Worker> dispatchingWorkerInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> dispatchingBroadcastReceiverInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mainApplicationComponent = DaggerMainApplicationComponent.builder().application(this).backendUrl(this.baseUrl).build();
        this.mainApplicationComponent.inject(this);
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
}
