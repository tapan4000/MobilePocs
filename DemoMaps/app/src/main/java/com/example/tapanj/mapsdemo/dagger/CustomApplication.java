package com.example.tapanj.mapsdemo.dagger;

import android.app.Application;

public class CustomApplication extends Application {
    private CustomApplicationComponent customApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        customApplicationComponent = DaggerCustomApplicationComponent.builder().loggerModule(new LoggerModule(this)).build();
        customApplicationComponent.inject(this);
    }

    public CustomApplicationComponent getCustomApplicationComponent() {
        return customApplicationComponent;
    }
}
