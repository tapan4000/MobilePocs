package com.example.tapanj.mapsdemo.workmanager;

import android.location.Location;
import android.support.annotation.NonNull;
import androidx.work.Worker;
import com.example.tapanj.mapsdemo.dagger.MainApplication;
import com.example.tapanj.mapsdemo.interfaces.location.ILocationCallback;
import com.example.tapanj.mapsdemo.interfaces.location.ILocationProvider;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.WorkflowSourceType;
import com.google.android.gms.common.api.ResolvableApiException;

import javax.inject.Inject;

public class PeriodicLocationFetchWorker extends Worker {

    @Inject
    ILocationProvider locationProvider;

    @Inject
    MainApplication application;

    private WorkflowContext workflowContext;

    public PeriodicLocationFetchWorker(){
        super();
        //this.getApplicationContext()
        application.getMainApplicationComponent().inject(this);
        this.workflowContext = new WorkflowContext(PeriodicLocationFetchWorker.class.getSimpleName(), WorkflowSourceType.WorkManager_Create);
    }

    @NonNull
    @Override
    public Result doWork() {
        this.locationProvider.getCurrentLocation(this.workflowContext, new ILocationCallback() {
            @Override
            public void onLocationCheckLogEventReceived(String message) {

            }

            @Override
            public void onCurrentLocationRequestComplete(Location location) {
            }

            @Override
            public void onLocationRequestFailure(String failureReason) {

            }

            @Override
            public void onRequestEnableGpsSettingRequired(ResolvableApiException ex) {

            }
        });
        return null;
    }
}
