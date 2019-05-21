package com.example.tapanj.mapsdemo.models.backendModels.response.group;

import com.example.tapanj.mapsdemo.models.backendModels.response.ServiceResponseModel;
import com.google.gson.annotations.SerializedName;

public class GroupMemberResponseModel extends ServiceResponseModel {
    @SerializedName("groupMemberId")
    public int GroupMemberId;

    @SerializedName("groupId")
    public int GroupId;

    @SerializedName("userId")
    public int UserId;

}
