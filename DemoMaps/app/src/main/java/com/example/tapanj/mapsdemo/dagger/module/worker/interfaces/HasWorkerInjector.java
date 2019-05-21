package com.example.tapanj.mapsdemo.dagger.module.worker.interfaces;

import androidx.work.Worker;
import dagger.android.AndroidInjector;

public interface HasWorkerInjector {
    AndroidInjector<Worker> workerInjector();
}
