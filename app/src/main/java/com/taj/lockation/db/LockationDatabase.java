package com.taj.lockation.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.taj.lockation.db.daos.LockedAppDao;
import com.taj.lockation.db.daos.SafeLocationDao;
import com.taj.lockation.db.daos.SafeWifiDao;
import com.taj.lockation.db.entities.LockedApp;
import com.taj.lockation.db.entities.SafeLocation;
import com.taj.lockation.db.entities.SafeWifi;

@Database(entities = {SafeLocation.class, SafeWifi.class, LockedApp.class}, version = 7)
public abstract class LockationDatabase extends RoomDatabase
{
    public abstract SafeLocationDao safeLocationDao();
    public abstract SafeWifiDao safeWifiDao();
    public abstract LockedAppDao lockedAppDao();
}
