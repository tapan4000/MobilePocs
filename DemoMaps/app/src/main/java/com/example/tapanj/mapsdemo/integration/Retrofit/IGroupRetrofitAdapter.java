package com.example.tapanj.mapsdemo.integration.Retrofit;

import com.example.tapanj.mapsdemo.models.Group;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface IGroupRetrofitAdapter {
    @GET("groups")
    Call<List<Group>> getGroups();
}
