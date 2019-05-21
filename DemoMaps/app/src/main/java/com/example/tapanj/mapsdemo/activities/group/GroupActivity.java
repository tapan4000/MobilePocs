package com.example.tapanj.mapsdemo.activities.group;

import android.Manifest;
import android.app.*;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.work.*;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.ActivityBase;
import com.example.tapanj.mapsdemo.activities.map.MapsActivity;
import com.example.tapanj.mapsdemo.adapters.GenericRecyclerViewAdapter;
import com.example.tapanj.mapsdemo.broadcastreceiver.FetchCurrentLocationAlarmReceiver;
import com.example.tapanj.mapsdemo.common.Constants;
import com.example.tapanj.mapsdemo.common.Utility.LocationHelper;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.SharedPreferenceConstants;
import com.example.tapanj.mapsdemo.intentservice.FetchAddressIntentService;
import com.example.tapanj.mapsdemo.intentservice.FetchCurrentLocationIntentService;
import com.example.tapanj.mapsdemo.intentservice.GeofenceTransitionIntentService;
import com.example.tapanj.mapsdemo.common.logging.interfaces.ILogger;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationCallback;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationProvider;
import com.example.tapanj.mapsdemo.common.location.interfaces.IPeriodicLocationCallback;
import com.example.tapanj.mapsdemo.models.*;
import com.example.tapanj.mapsdemo.models.backendModels.response.ServiceResponseModel;
import com.example.tapanj.mapsdemo.models.dao.GroupMember;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import com.example.tapanj.mapsdemo.service.FetchCurrentLocationService;
import com.example.tapanj.mapsdemo.viewmodel.LocationViewModel;
import com.example.tapanj.mapsdemo.viewmodel.ViewModelFactory;
import com.example.tapanj.mapsdemo.workmanager.PeriodicLocationFetchWorker;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import dagger.android.AndroidInjection;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GroupActivity extends ActivityBase {
    // region All private variables
    private Group mGroup;
    private RecyclerView.LayoutManager membersRecyclerViewLayoutManager;
    private RecyclerView.LayoutManager hangoutsRecyclerViewLayoutManager;
    private LocationAddressResultReceiver locationAddressResultReceiver;
    private LocationViewModel locationViewModel;

    private PendingIntent geofencePendingIntent;

    private final String LOG_FILE_NAME = "log.txt";
    private final String Start_String = "START";
    private final String Stop_String = "STOP";
    private LocationCallback periodicLocationCallback;

    @Inject
    ILogger logger;

    @Inject
    ILocationProvider locationProvider;

    @Inject
    ViewModelFactory viewModelFactory;

    //endregion

    // region All public static variables
    public static final String GROUPDETAIL = "com.example.tapanj.mapsdemo.GROUPDETAIL";

    private FusedLocationProviderClient mfusedLocationProviderClient;
    private final int REQUEST_GPS_SETTINGS = 1;
    private final int REQUEST_ACCESS_FINE_LOCATION = 2;

    private FetchCurrentLocationService fetchCurrentLocationService;
    private boolean isFetchCurrentLocationServiceBound = false;
    private ServiceConnection fetchCurrentLocationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            FetchCurrentLocationService.FetchCurrentLocationServiceBinder fetchCurrentLocationServiceBinder = (FetchCurrentLocationService.FetchCurrentLocationServiceBinder) iBinder;
            fetchCurrentLocationService = fetchCurrentLocationServiceBinder.getService();
            isFetchCurrentLocationServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isFetchCurrentLocationServiceBound = false;
        }
    };

    //endregion

    // region Overridden Activity methods

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION:
                // If the request is cancelled, the results array is empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateMessageOnCurrentLocationTextView("ermission granted.");
                } else {
                    populateMessageOnCurrentLocationTextView("Access fine location permission denied.");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_GPS_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made.
                        populateMessageOnCurrentLocationTextView("GPS settings enabled. Try operation again.");
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings but chose not to.
                        populateMessageOnCurrentLocationTextView("GPS settings enable request cancelled.");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        this.locationViewModel = ViewModelProviders.of(this, viewModelFactory).get(LocationViewModel.class);
        this.locationViewModel.reportOneTimeLocationResponse.observe(GroupActivity.this,
                new Observer<ApiResponse<ServiceResponseModel>>() {
                    @Override
                    public void onChanged(@Nullable ApiResponse<ServiceResponseModel> serviceResponseModelApiResponse) {
                        if(serviceResponseModelApiResponse.httpStatus != 0){
                            if(serviceResponseModelApiResponse.httpStatus == 200){
                                // Indicate a pop-up displaying location successfully reported.
                                displayLongMessage("Location submitted successfully.");
                            }
                            else if(serviceResponseModelApiResponse.httpStatus == 400) {
                                displayLongMessage("Invalid request format for reporting the location.");
                            }
                            else if(serviceResponseModelApiResponse.httpStatus == 402) {
                                displayLongMessage("Some error occurred while reporting the location. Please try again.");
                            }
                        }
                    }
                }
        );

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    return;
                }

                String token = task.getResult().getToken();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_trigger_emergency_test);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final Button btnLocationUpdate = (Button) findViewById(R.id.btn_locationUpdates);
        btnLocationUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                WorkflowContext locationUpdateButtonClickWorkflowContext =
                        new WorkflowContext(Utility.ExtractResourceName(btnLocationUpdate.toString()), WorkflowSourceType.Button_Click);
                if(btnLocationUpdate.getText().equals(Start_String)){
                    // Start the location capture
                    periodicLocationCallback = locationProvider.startLocationUpdates(locationUpdateButtonClickWorkflowContext, new IPeriodicLocationCallback() {
                        @Override
                        public void onPeriodicLocationUpdateReceived(Location location) {
                            saveLocationToFile(location);
                        }

                        @Override
                        public void onLocationCheckLogEventReceived(String message) {
                            populateMessageOnCurrentLocationTextView(message);
                        }

                        @Override
                        public void onFailedLocationUpdateStart(String failureReason) {
                            populateMessageOnCurrentLocationTextView(failureReason);
                        }
                    });
                    btnLocationUpdate.setText(Stop_String);
                }
                else{
                    // Stop the location capture
                    if(null != periodicLocationCallback){
                        locationProvider.stopLocationUpdates(locationUpdateButtonClickWorkflowContext, periodicLocationCallback);
                        periodicLocationCallback = null;
                    }
                    else{

                    }

                    btnLocationUpdate.setText(Start_String);
                }
            }
        });

        final Button btnGetFcmMessage = (Button) findViewById(R.id.btn_get_fcm_message);
        btnGetFcmMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceConstants.SharedPreferenceFileName, Context.MODE_PRIVATE);
                String content = FetchSharedPreference(sharedPreferences, SharedPreferenceConstants.ServiceCallTestMessage);
                populateMessageOnCurrentLocationTextView(content);
            }
        });

        final Button btnGetCurrentLocation = (Button) findViewById(R.id.btn_get_current_location);
        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkflowContext getCurrentLocationButtonClickWorkflowContext =
                        new WorkflowContext(Utility.ExtractResourceName(btnGetCurrentLocation.toString()), WorkflowSourceType.Button_Click);

                // TODO: As we are assigning a callback inside the activity, this may lead to memory leaks of GroupActivity every time the
                // screen is rotated. To avoid this we can make use of the LiveData or explicitly remove callback listener on suspend.
                // After the check location permission, the location should be fetched in the handler for location permission check complete.
                locationProvider.getCurrentLocation(getCurrentLocationButtonClickWorkflowContext, new ILocationCallback() {
                    @Override
                    public void onLocationCheckLogEventReceived(String message) {
                        populateMessageOnCurrentLocationTextView(message);
                    }

                    @Override
                    public void onCurrentLocationRequestComplete(Location location) {
                        //locationViewModel.reportOneTimeLocation(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()), Double.toString(location.getAltitude()), Double.toString(location.getSpeed()));
                        postProcessFetchedCurrentLocation(location);
                    }

                    @Override
                    public void onLocationRequestFailure(String failureReason) {
                        populateMessageOnCurrentLocationTextView(failureReason);
                    }

                    @Override
                    public void onRequestEnableGpsSettingRequired(ResolvableApiException resolvable) {
                        try
                        {
                            resolvable.startResolutionForResult(GroupActivity.this, REQUEST_GPS_SETTINGS);
                        }
                        catch(IntentSender.SendIntentException ex){
                            populateMessageOnCurrentLocationTextView("Send Intent exception received." + ex.getMessage());
                        }
                        catch (ClassCastException ex){
                            populateMessageOnCurrentLocationTextView("Class cast exception received." + ex.getMessage());
                        }
                    }
                });

                // Enqueue a one time job to the work manager
