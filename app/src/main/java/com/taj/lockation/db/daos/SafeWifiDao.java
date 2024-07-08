package com.taj.lockation.db.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.taj.lockation.db.entities.SafeWifi;

import java.util.List;

@Dao
public interface SafeWifiDao
{
    @Query("SELECT * FROM SafeWifi")
    List<SafeWifi> getAll();

    @Query("SELECT * FROM SafeWifi WHERE id = :bssid")
    SafeWifi getById(String bssid);

    @Insert
    void insertAll(SafeWifi... safeWifis);

    @Query("SELECT id FROM SafeWifi ORDER BY id DESC LIMIT 1")
    String getLastId();
    @Delete
    void delete(SafeWifi safeWifi);

    @Query("DELETE FROM SafeWifi WHERE id = :bssid")
    void deleteById(String bssid);

    @Query("SELECT COUNT(id) FROM SafeWifi")
    int getCount();
}
