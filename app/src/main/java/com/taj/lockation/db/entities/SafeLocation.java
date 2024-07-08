package com.taj.lockation.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SafeLocation
{
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public double longitude;
    public double latitude;
    public int radius;
}
