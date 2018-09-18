package com.example.tapanj.mapsdemo.dagger.module.worker;

import androidx.work.Worker;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.Multibinds;

import java.util.Map;

@Module
public abstract class AndroidWorkerInjectionModule {
    @Multibinds
    abstract Map<Class<? extends Worker>, AndroidInjector.Factory<? extends Worker>> workerInjectorFactories();
}
