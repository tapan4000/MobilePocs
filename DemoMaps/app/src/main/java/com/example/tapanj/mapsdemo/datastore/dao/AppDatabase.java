package com.example.tapanj.mapsdemo.datastore.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import com.example.tapanj.mapsdemo.models.GroupMember;

@Database(entities = {GroupMember.class}, version = AppDatabase.VERSION)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    static final int VERSION = 1;

    public abstract GroupMemberDao groupMemberDao();

    public static AppDatabase getInstance(Context context){
        if(null == INSTANCE){
            synchronized (AppDatabase.class){
                if(null == INSTANCE){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "CmpDb").build();
                }
            }
        }

        return INSTANCE;
    }
}
