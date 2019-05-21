package com.example.tapanj.mapsdemo.datastore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.example.tapanj.mapsdemo.models.dao.GroupMember;
import com.example.tapanj.mapsdemo.models.dao.UserLocationSession;

import java.util.List;

@Dao
public interface UserLocationSessionDao extends BaseDao<UserLocationSession> {
    @Query("Select * from UserLocationSession where UserId = :userId")
    LiveData<List<UserLocationSession>> getAllSessionsByUserId(int userId);

    @Insert
    void addUserLocationSession(UserLocationSession userLocationSession);
}
