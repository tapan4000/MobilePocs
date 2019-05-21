package com.example.tapanj.mapsdemo.common.logging;

import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.common.logging.interfaces.ILogProvider;
import com.example.tapanj.mapsdemo.common.logging.interfaces.ILogger;

public class EventLogger implements ILogger {
    private ILogProvider mlogProvider;

    public EventLogger(ILogProvider logProvider){
        mlogProvider = logProvider;
    }

    @Override
    public void LogInformation(String message, String workflowId) {
        mlogProvider.WriteLog(message, workflowId);
    }

    @Override
    public void LogError(String errorMessage, String workflowId) {
        mlogProvider.WriteLog(errorMessage, workflowId);
    }

    @Override
    public void LogVerbose(String message, String workflowId) {
        mlogProvider.WriteLog(message, workflowId);
    }

    @Override
    public void LogException(Exception ex, String workflowId) {
        mlogProvider.WriteLog(Utility.FlattenException(ex), workflowId);
    }

    @Override
    public void LogException(Exception ex, String customMessage, String workflowId) {
        String errorMessage = customMessage + "\nException:" + Utility.FlattenException(ex);
        mlogProvider.WriteLog(errorMessage, workflowId);
    }
}
