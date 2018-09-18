package com.example.tapanj.mapsdemo.dagger.subcomponent;


import com.example.tapanj.mapsdemo.activities.group.GroupActivity;
import com.example.tapanj.mapsdemo.dagger.module.activity.GroupActivityModule;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent(modules = {})
public interface GroupActivitySubcomponent extends AndroidInjector<GroupActivity> {
    @Subcomponent.Builder
    public abstract class Builder extends AndroidInjector.Builder<GroupActivity>{}
}
