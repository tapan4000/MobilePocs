package com.example.tapanj.mapsdemo.integration.Retrofit;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.models.Group;
import com.example.tapanj.mapsdemo.models.GroupMember;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface IGroupRetrofitAdapter {
    @GET("/groups")
    Call<List<Group>> getGroups();

    @GET("/groupmember/{groupmemberid}")
    LiveData<ApiResponse<GroupMember>> getGroupMember(@Path("groupmemberid") int groupMemberId);
}
