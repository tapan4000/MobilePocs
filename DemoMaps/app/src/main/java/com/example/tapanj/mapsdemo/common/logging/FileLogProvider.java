package com.example.tapanj.mapsdemo.common.logging;

import android.content.ContextWrapper;
import com.example.tapanj.mapsdemo.common.logging.interfaces.ILogProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileLogProvider implements ILogProvider {
    String LOG_FILE_NAME = "fileLog.txt";
    private ContextWrapper mContextWrapper;
    public FileLogProvider(ContextWrapper contextWrapper){
        // All the activities would run under the same application context and have the same file directory.
        // So, any activity or application initializing the logger can be used to fetch the file path.
        // Need to check if the activity context is accessible if the activity goes out of context or activity is destroyed.
        // In such case the application context should always be used and the logger should be initialized in the application startup.
        this.mContextWrapper = contextWrapper;
    }

    @Override
    public void WriteLog(String message, String workflowId) {
        message = workflowId + "--" + message;
        saveToFile(message);
    }

    private void saveToFile(String message) {
        File directory = this.mContextWrapper.getFilesDir();
        String path = directory.getAbsolutePath();
        File logFile = new File(directory, LOG_FILE_NAME);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.mContextWrapper
                    .openFileOutput(LOG_FILE_NAME, this.mContextWrapper.MODE_APPEND | this.mContextWrapper.MODE_PRIVATE));
            outputStreamWriter.append(message);
            outputStreamWriter.append("\n");
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
