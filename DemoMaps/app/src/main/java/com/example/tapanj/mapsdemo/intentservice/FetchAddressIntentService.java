package com.example.tapanj.mapsdemo.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.common.Constants;
import com.example.tapanj.mapsdemo.dagger.CustomApplication;
import com.example.tapanj.mapsdemo.interfaces.ILogger;
import com.example.tapanj.mapsdemo.models.IntentServiceResult;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentServiceBase {
    public FetchAddressIntentService(){
        super(FetchAddressIntentService.class.getName());
    }

    @Override
    protected IntentServiceResult ProcessIntent(Intent intent) {
        String errorMessage = "";

        // Get the location passed to this service through extra.
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        this.resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        List<Address> resolvedAddresses = null;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try{
            resolvedAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch(IOException ioException){
            errorMessage = getString(R.string.service_not_available);
            logger.LogException(ioException, errorMessage, this.workflowContext.getWorkflowId());
        }
        catch(IllegalArgumentException illegalArgumentException){
            errorMessage = getString(R.string.invalid_lat_long_used);
            logger.LogException(illegalArgumentException, errorMessage + " Data passed: (" + location.getLatitude() + ", " + location.getLongitude() + ").", this.workflowContext.getWorkflowId());
        }

        if(null == resolvedAddresses){
            logger.LogError("Resolved Addresses are null.", this.workflowContext.getWorkflowId());
            if(errorMessage.isEmpty()){
                errorMessage = getString(R.string.address_list_null);
                logger.LogError(errorMessage, this.workflowContext.getWorkflowId());
            }

            return new IntentServiceResult(Constants.FAILURE_RESULT, errorMessage);
        }
        else  if(resolvedAddresses.size() == 0){
            logger.LogError("No address found.", this.workflowContext.getWorkflowId());
            if(errorMessage.isEmpty()){
                errorMessage = getString(R.string.no_address_found);
                logger.LogError(errorMessage, this.workflowContext.getWorkflowId());
            }

            return new IntentServiceResult(Constants.FAILURE_RESULT, errorMessage);
        }
        else{
            Address address = resolvedAddresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines, join them and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                addressFragments.add(address.getAddressLine(i));
            }

            logger.LogVerbose(getString(R.string.address_found), this.workflowContext.getWorkflowId());
            return new IntentServiceResult(Constants.SUCCESS_RESULT, TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }
    }

    /*protected static JSONObject getLocationFormGoogle(String placesName) {

        String apiRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + placesName; //+ "&ka&sensor=false"
        HttpGet httpGet = new HttpGet(apiRequest);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return jsonObject;
    }

    protected static String getCityAddress( JSONObject result ){
        if( result.has("results") ){
            try {
                JSONArray array = result.getJSONArray("results");
                if( array.length() > 0 ){
                    JSONObject place = array.getJSONObject(0);
                    JSONArray components = place.getJSONArray("address_components");
                    for( int i = 0 ; i < components.length() ; i++ ){
                        JSONObject component = components.getJSONObject(i);
                        JSONArray types = component.getJSONArray("types");
                        for( int j = 0 ; j < types.length() ; j ++ ){
                            if( types.getString(j).equals("locality") ){
                                return component.getString("long_name");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }*/
}