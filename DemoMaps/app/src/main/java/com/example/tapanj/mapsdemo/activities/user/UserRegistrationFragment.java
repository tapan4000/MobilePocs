package com.example.tapanj.mapsdemo.activities.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.FragmentBase;

public class UserRegistrationFragment extends FragmentBase {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_userregistration, container, false);
        return rootView;
    }

    @Override
    protected void initializeFragmentLifecycleWorkflowContext() {

    }

    @Override
    protected void injectMembers() {

    }
}
