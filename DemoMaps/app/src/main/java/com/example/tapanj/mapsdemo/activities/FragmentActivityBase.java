package com.example.tapanj.mapsdemo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

public abstract class FragmentActivityBase extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeFragmentActivityLifecycleWorkflowContext();
        this.injectMembers();
    }
    protected abstract void initializeFragmentActivityLifecycleWorkflowContext();

    protected abstract void injectMembers();
}
