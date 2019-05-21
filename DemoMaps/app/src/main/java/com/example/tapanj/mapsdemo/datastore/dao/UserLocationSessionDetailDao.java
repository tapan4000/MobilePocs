package com.example.tapanj.mapsdemo.datastore.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import com.example.tapanj.mapsdemo.models.dao.UserLocationSession;
import com.example.tapanj.mapsdemo.models.dao.UserLocationSessionDetail;

import java.util.List;

@Dao
public interface UserLocationSessionDetailDao extends BaseDao<UserLocationSessionDetail> {
    @Query("Select * from UserLocationSessionDetail where UserLocationSessionLocalId = :sessionLocalId")
    LiveData<List<UserLocationSessionDetail>> getSessionDetailBySessionLocalId(String sessionLocalId);
}
