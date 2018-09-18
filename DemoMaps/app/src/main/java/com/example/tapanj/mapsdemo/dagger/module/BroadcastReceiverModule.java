package com.example.tapanj.mapsdemo.dagger.module;

import com.example.tapanj.mapsdemo.broadcastreceiver.FetchCurrentLocationAlarmReceiver;
import com.example.tapanj.mapsdemo.dagger.scope.ActivityScope;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BroadcastReceiverModule {
    @ActivityScope
    @ContributesAndroidInjector
    abstract FetchCurrentLocationAlarmReceiver contributeFetchCurrentLocationAlartReceiver();
}
