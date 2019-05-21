package com.example.tapanj.mapsdemo.enums;

import java.util.HashMap;
import java.util.Map;

public enum LocationCaptureSessionStateEnum {
    NONE(0),
    PendingSyncWithLocationProvider(1),
    Active(2),
    Stopped(3),
    Expired(4);

    private int value;
    private static Map map = new HashMap<>();

    private LocationCaptureSessionStateEnum(int value){
        this.value = value;
    }

    static{
        for(LocationCaptureSessionStateEnum locationCaptureSessionStateEnum : LocationCaptureSessionStateEnum.values()){
            map.put(locationCaptureSessionStateEnum.value, locationCaptureSessionStateEnum);
        }
    }

    public static LocationCaptureSessionStateEnum valueOf(int locationCaptureSessionStateId){
        return (LocationCaptureSessionStateEnum) map.get(locationCaptureSessionStateId);
    }

    public int getValue(){
        return this.value;
    }
}
