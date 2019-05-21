package com.example.tapanj.mapsdemo.integration.adapters;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.integration.Retrofit.IGroupRetrofitAdapter;
import com.example.tapanj.mapsdemo.integration.Retrofit.ServiceBuilder;
import com.example.tapanj.mapsdemo.integration.adapters.interfaces.IGroupAdapter;
import com.example.tapanj.mapsdemo.models.Group;
import com.example.tapanj.mapsdemo.models.backendModels.response.group.GroupMemberResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.function.Consumer;

public class GroupRestAdapter implements IGroupAdapter {
    private IGroupRetrofitAdapter groupRetrofitAdapter;
    public GroupRestAdapter(){
        groupRetrofitAdapter = ServiceBuilder.buildService(IGroupRetrofitAdapter.class);
    }

    @Override
    public void getGroups(Consumer<List<Group>> onSuccessCallbackHandler, Consumer<String> onFailureCallbackHandler) {

        Call<List<Group>> groupListRequest = groupRetrofitAdapter.getGroups();
        groupListRequest.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    List<Group> responseData = response.body();
                } else if (response.code() == 401) {

                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                String exceptionMessage = t.getMessage();
            }
        });
    }

    @Override
    public LiveData<ApiResponse<GroupMemberResponseModel>> getGroupMember(int groupMemberId) {
        return null;
    }
}
