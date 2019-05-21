package com.example.tapanj.mapsdemo.enums;

import java.util.HashMap;
import java.util.Map;

public enum LocationCaptureTypeEnum {
    None(0),
    OneTime(1),
    PeriodicUpdate(2),
    Emergency(3);

    private int value;
    private static Map map = new HashMap<>();

    private LocationCaptureTypeEnum(int value){
        this.value = value;
    }

    static{
        for(LocationCaptureTypeEnum locationCaptureTypeEnum : LocationCaptureTypeEnum.values()){
            map.put(locationCaptureTypeEnum.value, locationCaptureTypeEnum);
        }
    }

    public static LocationCaptureTypeEnum valueOf(int locationCaptureTypeId){
        return (LocationCaptureTypeEnum) map.get(locationCaptureTypeId);
    }

    public int getValue(){
        return this.value;
    }
}
