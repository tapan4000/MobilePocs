package com.example.tapanj.mapsdemo.integration.Retrofit;

import com.example.tapanj.mapsdemo.interfaces.integration.IGroupAdapter;
import com.example.tapanj.mapsdemo.models.Group;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.function.Consumer;

public class GroupRetrofitAdapter implements IGroupAdapter {
    @Override
    public void getGroups(Consumer<List<Group>> onSuccessCallbackHandler, Consumer<String> onFailureCallbackHandler) {
        IGroupRetrofitAdapter groupAdapter = ServiceBuilder.buildService(IGroupRetrofitAdapter.class);
        Call<List<Group>> groupListRequest = groupAdapter.getGroups();
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
}
