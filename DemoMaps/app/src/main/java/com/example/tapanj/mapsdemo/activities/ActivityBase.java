package com.example.tapanj.mapsdemo.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.dagger.MainApplication;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class ActivityBase extends AppCompatActivity {
    protected WorkflowContext activityLifecycleWorkflowContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.injectMembers();
        super.onCreate(savedInstanceState);
        this.initializeActivityLifecycleWorkflowContext();
    }

    protected abstract void initializeActivityLifecycleWorkflowContext();

    protected abstract void injectMembers();

    protected void displayLongMessage(String message){
        //Toast.makeText(getActivity(), appUnhandledErrorMessage, Toast.LENGTH_LONG).show();
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
