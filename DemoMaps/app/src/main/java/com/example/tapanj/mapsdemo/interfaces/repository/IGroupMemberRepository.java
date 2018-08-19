package com.example.tapanj.mapsdemo.interfaces.repository;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.models.GroupMember;
import com.example.tapanj.mapsdemo.repository.Resource;

public interface IGroupMemberRepository {
    LiveData<Resource<GroupMember>> getGroupMember(final int groupMemberId);
}
