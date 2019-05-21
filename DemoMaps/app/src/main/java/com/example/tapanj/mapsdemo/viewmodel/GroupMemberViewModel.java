package com.example.tapanj.mapsdemo.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import com.example.tapanj.mapsdemo.repository.interfaces.IGroupMemberRepository;
import com.example.tapanj.mapsdemo.models.dao.GroupMember;

import javax.inject.Inject;

public class GroupMemberViewModel extends ViewModel{
    private LiveData<GroupMember> groupMember;
    @Inject
    IGroupMemberRepository groupMemberRepository;

    public LiveData<GroupMember> getGroupMember()
    {
        return groupMember;
    }

    public void initialize(GroupMember groupMember){
        //this.groupMember = groupMember;
    }
}
