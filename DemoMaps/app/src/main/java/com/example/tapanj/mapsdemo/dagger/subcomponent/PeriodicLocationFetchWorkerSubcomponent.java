package com.example.tapanj.mapsdemo.dagger.subcomponent;

import android.app.Application;
import com.example.tapanj.mapsdemo.workmanager.PeriodicLocationFetchWorker;
import dagger.BindsInstance;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface PeriodicLocationFetchWorkerSubcomponent extends AndroidInjector<PeriodicLocationFetchWorker> {
    @Subcomponent.Builder
    public abstract class Builder extends AndroidInjector.Builder<PeriodicLocationFetchWorker>{
    }
}
