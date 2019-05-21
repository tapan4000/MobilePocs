package com.example.tapanj.mapsdemo.common.Utility;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.threeten.bp.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Date;

public class Utility {
    public static String FlattenException(Exception ex){
        return ex.getMessage();
    }

    public static String GetUniqueWorkflowId(){
        return UUID.randomUUID().toString();
    }

    public static String ServerDateTimeFormat = "yyyy-MM-dd'T'hh:mm:ss.SSS";

    public static String ExtractResourceName(String fullName){
        String[] resourceNameSplit = fullName.split("/");
        return resourceNameSplit[resourceNameSplit.length - 1];
    }

    public static boolean isFirstInstantGreaterThanSecond(Instant firstInstant, Instant secondInstant){
        if(firstInstant.compareTo(secondInstant) > 0){
            return true;
        }

        return false;
    }

    public static boolean isInstantGreaterThanCurrentUtcInstant(Instant instant){
        return isFirstInstantGreaterThanSecond(instant, getCurrentUtcInstant());
    }

    public static boolean isInstantLessThanCurrentUtcInstant(Instant instant){
        return isFirstInstantGreaterThanSecond(getCurrentUtcInstant(), instant);
    }

    public static Date getCurrentUtcDateTime() {
        // Java.time.Instant required a min of API level 26, hence cannot use it for date calculations.
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat(ServerDateTimeFormat);
        dateFormat.setTimeZone(utcTimeZone);
        String strUtcTime = dateFormat.format(new Date());
        Date utcTime = getDateFromString(strUtcTime);
        return utcTime;
    }

    public static Instant getCurrentUtcInstant(){
        return Instant.now();
    }

    public static Date getDateFromString(String strDate){
        Date dateObj = null;

        // We are using calendar here and not java.time because java.time is supported in API level 26 and we need to support API level 15.
        try{
            if(strDate.length() == 19){
                // Parsing date in the format: 0001-01-01T00:00:00
                SimpleDateFormat dateFormatWithoutMilliSeconds = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                dateObj = dateFormatWithoutMilliSeconds.parse(strDate);
            }
            else if(strDate.length() > 19){
                // Parsing date in the format: 0001-01-01T00:00:00.123
                SimpleDateFormat dateFormatWithMilliSeconds = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
                dateObj = dateFormatWithMilliSeconds.parse(strDate);
            }

            String strDate2 = null;
            // Truncate the last digits from the string to have decimals upto 3 digits only.
            if(strDate.length() > 23){
                strDate2 = strDate.substring(0, 19);
            }

            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
            //dateObj = dateFormat.parse(strDate2);

            //String newDate = dateFormat.format(dateObj);
            String newDate2 = "";

            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSSS'Z'");
            Date dateObj2 = dateFormat2.parse(strDate);

            String newDate3 = dateFormat2.format(dateObj2);

            SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Date dateObj3 = dateFormat3.parse("0001-01-01T00:00:00");
            String newDate4 = dateFormat3.format(dateObj3);

            SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
            Date dateObj4 = dateFormat4.parse("0001-01-01T00:00:00");

            String newDate5 = dateFormat4.format(dateObj4);
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        return dateObj;
    }

    public static String getExceptionMessage(Exception ex){
        StringWriter exceptionStringWriter = new StringWriter();
        PrintWriter exceptionPrintWriter = new PrintWriter(exceptionStringWriter);
        ex.printStackTrace(exceptionPrintWriter);
        return exceptionStringWriter.toString();
    }
}
