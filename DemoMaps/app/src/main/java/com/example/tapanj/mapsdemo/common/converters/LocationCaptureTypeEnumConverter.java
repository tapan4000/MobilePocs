package com.example.tapanj.mapsdemo.common.converters;

import android.arch.persistence.room.TypeConverter;
import com.example.tapanj.mapsdemo.enums.LocationCaptureTypeEnum;
import org.threeten.bp.Instant;

public class LocationCaptureTypeEnumConverter {
    @TypeConverter
    public static LocationCaptureTypeEnum toLocationCaptureTypeEnum(int value){
        return LocationCaptureTypeEnum.valueOf(value);
    }

    @TypeConverter
    public static int fromLocationCaptureTypeEnum(LocationCaptureTypeEnum locationCaptureTypeEnum){
        return locationCaptureTypeEnum.getValue();
    }
}
