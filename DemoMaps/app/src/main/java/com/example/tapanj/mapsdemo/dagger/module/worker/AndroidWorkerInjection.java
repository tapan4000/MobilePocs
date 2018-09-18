package com.example.tapanj.mapsdemo.dagger.module.worker;

import android.content.Context;
import androidx.work.Worker;
import dagger.android.AndroidInjector;

public class AndroidWorkerInjection {
    public static void inject(Worker worker){
        if(null == worker){
            return;
        }

        Context applicationContext = worker.getApplicationContext();
        if(!(applicationContext instanceof HasWorkerInjector)){
            throw new RuntimeException("Application does not implement HasWorkerInjector");
        }

        AndroidInjector<Worker> workerInjector = ((HasWorkerInjector)applicationContext).workerInjector();
        if(null == workerInjector){
            return;
        }

        workerInjector.inject(worker);
    }
}
