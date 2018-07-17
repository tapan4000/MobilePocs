package com.example.tapanj.mapsdemo.dagger;

import android.app.Application;
import com.example.tapanj.mapsdemo.common.logging.EventLogger;
import com.example.tapanj.mapsdemo.common.logging.FileLogProvider;
import com.example.tapanj.mapsdemo.interfaces.ILogProvider;
import com.example.tapanj.mapsdemo.interfaces.ILogger;
import dagger.Module;
import dagger.Provides;

import java.util.UUID;

@Module
public class LoggerModule {
    private Application mApplication;
    public LoggerModule(Application application){
        this.mApplication = application;
    }

    @Provides
    public ILogProvider logProvider(){
        return new FileLogProvider(this.mApplication);
    }

    @Provides
    public ILogger logger(ILogProvider logProvider){
        return new EventLogger(logProvider);
    }
}
