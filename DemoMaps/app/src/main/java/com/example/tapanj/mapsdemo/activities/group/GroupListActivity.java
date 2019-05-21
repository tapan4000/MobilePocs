package com.example.tapanj.mapsdemo.activities.group;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.tapanj.mapsdemo.activities.ActivityBase;
import com.example.tapanj.mapsdemo.integration.Retrofit.ServiceBuilder;
import com.example.tapanj.mapsdemo.integration.Retrofit.IGroupRetrofitAdapter;
import com.example.tapanj.mapsdemo.managers.DataManager;
import com.example.tapanj.mapsdemo.adapters.GenericRecyclerViewAdapter;
import com.example.tapanj.mapsdemo.models.Group;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.WorkflowSourceType;
import com.example.tapanj.mapsdemo.viewmodel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class GroupListActivity extends ActivityBase {
    //region All private variables
    private RecyclerView.LayoutManager groupsRecyclerViewLayoutManager;

    private UserViewModel userViewModel;
    //endregion

    //region Overriden Activity methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_trigger_emergency);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        this.userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        initializeDisplayContent();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    return;
                }

                String token = task.getResult().getToken();
            }
        });
    }
    //endregion

    //region All private methods
    private void initializeDisplayContent() {
        RecyclerView groupList = (RecyclerView) findViewById(R.id.list_groups);
        List<Group> groups = DataManager.getInstance().getGroups();
        ArrayAdapter<Group> adapterGroups = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groups);

        groupList.setHasFixedSize(true);

        if(groupsRecyclerViewLayoutManager == null){
            groupsRecyclerViewLayoutManager = new LinearLayoutManager(this);
        }

        groupList.setLayoutManager(groupsRecyclerViewLayoutManager);

        // Specify an adapter
        GenericRecyclerViewAdapter groupListRecyclerViewAdapter = new GenericRecyclerViewAdapter(groups.toArray(), new GenericRecyclerViewAdapter.OnRecyclerItemClickListener<Group>() {
            @Override
            public void onItemClick(Group itemData) {
                Intent groupClickIntent = new Intent(GroupListActivity.this, GroupActivity.class);
                groupClickIntent.putExtra(GroupActivity.GROUPDETAIL, itemData);
                startActivity(groupClickIntent);
            }
        });
        groupList.setAdapter(groupListRecyclerViewAdapter);
    }

    private void fetchGroupsFromBackend(){
        // Fetch the list of groups from the backend
        IGroupRetrofitAdapter groupAdapter = ServiceBuilder.buildService(IGroupRetrofitAdapter.class);
        Call<List<Group>> groupListRequest = groupAdapter.getGroups();
        groupListRequest.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                List<Group> responseData = response.body();
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                String exceptionMessage = t.getMessage();
            }
        });
    }

    //endregion

    @Override
    protected void injectMembers(){
        //((MainApplication)getApplication()).getMainApplicationComponent().inject(this);
        AndroidInjection.inject(this);
    }

    @Override
    protected void initializeActivityLifecycleWorkflowContext() {
        this.activityLifecycleWorkflowContext = new WorkflowContext(GroupListActivity.class.getSimpleName(), WorkflowSourceType.Activity_Create);
    }
}
