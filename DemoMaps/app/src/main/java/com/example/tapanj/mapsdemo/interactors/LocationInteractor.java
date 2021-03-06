package com.example.tapanj.mapsdemo.interactors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import com.example.tapanj.mapsdemo.common.AppOperationStatusCodes;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.Interfaces.ISharedPreferenceProvider;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.SharedPreferenceConstants;
import com.example.tapanj.mapsdemo.enums.LocationCaptureSessionStateEnum;
import com.example.tapanj.mapsdemo.interactors.interfaces.ILocationInteractor;
import com.example.tapanj.mapsdemo.models.backendModels.request.location.InitiateEmergencySessionForSelfRequestModel;
import com.example.tapanj.mapsdemo.models.backendModels.request.location.LocationRequestModel;
import com.example.tapanj.mapsdemo.models.backendModels.response.location.InitiateLocationCaptureResponseModel;
import com.example.tapanj.mapsdemo.models.dao.UserLocationSession;
import com.example.tapanj.mapsdemo.models.retrofit.ApiResponse;
import com.example.tapanj.mapsdemo.models.user.UserInfo;
import com.example.tapanj.mapsdemo.repository.ResourceEvent;
import com.example.tapanj.mapsdemo.repository.interfaces.ILocationRepository;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

public class LocationInteractor implements ILocationInteractor {
    private ISharedPreferenceProvider sharedPreferenceProvider;

    private ILocationRepository locationRepository;

    public LocationInteractor(ISharedPreferenceProvider sharedPreferenceProvider, ILocationRepository locationRepository){
        this.sharedPreferenceProvider = sharedPreferenceProvider;
        this.locationRepository = locationRepository;
    }

    public LiveData<ResourceEvent<InitiateLocationCaptureResponseModel>> initiateEmergencyForSelf(SharedPreferences sharedPreferences, String emergencySessionTitle
            , LocationCaptureSessionStateEnum locationCaptureSessionState
            , int groupId
            , Date requestDateTime
            , int locationCapturePeriodInSeconds
            , LocationRequestModel location){
        // TODO: We should have a seperate response object used by the repository, viewmodel and activity. This resource object should be
        // populated with the operationState from the response model. The resource should contain an HTTP operationState code and normal operationState code
        // Based on the flow, the code should decide which codes to look at. Suppose we made a call for initiate emergency and it failed some
        // db check, then we need to have a operationState code for that operationState as well. These codes correspond to issues that happen on client side
        // checks itself.

        // Check the local data store to see if any existing location session is already going on. If it is going on, send a response to
        // the user indicating another emergency session already active. If any active session is present user should always see the button to
        // stop the existing session instead of allowing the user to create a new session.
        UserInfo userInfo = this.sharedPreferenceProvider.FetchSharedPreference(sharedPreferences, SharedPreferenceConstants.UserInfoKeyName, new TypeToken<UserInfo>(){});
        MediatorLiveData<ResourceEvent<InitiateLocationCaptureResponseModel>> result = new MediatorLiveData<>();
        LiveData<ResourceEvent<List<UserLocationSession>>> userLocationSessions = this.locationRepository.getAllLocationSessionsByUserId(userInfo.UserId);
        return Transformations.map(userLocationSessions, (sessionList) -> {
            List<UserLocationSession> sessions = sessionList.peekContent().getData();
            if(null != sessions){
                for(UserLocationSession userLocationSession: sessions){
                    LocationCaptureSessionStateEnum locationCaptureSessionStateId = userLocationSession.getLocationCaptureSessionStateId();
                    if(locationCaptureSessionStateId == LocationCaptureSessionStateEnum.Active
                            || locationCaptureSessionStateId == LocationCaptureSessionStateEnum.PendingSyncWithLocationProvider){
                        // TODO: See if we need to check the PendingWithLocationProvider state or if this state would never be needed.
                        // Check if the expiry date time has already been crossed. If that is the case then the session needs to be marked as expired.
                        // The entry will only be removed once it's state change has been notified to the server successfully.
                        if(Utility.isInstantGreaterThanCurrentUtcInstant(userLocationSession.getExpiryDateTime())){
                            // If the session is still active, then we need to display message to the user indicating another session
                            // cannot be started.
                            result.setValue(ResourceEvent.errorAppOperation(null, AppOperationStatusCodes.Emergency_Already_In_Progress, null));
                            return;
                        }
                    }
                }
            }

            return null;
        });

        result.addSource(storedSessions, new Observer<List<UserLocationSession>>() {
            @Override
            public void onChanged(@Nullable List<UserLocationSession> userLocationSessions) {
                result.removeSource(storedSessions);

                // Check if the user has any active and non-expired session present.


                // If there is no active session go ahead and create an entry in the SQLite database and notify the server.
                new AsyncTask<Void, Void, Void>(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        UserLocationSession userLocationSession = new UserLocationSession(){
                        };
                        //TODO: Assign the values for user location session.
                        userLocationSessionDao.addUserLocationSession(userLocationSession);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        // After saving the record in the database, we need to make a call to the network.
                        InitiateEmergencySessionForSelfRequestModel requestModel = new InitiateEmergencySessionForSelfRequestModel();
                        requestModel.EmergencySessionTitle = emergencySessionTitle;
                        requestModel.GroupId = groupId;
                        requestModel.RequestDateTime = requestDateTime;
                        requestModel.LocationCapturePeriodInSeconds = locationCapturePeriodInSeconds;
                        requestModel.Location = location;
                        LiveData<ApiResponse<InitiateLocationCaptureResponseModel>> initiateEmergencyCall = locationRetrofitAdapter.initiateEmergencyForSelf(requestModel);
                        result.addSource(initiateEmergencyCall, new Observer<ApiResponse<InitiateLocationCaptureResponseModel>>() {
                            @Override
                            public void onChanged(@Nullable ApiResponse<InitiateLocationCaptureResponseModel> initiateLocationCaptureResponseModelApiResponse) {
                                result.removeSource(initiateEmergencyCall);
                                if(initiateLocationCaptureResponseModelApiResponse.isSuccessful()){
                                    // Check if the status code for notifying server is successful. In that case, update the status in local
                                    // db to server notified.
                                }
                                else {
                                    // Return the http status code to client to display appropriate message. However, even on network error
                                    // the local emergency session would continue.
                                }
                            }
                        });
                    }
                }.execute();
            }
        });

        return result;
    }
}
