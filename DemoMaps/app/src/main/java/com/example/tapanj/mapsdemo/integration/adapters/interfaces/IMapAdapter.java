package com.example.tapanj.mapsdemo.integration.adapters.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface IMapAdapter {
    // Request Sample: http://maps.googleapis.com/maps/api/geocode/json?latlng=42.311129,-83.221113&sensor=false&language=en-US
    @GET("geocode/json")
    Call<String> getAddresses(@Query("latlng")String latLongCsv, @Query("sensor")boolean sensor, @Query("language")String language);
}
