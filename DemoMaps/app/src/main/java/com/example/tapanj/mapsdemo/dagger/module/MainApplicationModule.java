package com.example.tapanj.mapsdemo.dagger.module;

import android.app.Application;
import android.content.Context;
import com.example.tapanj.mapsdemo.dagger.MainApplication;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public abstract class MainApplicationModule {
    @Binds
    abstract Context provideContext(MainApplication application);
}
