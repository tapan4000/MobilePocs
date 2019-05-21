package com.example.tapanj.mapsdemo.dagger.module.fragment;

import com.example.tapanj.mapsdemo.activities.user.UserLoginFragment;
import com.example.tapanj.mapsdemo.activities.user.UserRegistrationFragment;
import com.example.tapanj.mapsdemo.dagger.scope.ActivityScope;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = {})
    abstract UserLoginFragment contributeUserLoginFragmentInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {})
    abstract UserRegistrationFragment contributeUserRegistrationFragmentInjector();
}
