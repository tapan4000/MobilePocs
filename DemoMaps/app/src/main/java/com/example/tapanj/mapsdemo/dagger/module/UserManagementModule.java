package com.example.tapanj.mapsdemo.dagger.module;

import com.example.tapanj.mapsdemo.datastore.sharedPreference.Interfaces.ISharedPreferenceProvider;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.SharedPreferenceProvider;
import com.example.tapanj.mapsdemo.integration.Retrofit.ILocationRetrofitAdapter;
import com.example.tapanj.mapsdemo.integration.Retrofit.IUserRetrofitAdapter;
import com.example.tapanj.mapsdemo.integration.Retrofit.ServiceBuilder;
import com.example.tapanj.mapsdemo.repository.LocationRepository;
import com.example.tapanj.mapsdemo.repository.interfaces.ILocationRepository;
import com.example.tapanj.mapsdemo.repository.interfaces.IUserRepository;
import com.example.tapanj.mapsdemo.repository.UserRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class UserManagementModule {
    @Singleton
    @Provides
    ISharedPreferenceProvider sharedPreferenceProvider(){
        return new SharedPreferenceProvider();
    }

    @Singleton
    @Provides
    IUserRetrofitAdapter userRetrofitAdapter(){
        return ServiceBuilder.buildService(IUserRetrofitAdapter.class);
    }

    @Singleton
    @Provides
    IUserRepository userRepository(IUserRetrofitAdapter userRetrofitAdapter, ISharedPreferenceProvider sharedPreferenceProvider){
        return new UserRepository(userRetrofitAdapter, sharedPreferenceProvider);
    }
}
