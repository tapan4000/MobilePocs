package com.example.tapanj.mapsdemo.common.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Utility {
    public static String FlattenException(Exception ex){
        return ex.getMessage();
    }

    public static String GetUniqueWorkflowId(){
        return UUID.randomUUID().toString();
    }

    public static String ExtractResourceName(String fullName){
        String[] resourceNameSplit = fullName.split("/");
        return resourceNameSplit[resourceNameSplit.length - 1];
    }

    public static String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }
}
