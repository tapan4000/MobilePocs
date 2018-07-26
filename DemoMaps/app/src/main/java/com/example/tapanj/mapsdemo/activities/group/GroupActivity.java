package com.example.tapanj.mapsdemo.activities.group;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.LocationActivityBase;
import com.example.tapanj.mapsdemo.activities.map.MapsActivity;
import com.example.tapanj.mapsdemo.adapters.GenericRecyclerViewAdapter;
import com.example.tapanj.mapsdemo.common.Constants;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.dagger.CustomApplication;
import com.example.tapanj.mapsdemo.intentservice.FetchAddressIntentService;
import com.example.tapanj.mapsdemo.intentservice.GeofenceTransitionIntentService;
import com.example.tapanj.mapsdemo.interfaces.ILogger;
import com.example.tapanj.mapsdemo.models.*;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class GroupActivity extends LocationActivityBase {
    // region All private variables
    private Group mGroup;
    private RecyclerView.LayoutManager membersRecyclerViewLayoutManager;
    private RecyclerView.LayoutManager hangoutsRecyclerViewLayoutManager;
    private LocationAddressResultReceiver locationAddressResultReceiver;
    private WorkflowContext activityLifecycleWorkflowContext;
    private PendingIntent geofencePendingIntent;

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

                // After the check location permission, the location should be fetched in the handler for location permission check complete.
                checkLocationPermission(getCurrentLocationButtonClickWorkflowContext);
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
        checkLocationPermission(this.activityLifecycleWorkflowContext);
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

    //endregion

    // region All private methods

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
                            String locationString = getLocationString(location);
                            populateMessageOnCurrentLocationTextView(locationString);
                        }

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

    @Override
    protected void onCompleteLocationCheck(boolean isConnectSuccessful) {
        if(isConnectSuccessful){
            fetchLocationPostValidations();
        }
        else{

        }
    }

    @Override
    protected void onLocationCheckLogEventReceived(String logEvent) {
        populateMessageOnCurrentLocationTextView(logEvent);
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
