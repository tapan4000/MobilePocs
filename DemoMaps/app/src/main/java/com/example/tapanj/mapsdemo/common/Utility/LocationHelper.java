package com.example.tapanj.mapsdemo.common.Utility;

import android.location.Location;

public class LocationHelper {
    public static String getLocationString(Location location) {
        String provider = location.getProvider();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        long time = location.getTime();

        float accuracy = -1;
        if(location.hasAccuracy()){
            accuracy = location.getAccuracy();
        }

        double altitude = -1;
        if(location.hasAltitude()){
            altitude = location.getAltitude();
        }

        float speed = -1;
        if(location.hasSpeed()){
            speed = location.getSpeed();
        }

        float bearing = -1;
        if(location.hasBearing())
        {
            bearing = location.getBearing();
        }

        String strDate = Utility.getCurrentDateTime();

        String locationDetail = strDate + " - Provider:" + provider + ", Latitude:" + latitude + ", Longitude:" + longitude + ", Time:" + time + ", Accuracy:" + accuracy
                + ", Altitude:" + altitude + ", Speed:" + speed + ", Bearing:" + bearing;

        return locationDetail;
    }
}
