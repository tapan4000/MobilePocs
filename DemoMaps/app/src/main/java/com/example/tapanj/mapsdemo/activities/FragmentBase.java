package com.example.tapanj.mapsdemo.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;

public abstract class FragmentBase extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeFragmentLifecycleWorkflowContext();
    }

    @Override
    public void onAttach(Context context) {
        this.injectMembers();
        super.onAttach(context);
    }

    protected void displayMessage(String message){
        //Toast.makeText(getActivity(), appUnhandledErrorMessage, Toast.LENGTH_LONG).show();
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void requestToReportToAdministrator(String message){
        displayMessage(message);
    }

    protected abstract void initializeFragmentLifecycleWorkflowContext();

    protected abstract void injectMembers();
}
