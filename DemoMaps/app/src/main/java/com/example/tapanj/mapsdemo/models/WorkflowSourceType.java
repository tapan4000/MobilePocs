package com.example.tapanj.mapsdemo.models;

public enum WorkflowSourceType {
    Button_Click,
    IntentService_WorkflowContext_Missing,
    Activity_Create,
    WorkManager_Create

    // This workflow type can be used for logging all operations post creation of activity that is not listed
    // in other operations as defined in this enum. These would primarily represent lifecycle events.
    // Need to determine if it is feasible to pass the workflow id's accross all the methods or is there a better way of sharing the workflow
    // context. Need to look at the activity lifecycle for the same. One option is to create a new context object whenever any background
    // service is called or a web service is called. Then at a common place the new workflow context would be used and the same
    // workflow context would be passed to background service or web server. Since each call would generate a new workflow id
    // the workflow is would be unique among calls and the same can be tracked on server. On client side we would log the sending
    // of the call and receiving the result of the call with the same workflow id. All other calls would use the same workflow
    // context that has been defined for an activity.
}
