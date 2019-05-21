package com.example.tapanj.mapsdemo.dagger.module;

import com.example.tapanj.mapsdemo.datastore.dao.GroupMemberDao;
import com.example.tapanj.mapsdemo.integration.adapters.GroupRestAdapter;
import com.example.tapanj.mapsdemo.integration.adapters.interfaces.IGroupAdapter;
import com.example.tapanj.mapsdemo.repository.interfaces.IGroupMemberRepository;
import com.example.tapanj.mapsdemo.repository.GroupMemberRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class GroupManagementModule {
    @Singleton
    @Provides
    IGroupAdapter groupAdapter(){
        return new GroupRestAdapter();
    }

    @Singleton
    @Provides
    IGroupMemberRepository groupMemberRepository(IGroupAdapter groupAdapter, GroupMemberDao groupMemberDao){
        return new GroupMemberRepository(groupAdapter, groupMemberDao);
    }
}
