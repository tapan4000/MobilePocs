package com.example.tapanj.mapsdemo.activities.groupmember;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.LocationActivityBase;
import com.example.tapanj.mapsdemo.models.GroupMember;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.WorkflowSourceType;
import com.example.tapanj.mapsdemo.viewmodel.GroupMemberViewModel;

public class GroupMemberActivity extends LocationActivityBase {

    public static final String MEMBERDETAIL = "com.example.tapanj.mapsdemo.MEMBERDETAIL";
    private Button btnStartEmergency;
    private Button btnStopEmergency;
    private TextView tvEmergencyStatus;
    private GroupMemberViewModel groupMemberViewModel;

    @Override
    protected void onLocationCheckLogEventReceived(String logEvent) {

    }

    @Override
    protected void onCurrentLocationRequestComplete(Location location) {
        this.tvEmergencyStatus.setText("First location received:" + location.getLatitude() +"," + location.getLongitude());

        // Once the first current location request is complete, store the location information along with session details in local
        // database. Thereafter start the periodic location updates.
        this.startLocationUpdates(this.activityLifecycleWorkflowContext);
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        this.btnStartEmergency = (Button)findViewById(R.id.btn_startEmergency);
        this.btnStopEmergency = (Button)findViewById(R.id.btn_stopEmergency);
        this.tvEmergencyStatus = (TextView)findViewById(R.id.textView_emergencyStatus);
        this.groupMemberViewModel = ViewModelProviders.of(this).get(GroupMemberViewModel.class);
        this.groupMemberViewModel.initialize(0);
        this.groupMemberViewModel.getGroupMember().observe(this, new Observer<GroupMember>() {
            @Override
            public void onChanged(@Nullable GroupMember groupMember) {
                // Update the UI
            }
        });
    }

    private Observer<GroupMember> onGroupMemberDataUpdated(GroupMember member) {

    }

    @Override
    protected void initializeActivityLifecycleWorkflowContext() {
        this.activityLifecycleWorkflowContext = new WorkflowContext(GroupMemberActivity.class.getSimpleName(), WorkflowSourceType.Activity_Create);
    }

    @Override
    protected void onLocationUpdateReceived(Location location) {

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
