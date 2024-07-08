package com.taj.lockation.db.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.taj.lockation.db.entities.SafeLocation;

import java.util.List;

@Dao
public interface SafeLocationDao
{
    @Query("SELECT * FROM SafeLocation")
    List<SafeLocation> getAll();

    @Query("SELECT * FROM SafeLocation WHERE id = :locId")
    SafeLocation getById(int locId);

    @Insert
    void insertAll(SafeLocation... safeLocations);

    @Query("SELECT id FROM SafeLocation ORDER BY id DESC LIMIT 1")
    int getLastId();

    @Delete
    void delete(SafeLocation safeLocation);

    @Query("DELETE FROM SafeLocation WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT COUNT(id) FROM SafeLocation")
    int getCount();
}
