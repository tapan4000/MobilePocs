package com.example.tapanj.mapsdemo.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class FragmentBase extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeFragmentLifecycleWorkflowContext();
        this.injectMembers();
    }

    protected abstract void initializeFragmentLifecycleWorkflowContext();

    protected abstract void injectMembers();
}
