package com.example.tapanj.mapsdemo.datastore.file;

import android.app.Application;
import com.example.tapanj.mapsdemo.interfaces.datastore.IFileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileProvider implements IFileProvider {
    private Application application;
    private final String LOG_FILE_NAME = "log.txt";
    public FileProvider(Application application){
        this.application = application;
    }

    @Override
    public boolean writeToFile(String message) {
        File directory = this.application.getFilesDir();
        String path = directory.getAbsolutePath();
        File logFile = new File(directory, LOG_FILE_NAME);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.application.openFileOutput(LOG_FILE_NAME,  this.application.MODE_APPEND | this.application.MODE_PRIVATE));
            outputStreamWriter.append(message);
            outputStreamWriter.append("\n");
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean truncateFile() {
        return true;
    }
}
