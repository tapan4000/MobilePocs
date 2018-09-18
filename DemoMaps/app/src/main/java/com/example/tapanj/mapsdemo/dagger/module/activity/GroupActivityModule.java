package com.example.tapanj.mapsdemo.dagger.module.activity;

import android.app.Activity;
import com.example.tapanj.mapsdemo.activities.group.GroupActivity;
import com.example.tapanj.mapsdemo.dagger.subcomponent.GroupActivitySubcomponent;
import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module(subcomponents = GroupActivitySubcomponent.class)
public abstract class GroupActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(GroupActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindGroupActivityInjectorFactory(GroupActivitySubcomponent.Builder builder);
}
