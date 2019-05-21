package com.example.tapanj.mapsdemo.intentservice;

import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.example.tapanj.mapsdemo.common.Utility.LocationHelper;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationCallback;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationProvider;
import com.example.tapanj.mapsdemo.models.IntentServiceResult;
import com.google.android.gms.common.api.ResolvableApiException;

import javax.inject.Inject;

public class FetchCurrentLocationIntentService extends IntentServiceBase {
    @Inject
    ILocationProvider locationProvider;

    private int counter = 0;

    Handler handler = new Handler();
    private Runnable periodicLocationFetch = new Runnable() {
        @Override
        public void run() {
            if(counter > 10000){
                stopSelf();
            }
            else{
                counter++;
                handler.postDelayed(periodicLocationFetch, 30*1000);
                fetchLocation();
            }
        }
    };

    public FetchCurrentLocationIntentService()
    {
        super(FetchCurrentLocationIntentService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logger.LogInformation("Service Created", "");
    }

    @Override
    public void onDestroy() {
        logger.LogInformation("Service Destroyed", "");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        int returnVal = super.onStartCommand(intent, flags, startId);
        //return START_STICKY;
        return returnVal;
    }

    @Override
    protected IntentServiceResult ProcessIntent(Intent intent) {
        handler.post(periodicLocationFetch);

        // No need to return result to the receiver.
        return null;
    }

    private void fetchLocation(){
        if(null != locationProvider){
            this.locationProvider.getCurrentLocation(this.workflowContext, new ILocationCallback() {
                @Override
                public void onLocationCheckLogEventReceived(String message) {
                    logger.LogInformation(message, workflowContext.getWorkflowId());
                }

                @Override
                public void onCurrentLocationRequestComplete(Location location) {
                    logger.LogInformation(LocationHelper.getLocationString(location), workflowContext.getWorkflowId());
                }

                @Override
                public void onLocationRequestFailure(String failureReason) {
                    logger.LogInformation(failureReason, workflowContext.getWorkflowId());
                }

                @Override
                public void onRequestEnableGpsSettingRequired(ResolvableApiException ex) {
                    logger.LogInformation(Utility.FlattenException(ex), workflowContext.getWorkflowId());
                }
            });
        }
    }
}
