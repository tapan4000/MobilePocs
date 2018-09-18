package com.example.tapanj.mapsdemo.dagger.module;

import android.app.Application;
import com.example.tapanj.mapsdemo.datastore.dao.AppDatabase;
import com.example.tapanj.mapsdemo.datastore.dao.GroupMemberDao;
import com.example.tapanj.mapsdemo.datastore.file.FileProvider;
import com.example.tapanj.mapsdemo.interfaces.datastore.IFileProvider;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DataStoreModule {
//    Application application;
//
//    public DataStoreModule(Application application){
//        this.application = application;
//    }

    @Provides
    @Singleton
    IFileProvider fileProvider(Application application){
        return new FileProvider(application);
    }

    @Singleton
    @Provides
    AppDatabase appDatabase(Application application){
        return AppDatabase.getInstance(application);
    }

    @Singleton
    @Provides
    GroupMemberDao groupMemberDao(Application application){
        return AppDatabase.getInstance(application).groupMemberDao();
    }
}
