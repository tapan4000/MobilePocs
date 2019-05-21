package com.example.tapanj.mapsdemo.workmanager;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import androidx.work.Worker;
import com.example.tapanj.mapsdemo.common.Utility.LocationHelper;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.dagger.module.worker.AndroidWorkerInjection;
import com.example.tapanj.mapsdemo.common.logging.interfaces.ILogProvider;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationCallback;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationProvider;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.WorkflowSourceType;
import com.google.android.gms.common.api.ResolvableApiException;

import javax.inject.Inject;

public class PeriodicLocationFetchWorker extends Worker {

    @Inject
    ILocationProvider locationProvider;

    @Inject
    ILogProvider logProvider;

    private WorkflowContext workflowContext;

    public PeriodicLocationFetchWorker(){
        super();
//        Context applicationContext = getApplicationContext();
//        if(applicationContext instanceof MainApplication){
//            ((MainApplication)applicationContext).getMainApplicationComponent().inject(this);
//        }

        Context appContext = this.getApplicationContext();
        this.workflowContext = new WorkflowContext(PeriodicLocationFetchWorker.class.getSimpleName(), WorkflowSourceType.WorkManager_Create);
    }

    @NonNull
    @Override
    public Result doWork() {
//        Context appContext = this.getApplicationContext();
//        if(!(appContext instanceof MainApplication)){
//            return Result.FAILURE;
//        }
//
//        MainApplication application = (MainApplication) appContext;
//        application.getMainApplicationComponent().inject(this);

        AndroidWorkerInjection.inject(this);
        if(null != locationProvider){
            this.locationProvider.getCurrentLocation(this.workflowContext, new ILocationCallback() {
                @Override
                public void onLocationCheckLogEventReceived(String message) {
                    logProvider.WriteLog(message, workflowContext.getWorkflowId());
                }

                @Override
                public void onCurrentLocationRequestComplete(Location location) {
                    logProvider.WriteLog(LocationHelper.getLocationString(location) + ", Id:" + getId() + ", Tags:" + getTags(), workflowContext.getWorkflowId());
                }

                @Override
                public void onLocationRequestFailure(String failureReason) {
                    logProvider.WriteLog(failureReason, workflowContext.getWorkflowId());
                }

                @Override
                public void onRequestEnableGpsSettingRequired(ResolvableApiException ex) {
                    logProvider.WriteLog(Utility.FlattenException(ex), workflowContext.getWorkflowId());
                }
            });
        }

        return Result.SUCCESS;
    }
}
