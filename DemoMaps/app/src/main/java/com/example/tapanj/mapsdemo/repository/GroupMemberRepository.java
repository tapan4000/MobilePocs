package com.example.tapanj.mapsdemo.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.tapanj.mapsdemo.datastore.dao.GroupMemberDao;
import com.example.tapanj.mapsdemo.integration.adapters.interfaces.IGroupAdapter;
import com.example.tapanj.mapsdemo.repository.interfaces.IGroupMemberRepository;
import com.example.tapanj.mapsdemo.models.dao.GroupMember;
import com.example.tapanj.mapsdemo.models.backendModels.response.group.GroupMemberResponseModel;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GroupMemberRepository implements IGroupMemberRepository {
    //private AppDatabase appDatabase;
    private final IGroupAdapter groupAdapter;

    private final GroupMemberDao groupMemberDao;
    //private final Executor executor;

    @Inject
    public GroupMemberRepository(IGroupAdapter groupAdapter, GroupMemberDao groupMemberDao)//, Executor executor)
    {
        //this.appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "cmpdb").build();
        this.groupAdapter = groupAdapter;
        this.groupMemberDao = groupMemberDao;
        //this.executor = executor;
    }

    public LiveData<ResourceEvent<GroupMember>> getGroupMember(final int groupMemberId){
        return new NetworkBoundResource<GroupMember, GroupMemberResponseModel>(){

            @Override
            protected void saveCallResultToDataStore(@NonNull GroupMemberResponseModel item) {
                // TODO: Integrate the group member call.
                GroupMember groupMember = new GroupMember(2);
                groupMemberDao.addGroupMember(groupMember);
            }

            @Override
            protected boolean shouldFetch(@Nullable GroupMember data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<GroupMember> loadFromDataStore() {
                return groupMemberDao.getById(groupMemberId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GroupMemberResponseModel>> createApiCall() {
                return groupAdapter.getGroupMember(groupMemberId);
            }
        }.getAsLiveData();
        //fetchGroupMemberFromBackend(groupMemberId);
        //return groupMemberDao.getById(groupMemberId);
    }

    /*    private void fetchGroupMemberFromBackend(final int groupMemberId) {
        // TODO: Check if we need to exted the executor for any custom logic.
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                // Running in a backgroud thread. Check if the user was fetched recently.
                boolean isGroupMemberAvailable = groupMemberDao.isGroupMemberAvailable(groupMemberId);
                if(!isGroupMemberAvailable){
                    try{
                        Response<GroupMember> response = groupAdapter.getGroupMember(groupMemberId).execute();
                        groupMemberDao.addGroupMember(response.body());
                    }
                    catch (IOException ex){

                    }
                }
            }
        });
    }*/
}
