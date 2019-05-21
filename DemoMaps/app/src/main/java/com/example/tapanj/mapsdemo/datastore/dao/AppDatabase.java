package com.example.tapanj.mapsdemo.datastore.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import com.example.tapanj.mapsdemo.common.converters.OffsetDateTimeConverter;
import com.example.tapanj.mapsdemo.models.dao.GroupMember;
import com.example.tapanj.mapsdemo.models.dao.UserLocationSession;
import com.example.tapanj.mapsdemo.models.dao.UserLocationSessionDetail;
import com.example.tapanj.mapsdemo.models.user.UserTable;

@Database(entities = {GroupMember.class, UserTable.class, UserLocationSession.class, UserLocationSessionDetail.class}, version = AppDatabase.VERSION, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    static final int VERSION = 1;

    // Reference to DAO's
    public abstract GroupMemberDao groupMemberDao();

    public abstract UserLocationSessionDao userLocationSessionDao();

    public abstract UserLocationSessionDetailDao userLocationSessionDetailDao();

    // Provide the instance of the database.
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
