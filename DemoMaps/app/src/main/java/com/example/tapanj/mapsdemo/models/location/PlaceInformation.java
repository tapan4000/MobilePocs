package com.example.tapanj.mapsdemo.models.location;

import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;

public class PlaceInformation {
    private String name;
    private String address;
    private String attributions;
    private LatLng latLng;

    public PlaceInformation(String name, String address, String attributions, LatLng latlng){
        this.name = name;
        this.address = address;
        this.attributions = attributions;
        this.latLng = latlng;
    }


    public String getMarkerSnippet(){
        String markerSnippet = this.address;
        if(null != this.attributions){
            markerSnippet += "\n" + attributions;
        }

        return markerSnippet;
    }

    public String getName(){
        return this.name;
    }

    public LatLng getLatLng(){
        return this.latLng;
    }
}
