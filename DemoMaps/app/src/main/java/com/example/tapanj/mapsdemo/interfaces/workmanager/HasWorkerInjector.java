package com.example.tapanj.mapsdemo.interfaces.workmanager;

import androidx.work.Worker;
import dagger.android.AndroidInjector;

public interface HasWorkerInjector {
    AndroidInjector<Worker> workerInjector();
}
