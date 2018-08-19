package com.example.tapanj.mapsdemo.dagger;

import android.support.multidex.MultiDexApplication;
import com.example.tapanj.mapsdemo.dagger.module.*;

public class MainApplication extends MultiDexApplication {
    private MainApplicationComponent mainApplicationComponent;
    private String baseUrl = "http://10.0.2.2:8080/FrontEndService/api/";

    @Override
    public void onCreate() {
        super.onCreate();
        /*mainApplicationComponent = DaggerMainApplicationComponent.builder()
                .roomModule(new RoomModule(this))
                .netModule(new NetModule(this.baseUrl))
                .loggerModule(new LoggerModule(this))
                .locationModule(new LocationModule(this))
                .dataStoreModule(new DataStoreModule(this))
                .groupManagementModule(new GroupManagementModule()).build();*/
        mainApplicationComponent = DaggerMainApplicationComponent.builder()
                .application(this)
                .netModule(new NetModule(this.baseUrl))
                .build();
        mainApplicationComponent.inject(this);
    }

    public MainApplicationComponent getMainApplicationComponent() {
        return mainApplicationComponent;
    }
}
