package com.example.tapanj.mapsdemo.common.logging.interfaces;

public interface ILogger {
    void LogInformation(String message, String workflowId);

    void LogError(String errorMessage, String workflowId);

    void LogVerbose(String message, String workflowId);

    void LogException(Exception ex, String workflowId);

    void LogException(Exception ex, String customMessage, String workflowId);
}
