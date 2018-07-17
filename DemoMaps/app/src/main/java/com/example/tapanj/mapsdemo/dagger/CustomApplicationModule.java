package com.example.tapanj.mapsdemo.dagger;

import com.example.tapanj.mapsdemo.activities.group.GroupActivity;
import com.example.tapanj.mapsdemo.common.logging.FileLogProvider;
import com.example.tapanj.mapsdemo.integration.Retrofit.GroupRetrofitAdapter;
import com.example.tapanj.mapsdemo.interfaces.ILogProvider;
import com.example.tapanj.mapsdemo.interfaces.integration.IGroupAdapter;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public class CustomApplicationModule {
    @Provides
    public IGroupAdapter groupAdapter(){
        return new GroupRetrofitAdapter();
    }
}
