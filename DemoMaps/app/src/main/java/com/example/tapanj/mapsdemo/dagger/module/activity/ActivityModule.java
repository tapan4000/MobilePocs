package com.example.tapanj.mapsdemo.dagger.module.activity;

import com.example.tapanj.mapsdemo.activities.group.GroupListActivity;
import com.example.tapanj.mapsdemo.activities.group.GroupMemberActivity;
import com.example.tapanj.mapsdemo.activities.map.MapsActivity;
import com.example.tapanj.mapsdemo.dagger.scope.ActivityScope;
import com.example.tapanj.mapsdemo.intentservice.FetchAddressIntentService;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = {})
    abstract GroupListActivity contributeGroupListActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {})
    abstract GroupMemberActivity contributeGroupMemberActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {})
    abstract MapsActivity contributeMapsActivityInjector();
}
