package com.example.tapanj.mapsdemo.activities.map;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.ActivityBase;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationCallback;
import com.example.tapanj.mapsdemo.common.location.interfaces.ILocationProvider;
import com.example.tapanj.mapsdemo.models.WorkflowContext;
import com.example.tapanj.mapsdemo.models.WorkflowSourceType;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.places.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import dagger.android.AndroidInjection;

import javax.inject.Inject;

public class MapsActivity extends ActivityBase implements OnMapReadyCallback, GoogleMap.OnCircleClickListener {

    //region Private variables
    private GoogleMap mMap;

    private GeoDataClient geoDataClient;

    private PlaceDetectionClient placeDetectionClient;

    private final int DEFAULT_ZOOM = 15;
    //endregion

    public static final String CURRENTLOCATION = "com.example.tapanj.mapsdemo.CURRENTLOCATION";

    private WorkflowContext workflowContext;

    @Inject
    ILocationProvider locationProvider;

    //region Overridden activity methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.geoDataClient = Places.getGeoDataClient(this);
        this.placeDetectionClient = Places.getPlaceDetectionClient(this);
        this.workflowContext = new WorkflowContext(MapsActivity.class.getSimpleName(), WorkflowSourceType.Activity_Create);
    }

    @Override
    protected void injectMembers(){
        //((MainApplication)getApplication()).getMainApplicationComponent().inject(this);
        AndroidInjection.inject(this);
    }

    @Override
    protected void initializeActivityLifecycleWorkflowContext() {
        this.activityLifecycleWorkflowContext = new WorkflowContext(MapsActivity.class.getSimpleName(), WorkflowSourceType.Activity_Create);
    }

    //endregion

    //region Overridden OnMapReadyCallBack methods
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCircleClickListener(this);
        Intent mapIntent = getIntent();
        Location currentLocation = mapIntent.getParcelableExtra(CURRENTLOCATION);

        //mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setIndoorLevelPickerEnabled(true);

        //mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.addCircle(new CircleOptions()
        .clickable(true)
        .center(current)
        .radius(500));

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

//        try{
//            // Customize the styling of the base map using a JSON object defined in a raw resource file.
//            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
//
//            if(!success){
//
//            }
//        }
//        catch(Resources.NotFoundException ex){
//
//        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, this.DEFAULT_ZOOM));
        this.locationProvider.getCurrentLocation(this.workflowContext, new ILocationCallback() {
            @Override
            public void onLocationCheckLogEventReceived(String message) {

            }

            @Override
            public void onCurrentLocationRequestComplete(Location location) {

            }

            @Override
            public void onLocationRequestFailure(String failureReason) {

            }

            @Override
            public void onRequestEnableGpsSettingRequired(ResolvableApiException ex) {

            }
        });
        this.handleInfoWindowAdapter();
    }

    private void handleInfoWindowAdapter() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                String title = marker.getTitle();
                String snippet = marker.getSnippet();
                return null;
            }
        });
    }

    @Override
    public void onCircleClick(Circle circle) {
        Toast.makeText(this, "Circle clicked", Toast.LENGTH_SHORT).show();
    }

    protected void onLocationPermissionCheckComplete(boolean isConnectSuccessful) {
        @SuppressWarnings("MissingPermission")
        Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                if(task.isSuccessful() && null != task.getResult()){
                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                    boolean isFirstRecord = true;
                    for(PlaceLikelihood placeLikelihood : likelyPlaces){
                        String name = (String)placeLikelihood.getPlace().getName();
                        String address = (String)placeLikelihood.getPlace().getAddress();
                        String attributions = (String)placeLikelihood.getPlace().getAttributions();
                        LatLng latLng = placeLikelihood.getPlace().getLatLng();
                        String marketSnippet = address;
                        if(null != attributions){
                            marketSnippet += "\n" + attributions;
                        }
                        if(isFirstRecord){
                            isFirstRecord = false;
                            mMap.addMarker(new MarkerOptions()
                                    .title(name)
                                    .position(latLng)
                                    .snippet(marketSnippet));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                        }
                    }
                    // Release the place likelyhood buffer to avoid memory leaks.
                    likelyPlaces.release();
                }
                else{
                    Exception ex = task.getException();
                }
            }
        });
    }
    //endregion
}
