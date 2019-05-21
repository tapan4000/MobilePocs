package com.example.tapanj.mapsdemo.activities.user;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.FragmentBase;
import com.example.tapanj.mapsdemo.activities.group.GroupActivity;
import com.example.tapanj.mapsdemo.common.AppOperationStatusCodes;
import com.example.tapanj.mapsdemo.common.Constants;
import com.example.tapanj.mapsdemo.common.ServicePublicStatusCodes;
import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.SharedPreferenceConstants;
import com.example.tapanj.mapsdemo.repository.ResourceEvent;
import com.example.tapanj.mapsdemo.repository.interfaces.IGroupMemberRepository;
import com.example.tapanj.mapsdemo.models.user.User;
import com.example.tapanj.mapsdemo.repository.Resource;
import com.example.tapanj.mapsdemo.viewmodel.UserViewModel;
import com.example.tapanj.mapsdemo.viewmodel.ViewModelFactory;
import dagger.android.support.AndroidSupportInjection;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserLoginFragment extends FragmentBase {
    private UserViewModel userViewModel;

    private TextView txtMobileNumber;

    private TextView txtPassword;

    @Inject
    IGroupMemberRepository groupMemberRepository;

    @Inject
    ViewModelFactory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_userlogin, container, false);
        // Instantiate the view model
        this.userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        try{
            Date dateFormatted = Utility.getDateFromString("2019-04-04T11:18:08.7522264Z");
            Date tokenExpirationTime2 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSSS'Z'").parse("2019-04-04T11:18:08.7522264Z");
            String val = tokenExpirationTime2.toString();
            long val2 = tokenExpirationTime2.getTime();
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        // TODO: Rename all the variables to use the format type_Activity_id
        // Add the handler for login button
        SharedPreferences sharedPreferences = rootView.getContext().getSharedPreferences(SharedPreferenceConstants.SharedPreferenceFileName, Context.MODE_PRIVATE);
        this.txtMobileNumber = rootView.findViewById(R.id.txt_userMobile);
        this.txtPassword = rootView.findViewById(R.id.txt_password);
        Button btnLogin = rootView.findViewById(R.id.btn_userLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the login method on the view model.
                        userViewModel
                        .loginUser( sharedPreferences, "+91", txtMobileNumber.getText().toString(), txtPassword.getText().toString());
            }
        });

        userViewModel
                .loginResponse
                .observe(UserLoginFragment.this, new Observer<ResourceEvent<User>>() {
                    @Override
                    public void onChanged(@Nullable ResourceEvent<User> userResourceEvent) {
                        Resource<User> userResource = userResourceEvent.getContentIfNotHandled();
                        if(null == userResource){
                            return;
                        }

                        // Check if the network call has completed (by checking if httpstatus is not 0) and the response is a httpstatus 200
                        if(userResource.httpStatus != 0){
                            if(userResource.httpStatus == 200){
                                // Check the internal operationState code to determine if the call is successful.
                                if(userResource.serviceOperationStatus == 200)
                                {
                                    if(userResource.data != null
                                            && userResource.data.UserAuthInfo != null
                                            && userResource.data.UserAuthInfo.UserAuthToken != null
                                            && !userResource.data.UserAuthInfo.UserAuthToken.isEmpty()){
                                        // Check if the token expiration time is still valid.
                                        Date currentUtcDateTime = Utility.getCurrentUtcDateTime();
                                        if(userResource.data.UserAuthInfo.AuthTokenExpirationDateTime != null
                                                && userResource.data.UserAuthInfo.AuthTokenExpirationDateTime.compareTo(currentUtcDateTime) > 0){
                                            // The token is still active and we can safely pass the user to the dashboard.
                                            Intent loginCompleteIntent = new Intent(getActivity(), GroupActivity.class);
                                            startActivity(loginCompleteIntent);
                                        }
                                        else{
                                            // We need to wait to get the refreshed token. So, no action needed here.
                                            // TODO: Check if this scenario can happen as the internal operationState field is already populated.
                                        }
                                    }
                                }
                                else if(userResource.serviceOperationStatus == 400)
                                {
                                    displayMessage(getString(R.string.generic_internal_400));
                                }
                                else if(userResource.serviceOperationStatus == 610)
                                {
                                    displayMessage(getString(R.string.internal_610));
                                }
                                else if(userResource.serviceOperationStatus == 611)
                                {
                                    displayMessage(getString(R.string.internal_611));
                                }
                                else if(userResource.serviceOperationStatus == 602)
                                {
                                    // TODO: Take the user to the OTP screen and display below appUnhandledErrorMessage.
                                    displayMessage(getString(R.string.internal_602));
                                }
                                else if(userResource.serviceOperationStatus == 603)
                                {
                                    displayMessage(getString(R.string.internal_603));
                                }
                                else if(userResource.serviceOperationStatus == 604)
                                {
                                    displayMessage(getString(R.string.internal_604));
                                }
                                else if(userResource.serviceOperationStatus == 605)
                                {
                                    displayMessage(getString(R.string.internal_605));
                                }
                                else if(userResource.serviceOperationStatus == 612)
                                {
                                    // TODO: Take the user to the OTP screen. An SMS would have been sent in the background.
                                }
                                else {
                                    displayMessage(getString(R.string.generic_issue));
                                }
                            }
                            else if(userResource.httpStatus == 400){
                                // Display a appUnhandledErrorMessage indicating bad request.
                                displayMessage(getString(R.string.generic_http_400));
                            }
                            else if(userResource.httpStatus == 500){
                                // Display a appUnhandledErrorMessage indicating internal server error on the server.
                                displayMessage(getString(R.string.generic_http_500));
                            }
                            else if(userResource.httpStatus == AppOperationStatusCodes.APICALLFAILURE_ERRORCODE){
                                if(userResource.appUnhandledErrorMessage.isEmpty()){
                                    displayMessage(getString(R.string.generic_issue));
                                }
                                else {
                                    // In case there is an error, the error should be stored on the user's machine and user should be asked to
                                    // report that error to the admin and still the generic appUnhandledErrorMessage should be shown.
                                    requestToReportToAdministrator(userResource.appUnhandledErrorMessage);
                                }
                            }
                        }
                    }
                });
        return rootView;
    }

    @Override
    protected void initializeFragmentLifecycleWorkflowContext() {

    }

    @Override
    protected void injectMembers() {
        AndroidSupportInjection.inject(this);
    }
}
