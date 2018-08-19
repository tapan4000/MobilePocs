package com.example.tapanj.mapsdemo.datastore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.example.tapanj.mapsdemo.models.GroupMember;

@Dao
public interface GroupMemberDao {

    // Get by id has been marked to return LiveData. Hence Room database would know when the data changes
    // and would trigger a notification to all subscribers.
    @Query("Select * from GroupMember where GroupMemberId = :groupMemberId")
    LiveData<GroupMember> getById(int groupMemberId);

    @Insert
    void addGroupMember(GroupMember groupMember);

    @Delete
    void deleteGroupMember(GroupMember groupMember);
}
