package com.example.tapanj.mapsdemo.common.converters;

import android.arch.persistence.room.TypeConverter;
import com.example.tapanj.mapsdemo.enums.LocationCaptureSessionStateEnum;

public class LocationCaptureSessionStateEnumConverter {
    @TypeConverter
    public static LocationCaptureSessionStateEnum toLocationCaptureSessionStateEnum(int value){
        return LocationCaptureSessionStateEnum.valueOf(value);
    }

    @TypeConverter
    public static int fromLocationCaptureSessionStateEnum(LocationCaptureSessionStateEnum locationCaptureSessionStateEnum){
        return locationCaptureSessionStateEnum.getValue();
    }
}
