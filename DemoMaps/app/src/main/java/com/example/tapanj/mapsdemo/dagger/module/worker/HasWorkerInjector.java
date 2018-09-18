package com.example.tapanj.mapsdemo.dagger.module.worker;

import androidx.work.Worker;
import dagger.android.AndroidInjector;

public interface HasWorkerInjector {
    AndroidInjector<Worker> workerInjector();
}
