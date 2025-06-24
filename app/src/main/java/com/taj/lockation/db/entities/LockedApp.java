package com.taj.lockation.db.entities;

import androidx.annotation.NonNull;
import androidx.compose.ui.graphics.ImageBitmap;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class LockedApp
{
    @PrimaryKey
    @NonNull
    public String id; // package name of the app

    public String name;

    @Ignore
    public ImageBitmap icon;
}
