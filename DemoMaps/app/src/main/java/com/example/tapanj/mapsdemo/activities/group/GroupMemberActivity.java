package com.example.tapanj.mapsdemo.activities.group;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.ActivityBase;
import com.example.tapanj.mapsdemo.datastore.dao.AppDatabase;
import com.example.tapanj.mapsdemo.models.dao.GroupMember;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.WorkflowSourceType;
import com.example.tapanj.mapsdemo.viewmodel.GroupMemberViewModel;
import dagger.android.AndroidInjection;

public class GroupMemberActivity extends ActivityBase {

    public static final String GROUPMEMBERDETAIL = "com.example.tapanj.mapsdemo.GROUPMEMBERDETAIL";
    private Button btnStartEmergency;
    private Button btnStopEmergency;
    private TextView tvEmergencyStatus;
    private GroupMemberViewModel groupMemberViewModel;
    private GroupMember groupMember;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        this.btnStartEmergency = (Button)findViewById(R.id.btn_startEmergency);
        this.btnStopEmergency = (Button)findViewById(R.id.btn_stopEmergency);
        this.tvEmergencyStatus = (TextView)findViewById(R.id.textView_emergencyStatus);
        this.groupMemberViewModel = ViewModelProviders.of(this).get(GroupMemberViewModel.class);
        this.appDatabase = AppDatabase.getInstance(this);
        this.readGroupMemberFromIntent();
    }

    private void readGroupMemberFromIntent() {
        Intent intent = getIntent();
        this.groupMember = intent.getParcelableExtra(GROUPMEMBERDETAIL);
        this.groupMemberViewModel.initialize(this.groupMember);
        LiveData<GroupMember> groupMember = this.groupMemberViewModel.getGroupMember();
        if(null != groupMember){
            groupMember.observe(this, new Observer<GroupMember>() {
                @Override
                public void onChanged(@Nullable GroupMember groupMember) {
                    // Update the UI
                }
            });
        }
    }

    @Override
    protected void injectMembers(){
        //((MainApplication)getApplication()).getMainApplicationComponent().inject(this);
        AndroidInjection.inject(this);
    }

    @Override
    protected void initializeActivityLifecycleWorkflowContext() {
        this.activityLifecycleWorkflowContext = new WorkflowContext(GroupMemberActivity.class.getSimpleName(), WorkflowSourceType.Activity_Create);
    }

    public void onBtnStartEmergencyClicked(View v){
        this.btnStartEmergency.setVisibility(View.INVISIBLE);
        this.btnStopEmergency.setVisibility(View.VISIBLE);

        // Fetch the current location
        // Make an entry in the local database.

        //
        this.tvEmergencyStatus.setText("Emergency started");
    }

    public void onBtnStopEmergencyClicked(View v){
        this.btnStartEmergency.setVisibility(View.VISIBLE);
        this.btnStopEmergency.setVisibility(View.INVISIBLE);
        this.tvEmergencyStatus.setText("Emergency not started");
    }

}
