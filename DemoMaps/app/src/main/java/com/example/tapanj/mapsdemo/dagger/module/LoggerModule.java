package com.example.tapanj.mapsdemo.dagger.module;

import android.app.Application;
import com.example.tapanj.mapsdemo.common.logging.EventLogger;
import com.example.tapanj.mapsdemo.common.logging.FileLogProvider;
import com.example.tapanj.mapsdemo.common.logging.interfaces.ILogProvider;
import com.example.tapanj.mapsdemo.common.logging.interfaces.ILogger;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class LoggerModule {
//    private Application mApplication;
//    public LoggerModule(Application application){
//        this.mApplication = application;
//    }

    @Provides
    @Singleton
    ILogProvider logProvider(Application application){
        return new FileLogProvider(application);
    }

    @Provides
    @Singleton
    ILogger logger(ILogProvider logProvider){
        return new EventLogger(logProvider);
    }
}
