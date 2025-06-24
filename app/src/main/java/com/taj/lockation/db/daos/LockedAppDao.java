package com.taj.lockation.db.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.taj.lockation.db.entities.LockedApp;

import java.util.List;
import java.util.Set;

@Dao
public interface LockedAppDao
{
    @Query("SELECT * FROM LockedApp")
    List<LockedApp> getAll();
    @Query("SELECT id FROM LockedApp")
    List<String> getAllPackageNames();

    @Query("SELECT * FROM LockedApp WHERE id = :id")
    LockedApp get(String id);

    @Insert
    void insertAll(LockedApp... objs);

    @Delete
    void delete(LockedApp obj);

    @Query("DELETE FROM LockedApp WHERE id = :id")
    void deleteById(String id);

    @Query("SELECT COUNT(id) FROM LockedApp")
    int getCount();
}
