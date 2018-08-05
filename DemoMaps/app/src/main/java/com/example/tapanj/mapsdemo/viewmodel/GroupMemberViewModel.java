package com.example.tapanj.mapsdemo.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import com.example.tapanj.mapsdemo.models.GroupMember;

public class GroupMemberViewModel extends ViewModel{
private LiveData<GroupMember> groupMember;

    public LiveData<GroupMember> getGroupMember()
    {
        return groupMember;
    }

    public void initialize(int groupMemberId){

    }
}
