package com.example.tapanj.mapsdemo.dagger.module;

import android.app.Application;
import com.example.tapanj.mapsdemo.datastore.dao.AppDatabase;
import com.example.tapanj.mapsdemo.datastore.dao.GroupMemberDao;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class RoomModule {
//    private AppDatabase appDatabase;
//
//    public RoomModule(Application application){
//        this.appDatabase = AppDatabase.getInstance(application);
//    }

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
