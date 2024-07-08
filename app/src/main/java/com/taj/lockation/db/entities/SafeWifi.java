package com.taj.lockation.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SafeWifi
{
    @PrimaryKey
    @NonNull
    public String id; // bssid of the wifi
    public String ssid;

}

