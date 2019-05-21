package com.example.tapanj.mapsdemo.repository.interfaces;

import android.arch.lifecycle.LiveData;
import com.example.tapanj.mapsdemo.models.dao.GroupMember;
import com.example.tapanj.mapsdemo.repository.Resource;
import com.example.tapanj.mapsdemo.repository.ResourceEvent;

public interface IGroupMemberRepository {
    LiveData<ResourceEvent<GroupMember>> getGroupMember(final int groupMemberId);
}
