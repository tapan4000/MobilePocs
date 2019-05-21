package com.example.tapanj.mapsdemo.datastore.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import com.example.tapanj.mapsdemo.models.dao.EntityBase;
import com.example.tapanj.mapsdemo.models.dao.UserLocationSession;

@Dao
public interface BaseDao<TEntity extends EntityBase> {
    @Insert
    void addEntity(TEntity entity);

    @Delete
    void deleteEntity(TEntity entity);
}
