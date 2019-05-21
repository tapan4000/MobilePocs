package com.example.tapanj.mapsdemo.integration.adapters.interfaces;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.models.Group;
import com.example.tapanj.mapsdemo.models.backendModels.response.group.GroupMemberResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;

import java.util.List;
import java.util.function.Consumer;

public interface IGroupAdapter {
    void getGroups(Consumer<List<Group>> onSuccessCallbackHandler, Consumer<String> onFailureCallbackHandler);

    LiveData<ApiResponse<GroupMemberResponseModel>> getGroupMember(int groupMemberId);
}