//                OneTimeWorkRequest currentLocationFetchWork = new OneTimeWorkRequest.Builder(PeriodicLocationFetchWorker.class).addTag(Constants.WORKER_TAG).build();
//                WorkManager.getInstance().enqueue(currentLocationFetchWork);
//                WorkManager.getInstance().getStatusById(currentLocationFetchWork.getId()).observe(GroupActivity.this, new android.arch.lifecycle.Observer<WorkStatus>() {
//                    @Override
//                    public void onChanged(@Nullable WorkStatus workStatus) {
//                        if(workStatus != null && workStatus.getState().isFinished()){
//                            populateMessageOnCurrentLocationTextView("Work has finished.");
//                        }
//                    }
//                });
            }
        });

        // Pass new handler as the handler to indicate that the result receiver will run on the current (UI) thread.
        // If null is passed then the result receiver would run on an arbitrary thread and you would not be able
        // to update the UI elements in that thread.
        locationAddressResultReceiver = new LocationAddressResultReceiver(new Handler());
        readGroupFromIntent();
        populateDisplay();
        //ObserveWorkManager();
    }

    private void ObserveWorkManager(){
        WorkManager.getInstance().getStatusesByTag("com.example.tapanj.mapsdemo.workmanager.PeriodicLocationFetchWorker").removeObservers(GroupActivity.this);
        WorkManager.getInstance().getStatusesByTag("com.example.tapanj.mapsdemo.workmanager.PeriodicLocationFetchWorker").observe(GroupActivity.this, new android.arch.lifecycle.Observer<List<WorkStatus>>() {
            @Override
            public void onChanged(@Nullable List<WorkStatus> workStatuses) {
                if(null != workStatuses){
                    for (WorkStatus workStatus :
                            workStatuses) {
                        if (workStatus != null && workStatus.getState().isFinished()) {
                            populateMessageOnCurrentLocationTextView(workStatus.getId() + ", Work has finished.");
                        } else {
                            logger.LogInformation(workStatus.getId() + ", Work State: " + workStatus.getState().name(), "");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void injectMembers(){
        AndroidInjection.inject(this);
        //((MainApplication)getApplication()).getMainApplicationComponent().inject(this);
    }

    @Override
    protected void initializeActivityLifecycleWorkflowContext() {
        this.activityLifecycleWorkflowContext = new WorkflowContext(GroupActivity.class.getSimpleName(), WorkflowSourceType.Activity_Create);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.locationAddressResultReceiver = null;
        this.logger.LogInformation("GroupActivity paused.", this.activityLifecycleWorkflowContext.getWorkflowId());
        //stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.locationAddressResultReceiver = new LocationAddressResultReceiver(new Handler());

        // Bind to the Fetch current location service
        Intent fetchCurrentLocationServiceIntent = new Intent(this, FetchCurrentLocationService.class);
        bindService(fetchCurrentLocationServiceIntent, fetchCurrentLocationServiceConnection, Context.BIND_AUTO_CREATE);
        this.logger.LogInformation("GroupActivity resumed.", this.activityLifecycleWorkflowContext.getWorkflowId());
        //startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(fetchCurrentLocationServiceConnection);
        this.logger.LogInformation("GroupActivity stopped.", this.activityLifecycleWorkflowContext.getWorkflowId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.logger.LogInformation("GroupActivity destroyed.", this.activityLifecycleWorkflowContext.getWorkflowId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == com.example.tapanj.mapsdemo.R.id.action_settings) {
            return true;
        }

        return false;
    }

    //endregion

    // region All private methods

    private void postProcessFetchedCurrentLocation(Location location){
        if (null != location) {
            CheckBox chkFetchAddress = (CheckBox) findViewById(R.id.chk_fetchAddress);
            if(chkFetchAddress.isChecked()){
                populateMessageOnCurrentLocationTextView("Fetching the address based on location.");

                if(!Geocoder.isPresent()){
                    populateMessageOnCurrentLocationTextView("No geocoder is available.");
                    return;
                }

                // Fetch the address by starting the Address search service.
                Intent locationServiceIntent = new Intent(GroupActivity.this, FetchAddressIntentService.class);
                locationServiceIntent.putExtra(Constants.RECEIVER, locationAddressResultReceiver);
                locationServiceIntent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
                startService(locationServiceIntent);
                populateMessageOnCurrentLocationTextView("Service started.");
            }
            else{
                //long startTime = System.currentTimeMillis();
                //double distanceInMetres = getDistanceInMetres(42.311129, -83.221113, 42.351129, -83.211113);
                //long endTime = System.currentTimeMillis();
                //long timeElapsedMillin = endTime - startTime;
                //initializeGeofence(location);
                //MyGeoCoder.getLocationAddress(location.getLatitude(), location.getLongitude(), 1);
                CheckBox chkShowMap = (CheckBox) findViewById(R.id.chk_showMap);
                if(chkShowMap.isChecked()){
                    Intent mapIntent = new Intent(GroupActivity.this, MapsActivity.class);
                    mapIntent.putExtra(MapsActivity.CURRENTLOCATION, location);
                    startActivity(mapIntent);
                }
                else{
                    logLocationToUi(location);
                }
            }
        } else {
            // The location may be null 1) if the location has been turned off in the device. 2) If the location was previously retreived because
            // disabling the location clears the cache. 3) The device never recorded the location in case of a new device. 4) Google play service
            // on the device has restarted and there is no active fused location provider client. To avoid these situations you can create a new
            // client and request location updates yourself.
            populateMessageOnCurrentLocationTextView("Location object is null");
        }

        //startLocationUpdates();
        //Button btnLocationUpdate = (Button)findViewById(com.example.tapanj.mapsdemo.R.id.btn_locationUpdates);
        //btnLocationUpdate.setText(Stop_String);
    }

    private void populateMessageOnCurrentLocationTextView(String message) {
        TextView tvCurrentLocation = (TextView) findViewById(R.id.textView_CurrentLocation);
        tvCurrentLocation.setText(message);
        logger.LogInformation(message, "");
    }

    private void saveToFile(String message) {
        File directory = getFilesDir();
        String path = directory.getAbsolutePath();
        File logFile = new File(directory, LOG_FILE_NAME);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(LOG_FILE_NAME, MODE_APPEND | MODE_PRIVATE));
            outputStreamWriter.append(message);
            outputStreamWriter.append("\n");
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private boolean checkAccessFineLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                populateMessageOnCurrentLocationTextView("Should show permission rationale. Requesting permission");

                // Show an explanation to the user asynchronously on why the permission is needed. Do not block this thread waiting for the user's response.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            } else {
                populateMessageOnCurrentLocationTextView("Should not show permission rationale. Requesting permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            }

            return false;
        }
        return true;
    }

    private void saveLocationToFile(Location location) {
        if(null != location){
            String locationDetail = LocationHelper.getLocationString(location);
            saveToFile(locationDetail);
        }
    }

    private void logLocationToUi(Location location) {
        if(null != location){
            String locationDetail = LocationHelper.getLocationString(location);
            populateMessageOnCurrentLocationTextView(locationDetail);
        }
    }

    private void populateDisplay() {
        if(null == this.mGroup){
            return;
        }

        TextView tvGroupName = (TextView) findViewById(com.example.tapanj.mapsdemo.R.id.textView_groupName);
        RecyclerView rvMembers = (RecyclerView) findViewById(com.example.tapanj.mapsdemo.R.id.recycler_members);
        RecyclerView rvHangouts = (RecyclerView) findViewById(R.id.recycler_hangouts);

        tvGroupName.setText(this.mGroup.getName());

        List<GroupMember> groupMembers = this.mGroup.getGroupMembers();
        if(null != groupMembers){
            if(membersRecyclerViewLayoutManager == null){
                membersRecyclerViewLayoutManager = new LinearLayoutManager(this);
            }

            rvMembers.setLayoutManager(membersRecyclerViewLayoutManager);

            // Specify an adapter
            GenericRecyclerViewAdapter membersRecyclerViewAdapter = new GenericRecyclerViewAdapter(groupMembers.toArray(), new GenericRecyclerViewAdapter.OnRecyclerItemClickListener<GroupMember>() {
                @Override
                public void onItemClick(GroupMember itemData) {
                    Intent memberClickIntent = new Intent(GroupActivity.this, GroupMemberActivity.class);
                    memberClickIntent.putExtra(GroupMemberActivity.GROUPMEMBERDETAIL, itemData);
                    startActivity(memberClickIntent);
                }
            });
            rvMembers.setAdapter(membersRecyclerViewAdapter);
        }

        List<Hangout> hangouts = this.mGroup.getGroupHangouts();
        if(null != hangouts){
            if(hangoutsRecyclerViewLayoutManager == null){
                hangoutsRecyclerViewLayoutManager = new LinearLayoutManager(this);
            }

            rvHangouts.setLayoutManager(hangoutsRecyclerViewLayoutManager);

            // Specify an adapter
            GenericRecyclerViewAdapter hangoutsRecyclerViewAdapter = new GenericRecyclerViewAdapter(hangouts.toArray(), new GenericRecyclerViewAdapter.OnRecyclerItemClickListener<Hangout>() {
                @Override
                public void onItemClick(Hangout itemData) {
                }
            });
            rvHangouts.setAdapter(hangoutsRecyclerViewAdapter);
        }
    }

    private void readGroupFromIntent() {
        Intent intent = getIntent();
        this.mGroup = intent.getParcelableExtra(GROUPDETAIL);
    }

    private void HandleLocationResult(int resultCode, Bundle resultData){

    }

    private double getDistanceInMetres(double lat1, double lon1, double lat2, double lon2){
        int earthRadiusInKm = 6371;
        double latitudeDifferenceInRadians = this.convertDegreeToRadians(lat2 - lat1);
        double longitudeDifferenceInRadians = this.convertDegreeToRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(latitudeDifferenceInRadians / 2), 2)
                + Math.cos(this.convertDegreeToRadians(lat1))
                    *Math.cos(this.convertDegreeToRadians(lat2))
                    *Math.pow(Math.sin(longitudeDifferenceInRadians / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distanceInMetres = earthRadiusInKm * c * 1000;
        return distanceInMetres;
    }

    private double convertDegreeToRadians(double degree){
        return degree * (Math.PI / 180);
    }

    private void initializeGeofence(Location location){
        String geofenceKey = "mytestgeofence";
        double latitude = 34.1786998;
        double longitude = -86.6154153;
        int geofenceRadiusInMetres = 100;
        int geofenceExpirationDurationInMilliseconds = 300000; // 300 seconds
        Geofence sampleGeofence = new Geofence.Builder()
                .setRequestId(geofenceKey)
                .setCircularRegion(location.getLatitude(), location.getLongitude(), geofenceRadiusInMetres)
                .setExpirationDuration(geofenceExpirationDurationInMilliseconds)
                .setNotificationResponsiveness(30000) // Set the responsiveness to 30 seconds so that the geofencing check happens every 5 min.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest sampleGeofencingRequest = new GeofencingRequest.Builder()
                // This tells the location service that Geofence_Transition_Enter should be trigerred if the device is already inside the geofence
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(sampleGeofence)
                .build();

        final GeofencingClient geofencingClient = new GeofencingClient(this);

        if(!checkAccessFineLocationPermission()){
            return;
        }

        geofencingClient.addGeofences(sampleGeofencingRequest, getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private PendingIntent getGeofencePendingIntent(){
        // Reuse the pending intent if we already have it.
        if(null != this.geofencePendingIntent){
            return this.geofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceTransitionIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addGeofences() and removeGeofences()
        this.geofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return this.geofencePendingIntent;
    }

    public void onBtnGetLocationPeriodicClicked(View view) {
        //setPeriodicWorkManagerRequest();
        //setAlarmManagerRequest();
        //setLocationFetchIntentServiceRequest();
        setLocationFetchServiceRequest();
    }

    public void onBtnGetServiceStatusClicked(View view) {
        EditText txtServiceStatus = (EditText) findViewById(R.id.txt_serviceStatus);
        txtServiceStatus.setText(fetchCurrentLocationService.fetchStatus());
    }

    private void setLocationFetchIntentServiceRequest()
    {
        Intent intent = new Intent(this, FetchCurrentLocationIntentService.class);
        startService(intent);
    }

    private void setLocationFetchServiceRequest()
    {
        Intent removeBatteryRestrictionsPermissionIntent = new Intent();
//        String deviceManufacturer = Build.MANUFACTURER;
//
//        switch (deviceManufacturer.toLowerCase()){
//            case "xiomi":
//                removeBatteryRestrictionsPermissionIntent.setComponent(new ComponentName("com.miui.securitycenter",
//                        "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//                break;
//            case "oppo":
//                removeBatteryRestrictionsPermissionIntent.setComponent(new ComponentName("com.coloros.safecenter",
//                        "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity"));
////                removeBatteryRestrictionsPermissionIntent.setComponent(new ComponentName("com.coloros.safecenter",
////                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
//                break;
//
//            case "vivo":
//                removeBatteryRestrictionsPermissionIntent.setComponent(new ComponentName("com.vivo.permissionmanager",
//                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
//                break;
//        }
//
//        List<ResolveInfo> permissionList = this.getPackageManager().queryIntentActivities(removeBatteryRestrictionsPermissionIntent, PackageManager.MATCH_DEFAULT_ONLY);
//        if(permissionList.size() > 0){
//            this.startActivity(removeBatteryRestrictionsPermissionIntent);
//        }

//        removeBatteryRestrictionsPermissionIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//        this.startActivity(removeBatteryRestrictionsPermissionIntent);

        Intent intent = new Intent(this, FetchCurrentLocationService.class);
        //stopService(intent);
        startService(intent);
    }

    private String FetchSharedPreference(SharedPreferences sharedPreferences, String keyName) {
        String storedPreference = sharedPreferences.getString(keyName, null);
        return storedPreference;
    }

    private void setAlarmManagerRequest(){
        long currentTimeInMs = System.currentTimeMillis();
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent fetchCurrentLocationIntent = new Intent(getApplicationContext(), FetchCurrentLocationAlarmReceiver.class);
        //boolean isRepeatingAlarmRunning = (PendingIntent.getBroadcast(getApplicationContext(), 0, fetchCurrentLocationIntent, PendingIntent.FLAG_NO_CREATE) != null);
        PendingIntent fetchCurrentLocationPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, fetchCurrentLocationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(fetchCurrentLocationPendingIntent);

//        if(!isRepeatingAlarmRunning){
//            PendingIntent fetchCurrentLocationPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, fetchCurrentLocationIntent, PendingIntent.FLAG_NO_CREATE);
//            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 30000, fetchCurrentLocationPendingIntent);
//        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currentTimeInMs + 3000, fetchCurrentLocationPendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentTimeInMs + 3000, fetchCurrentLocationPendingIntent);
        }
        else{
            alarmManager.set(AlarmManager.RTC_WAKEUP, currentTimeInMs + 3000, fetchCurrentLocationPendingIntent);
        }
    }

    private void setPeriodicWorkManagerRequest(){
        PeriodicWorkRequest.Builder periodicLocationRequestBuilder = new PeriodicWorkRequest.Builder(PeriodicLocationFetchWorker.class,
                15, TimeUnit.MINUTES).addTag(Constants.PERIODIC_LOCATION_REQUEST_TAG);

        WorkManager.getInstance().cancelAllWorkByTag("com.example.tapanj.mapsdemo.workmanager.PeriodicLocationFetchWorker");

        PeriodicWorkRequest periodicLocationRequest = periodicLocationRequestBuilder.build();
        WorkManager.getInstance().enqueue(periodicLocationRequest);
        WorkManager.getInstance().getStatusById(periodicLocationRequest.getId()).observe(GroupActivity.this, new android.arch.lifecycle.Observer<WorkStatus>() {
            @Override
            public void onChanged(@Nullable WorkStatus workStatus) {
                if(workStatus != null && workStatus.getState().isFinished()){
                    populateMessageOnCurrentLocationTextView("Work has finished.");
                }
                else{
                    logger.LogInformation("Work State: " + workStatus.getState().name(), "");
                }
            }
        });
    }
    //endregion

    //region ResultReceiver implementation
    class LocationAddressResultReceiver extends ResultReceiver {
        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(null == resultData){
                populateMessageOnCurrentLocationTextView("The Receive result data is null");
                return;
            }

            String addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if(null == addressOutput){
                populateMessageOnCurrentLocationTextView("The address output received is null");
            }

            if(resultCode == Constants.SUCCESS_RESULT){
                populateMessageOnCurrentLocationTextView("The address service succeeded.");
                populateMessageOnCurrentLocationTextView(addressOutput);
            }
            else{
                populateMessageOnCurrentLocationTextView("The address service failed." + addressOutput);
            }
        }
    }

    //endregion
}
