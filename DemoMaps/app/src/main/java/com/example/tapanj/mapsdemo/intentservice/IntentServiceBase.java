package com.example.tapanj.mapsdemo.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import com.example.tapanj.mapsdemo.common.Constants;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.dagger.MainApplication;
import com.example.tapanj.mapsdemo.interfaces.ILogger;
import com.example.tapanj.mapsdemo.models.IntentServiceResult;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.WorkflowSourceType;
import dagger.android.AndroidInjection;

import javax.inject.Inject;

public abstract class IntentServiceBase extends IntentService {
    private String serviceName;

    @Inject
    ILogger logger;

    protected ResultReceiver resultReceiver;

    protected WorkflowContext workflowContext;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public IntentServiceBase(String name) {
        super(name);
        this.serviceName = name;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
        //((MainApplication)getApplication()).getMainApplicationComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(null == intent){
            return;
        }

        // Add the logic of accessing the workflow context and assigning it to the local variable.
        this.workflowContext = intent.getParcelableExtra(Constants.WORKFLOW_CONTEXT);
        if(null == this.workflowContext){
            this.workflowContext = new WorkflowContext(this.serviceName, WorkflowSourceType.IntentService_WorkflowContext_Missing);
            logger.LogError("The workflow context not received for " + this.serviceName, this.workflowContext.getWorkflowId());
        }

        IntentServiceResult result = this.ProcessIntent(intent);
        this.deliverResultToReceiver(result);
    }

    private void deliverResultToReceiver(IntentServiceResult result) {
        if(null != result){
            Bundle bundle = new Bundle();
            bundle.putString(Constants.RESULT_DATA_KEY, result.ResultMessage);

            // Place the workflow context object inside the bundle for the requestor to continue using the workflow context.
            bundle.putParcelable(Constants.WORKFLOW_CONTEXT, this.workflowContext);
            this.resultReceiver.send(result.ResultCode, bundle);
        }
    }

    protected abstract IntentServiceResult ProcessIntent(Intent intent);
}
