package com.example.tapanj.mapsdemo.dagger.module.worker;

import androidx.work.Worker;
import com.example.tapanj.mapsdemo.dagger.subcomponent.PeriodicLocationFetchWorkerSubcomponent;
import com.example.tapanj.mapsdemo.workmanager.PeriodicLocationFetchWorker;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module(subcomponents = PeriodicLocationFetchWorkerSubcomponent.class)
public abstract class WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(PeriodicLocationFetchWorker.class)
    abstract AndroidInjector.Factory<? extends Worker> bindPeriodicLocationFetchWorkerFactory(PeriodicLocationFetchWorkerSubcomponent.Builder periodicLocationFetchWorkerBuilder);
}
