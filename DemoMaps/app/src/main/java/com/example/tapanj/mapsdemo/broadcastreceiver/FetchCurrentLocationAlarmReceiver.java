package com.example.tapanj.mapsdemo.broadcastreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import com.example.tapanj.mapsdemo.common.Utility.LocationHelper;
import com.example.tapanj.mapsdemo.common.logging.interfaces.ILogger;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationCallback;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationProvider;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.WorkflowSourceType;
import com.google.android.gms.common.api.ResolvableApiException;
import dagger.android.AndroidInjection;

import javax.inject.Inject;
import java.util.Calendar;

public class FetchCurrentLocationAlarmReceiver extends BroadcastReceiver {
    @Inject
    ILocationProvider locationProvider;

    @Inject
    ILogger logger;



    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);
        //Toast.makeText(context, "Invoked", Toast.LENGTH_SHORT).show();

        final WorkflowContext workflowContext = new WorkflowContext("", "", WorkflowSourceType.Activity_Create);
        locationProvider.getCurrentLocation(workflowContext, new ILocationCallback() {
            @Override
            public void onLocationCheckLogEventReceived(String message) {
            }

            @Override
            public void onCurrentLocationRequestComplete(Location location) {
                logger.LogInformation(LocationHelper.getLocationString(location), workflowContext.getWorkflowId());
            }

            @Override
            public void onLocationRequestFailure(String failureReason) {
            }

            @Override
            public void onRequestEnableGpsSettingRequired(ResolvableApiException resolvable) {
            }
        });

        // Scheduler the alarm again until the time of emergency is in place.
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 8, 1, 6, 10, 0);
        long endTime = calendar.getTimeInMillis();

        long currentTimeInMs = System.currentTimeMillis();

        if(currentTimeInMs > endTime){
            return;
        }

        logger.LogInformation("Setting alarm", "");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent fetchCurrentLocationIntent = new Intent(context, FetchCurrentLocationAlarmReceiver.class);
        PendingIntent fetchCurrentLocationPendingIntent = PendingIntent.getBroadcast(context, 0, fetchCurrentLocationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(fetchCurrentLocationPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currentTimeInMs + 30000, fetchCurrentLocationPendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentTimeInMs + 30000, fetchCurrentLocationPendingIntent);
        }
        else{
            alarmManager.set(AlarmManager.RTC_WAKEUP, currentTimeInMs + 30000, fetchCurrentLocationPendingIntent);
        }

        logger.LogInformation("Alarm set", "");
    }
}
