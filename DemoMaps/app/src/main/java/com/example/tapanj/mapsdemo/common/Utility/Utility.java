package com.example.tapanj.mapsdemo.common.Utility;

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
}
