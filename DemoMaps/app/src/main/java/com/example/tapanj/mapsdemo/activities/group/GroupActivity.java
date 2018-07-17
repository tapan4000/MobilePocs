package com.example.tapanj.mapsdemo.activities.group;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.adapters.GenericRecyclerViewAdapter;
import com.example.tapanj.mapsdemo.adapters.MyGeoCoder;
import com.example.tapanj.mapsdemo.common.Constants;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.dagger.CustomApplication;
import com.example.tapanj.mapsdemo.intentservice.FetchAddressIntentService;
import com.example.tapanj.mapsdemo.intentservice.GeofenceTransitionIntentService;
import com.example.tapanj.mapsdemo.interfaces.ILogger;
import com.example.tapanj.mapsdemo.models.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class GroupActivity extends AppCompatActivity {
    // region All private variables
    private Group mGroup;
    private RecyclerView.LayoutManager membersRecyclerViewLayoutManager;
    private RecyclerView.LayoutManager hangoutsRecyclerViewLayoutManager;
    private LocationAddressResultReceiver locationAddressResultReceiver;
    private WorkflowContext activityLifecycleWorkflowContext;
    private PendingIntent geofencePendingIntent;

    private final int REQUEST_GPS_SETTINGS = 1;
    private final int REQUEST_ACCESS_FINE_LOCATION = 2;
    private final String LOG_FILE_NAME = "log.txt";
    private final String Start_String = "START";
    private final String Stop_String = "STOP";

    @Inject ILogger logger;

    //endregion

    // region All public static variables
    public static final String GROUPDETAIL = "com.example.tapanj.mapsdemo.GROUPDETAIL";
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    //endregion

    // region Overridden Activity methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityLifecycleWorkflowContext = new WorkflowContext(GroupActivity.class.getSimpleName(), WorkflowSourceType.Activity_Create);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
                    startLocationUpdates(locationUpdateButtonClickWorkflowContext);
                    btnLocationUpdate.setText(Stop_String);
                }
                else{
                    // Stop the location capture
                    stopLocationUpdates(locationUpdateButtonClickWorkflowContext);
                    btnLocationUpdate.setText(Start_String);
                }
            }
        });

        final Button btnGetCurrentLocation = (Button) findViewById(R.id.btn_get_current_location);
        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkflowContext getCurrentLocationButtonClickWorkflowContext =
                        new WorkflowContext(Utility.ExtractResourceName(btnGetCurrentLocation.toString()), WorkflowSourceType.Button_Click);
                populateCurrentLocation(getCurrentLocationButtonClickWorkflowContext);
            }
        });

        ((CustomApplication)getApplication()).getCustomApplicationComponent().inject(this);

        // Pass new handler as the handler to indicate that the result receiver will run on the current (UI) thread.
        // If null is passed then the result receiver would run on an arbitrary thread and you would not be able
        // to update the UI elements in that thread.
        locationAddressResultReceiver = new LocationAddressResultReceiver(new Handler());
        readGroupFromIntent();
        populateDisplay();
        initializeLocationUpdateCallback();
        populateCurrentLocation(this.activityLifecycleWorkflowContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.locationAddressResultReceiver = null;
        //stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.locationAddressResultReceiver = new LocationAddressResultReceiver(new Handler());
        //startLocationUpdates();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == com.example.tapanj.mapsdemo.R.id.action_settings) {
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_GPS_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made.
                        fetchLocationPostValidations();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings but chose not to.
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION:
                // If the request is cancelled, the results array is empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Perform the location related task.
                    askForGps();
                } else {
                    // Permission has been denied.
                }
                break;
        }
    }

    //endregion

    // region All private methods
    private void populateCurrentLocation(WorkflowContext workflowContext) {
        // Check if the location permissions are available
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            populateMessageOnCurrentLocationTextView("Location permission not available");

            // Permission is not granted. Ask the user to grant the permission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                populateMessageOnCurrentLocationTextView("Should show permission rationale. Requesting permission");
                // Show an explanation to the user asynchronously on why the permission is needed. Do not block this thread waiting for the user's response.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            } else {
                populateMessageOnCurrentLocationTextView("Should not show permission rationale. Requesting permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            populateMessageOnCurrentLocationTextView("Location permission available.");
            // If the location permissions are available, then check if GPS is available.
            askForGps();
        }
    }

    private void fetchLocationPostValidations() {
        populateMessageOnCurrentLocationTextView("Entered fetch location post validations.");
        if (!checkAccessFineLocationPermission())
        {
            return;
        }


        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
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
                        initializeGeofence(location);
                        MyGeoCoder.getLocationAddress(location.getLatitude(), location.getLongitude(), 1);
                        String locationString = getLocationString(location);
                        populateMessageOnCurrentLocationTextView(locationString);
                    }
                } else {
                    // The location may be null 1) if the location has been turned off in the device. 2) If the location was previously retreived because
                    // disabling the location clears the cache. 3) The device never recorded the location in case of a new device. 4) Google play service
                    // on the device has restarted and there is no active fused location provider client. To avoid these situations you can create a new
                    // client and request location updates yourself.
                    populateMessageOnCurrentLocationTextView("Location object is null");
                }
            }
        });

        //startLocationUpdates();
        //Button btnLocationUpdate = (Button)findViewById(com.example.tapanj.mapsdemo.R.id.btn_locationUpdates);
        //btnLocationUpdate.setText(Stop_String);
    }

    private void populateMessageOnCurrentLocationTextView(String message) {
        TextView tvCurrentLocation = (TextView) findViewById(R.id.textView_CurrentLocation);
        tvCurrentLocation.setText(message);
        logger.LogInformation(message, "");
    }

    private void initializeLocationUpdateCallback() {
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                String callbackWorkflowId = Utility.GetUniqueWorkflowId();
                populateMessageOnCurrentLocationTextView("Location callback invoked.");
                //logger.LogVerbose("Location callback invoked.", callbackWorkflowId);
                if (null == locationResult) {
                    populateMessageOnCurrentLocationTextView("Location result is null.");
                    //logger.LogError("Location result is null", callbackWorkflowId);
                    return;
                }

                for(Location location: locationResult.getLocations()){
                    // Store the location information in local file.
                    String locationDetail = getLocationString(location);
                    populateMessageOnCurrentLocationTextView(locationDetail);
                    saveToFile(locationDetail);
                }
            }
        };
    }

    private String getLocationString(Location location) {
        String provider = location.getProvider();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        long time = location.getTime();

        float accuracy = -1;
        if(location.hasAccuracy()){
            accuracy = location.getAccuracy();
        }

        double altitude = -1;
        if(location.hasAltitude()){
            altitude = location.getAltitude();
        }

        float speed = -1;
        if(location.hasSpeed()){
            speed = location.getSpeed();
        }

        float bearing = -1;
        if(location.hasBearing())
        {
            bearing = location.getBearing();
        }

        String strDate = getCurrentDateTime();

        String locationDetail = strDate + " - Provider:" + provider + ", Latitude:" + latitude + ", Longitude:" + longitude + ", Time:" + time + ", Accuracy:" + accuracy
                + ", Altitude:" + altitude + ", Speed:" + speed + ", Bearing:" + bearing;

        return locationDetail;
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

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }

    private void startLocationUpdates(WorkflowContext workflowContext) {
        if(null != mfusedLocationProviderClient){
            if (!checkAccessFineLocationPermission())
                return;

            LocationRequest highAccuracyLocationRequest = new LocationRequest();
            highAccuracyLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            highAccuracyLocationRequest.setInterval(10000);
            highAccuracyLocationRequest.setFastestInterval(5000);
            mfusedLocationProviderClient.requestLocationUpdates(highAccuracyLocationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates(WorkflowContext workflowContext) {
        if(null != mfusedLocationProviderClient){
            mfusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private boolean checkAccessFineLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        return true;
    }

    private void askForGps(){
        LocationRequest highAccuracyLocationRequest = new LocationRequest();
        highAccuracyLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        highAccuracyLocationRequest.setInterval(10000);
        highAccuracyLocationRequest.setFastestInterval(5000);

//        LocationRequest balancedPowerAccuracyLocationRequest = new LocationRequest();
//        balancedPowerAccuracyLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        balancedPowerAccuracyLocationRequest.setInterval(10000);
//        balancedPowerAccuracyLocationRequest.setFastestInterval(5000);
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(highAccuracyLocationRequest);
//                .addLocationRequest(balancedPowerAccuracyLocationRequest);

        LocationServices.getSettingsClient(this)
                .checkLocationSettings(locationSettingsRequestBuilder.build())
                .addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            populateMessageOnCurrentLocationTextView("Location service response received.");
                            LocationSettingsResponse response = task.getResult(ApiException.class);

                            // If the GPS is available, fetch the current location.
                            fetchLocationPostValidations();
                        } catch (ApiException ex) {
                            populateMessageOnCurrentLocationTextView("Exception received: " + ex.getMessage());
                            switch (ex.getStatusCode()){
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied, but could be fixed by showing the user a dialog
                                    try{
                                        populateMessageOnCurrentLocationTextView("Resolution required Exception received. Sending request GPS enable request.");

                                        // Cast the resolvable exception
                                        ResolvableApiException resolvable = (ResolvableApiException) ex;

                                        // Show the dialog by calling startResolutionForResult() and check the result in onActivityResult.
                                        resolvable.startResolutionForResult(GroupActivity.this, REQUEST_GPS_SETTINGS);
                                    }
                                    catch(IntentSender.SendIntentException exception){
                                        populateMessageOnCurrentLocationTextView("Send Intent exception received." + ex.getMessage());
                                    }
                                    catch (ClassCastException exception){
                                        populateMessageOnCurrentLocationTextView("Class cast exception received." + ex.getMessage());
                                    }

                                    break;

                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog
                                    populateMessageOnCurrentLocationTextView("GPS Settings change is unavailable. No way to fix settings");
                                    break;
                            }
                        }
                    }
                });
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
