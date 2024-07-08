package com.taj.lockation.services;

import android.app.Notification;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.compose.material.icons.sharp.GpsNotFixedKt;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import com.taj.lockation.LockedActivity;
import com.taj.lockation.R;
import com.taj.lockation.db.LockationDatabase;
import com.taj.lockation.db.entities.SafeLocation;
import com.taj.lockation.db.entities.SafeWifi;
import com.taj.lockation.helpers.LockationHelper;
import com.taj.lockation.helpers.NotificationHelper;
import com.taj.lockation.helpers.Prefs;
import com.taj.lockation.receivers.StartServiceReceiver;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppLockService extends Service
{
    private static final String CHANNEL_ID = "AppLockService";
    private static final int LOCKER_SERVICE_INTERVAL = 500;
    private static final int PREMIUM_SERVICE_INTERVAL = 60 * 60 * 1000;

    private UsageStatsManager usm;
    private SharedPreferences prefs;
    private LockationDatabase lockationDatabase;
    private NotificationHelper notificationHelper;
    Set<String> activeApps = new HashSet<>();
    private Boolean inSafeLocation = false;
    private Boolean isSubscribed = false;
    @Override
    public void onCreate()
    {
        super.onCreate();

        lockationDatabase = Room.databaseBuilder(this, LockationDatabase.class, "lockation-db").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        prefs = getSharedPreferences(Prefs.NAME, Context.MODE_PRIVATE);
        usm = (UsageStatsManager) this.getSystemService(USAGE_STATS_SERVICE);
        notificationHelper = new NotificationHelper(this, CHANNEL_ID, "App Lock Service");

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(activeAppReceiver, new IntentFilter("lockation.intents.appActivated"));
        lbm.registerReceiver(forceUpdateReceiver, new IntentFilter("lockation.intents.forceUpdate"));

        registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        startForeground(NotificationHelper.ID_DEFAULT, new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("Lockation")
                        .setContentText("Lockation is running in the background.")
                        .setSmallIcon(R.drawable.ic_stat_name).build());

        startLocationListener();

        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(lockerService);
        mHandler.post(premiumService);
    }

    private void startLocationListener()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(@NonNull Location location)
            {
                List<SafeLocation> sls = lockationDatabase.safeLocationDao().getAll();

                // Workaround for a logical error
                // to reproduce
                // add safe location, and wait for apps to unlock
                // then delete the safe location
                // now app will remain unlocked because inSafeLocation is true
                // This solution has a delay
                if(sls.isEmpty()) inSafeLocation = false;

                if(!isSubscribed && sls.size() > 1) sls = sls.subList(0, 1);

                for (SafeLocation sl: sls)
                {
                    float[] results = new float[10];
                    Location.distanceBetween(sl.latitude, sl.longitude, location.getLatitude(), location.getLongitude(), results);

                    if (results[0] > sl.radius)
                    {
                        inSafeLocation = false;
                    }
                    else
                    {
                        inSafeLocation = true;
                        break;
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
                // Handle location provider status changes if needed
                // notificationHelper.showNotification("Location status", "p: " + provider + " , s: " + status, 34523);
            }

            @Override
            public void onProviderEnabled(String provider)
            {
                // Handle location provider enabled if needed
                // notificationHelper.showNotification("Location Enabled", "Lockation is working.", NotificationHelper.NOTIFICATION_ID_LOCATION_STATUS_CHANGED);
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                // Handle location provider disabled if needed
                // notificationHelper.showNotification("Location Disabled", "Turn it back on for Lockation to work.", NotificationHelper.ID_LOCATION_STATUS_CHANGED);
                inSafeLocation = false;
            }
        };

        try
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        catch (SecurityException e)
        {
            notificationHelper.showNotification("Location Error", "There was an issue getting location data.");
        }
    }

    private Handler mHandler;

    private void showLockScreen(String PackageName)
    {
        Intent activityIntent = new Intent(this, LockedActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        activityIntent.putExtra("package", PackageName);
        startActivity(activityIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activeAppReceiver);
        unregisterReceiver(screenOffReceiver);

        sendBroadcast(new Intent(this, StartServiceReceiver.class));

        super.onDestroy();
    }

    private final Runnable lockerService = new Runnable()
    {
        @Override
        public void run()
        {
            List<String> lockedApps = lockationDatabase.lockedAppDao().getAllPackageNames();
            List<SafeWifi> safeWifis = lockationDatabase.safeWifiDao().getAll();

            if(!isSubscribed && safeWifis.size() > 1) safeWifis = safeWifis.subList(0, 1);

            UsageEvents usageEvents = usm.queryEvents(System.currentTimeMillis() - LOCKER_SERVICE_INTERVAL, System.currentTimeMillis());
            UsageEvents.Event e = new UsageEvents.Event();

            while (usageEvents.hasNextEvent())
            {
                usageEvents.getNextEvent(e);
                Log.e("USM", e.getPackageName());
                if (e.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED)
                {
                    if(e.getClassName().equals("com.taj.lockation.LockedActivity")) continue;
                    Log.e("USM", e.getPackageName() + " Resumed.");
                    if(!inSafeLocation)
                    {
                        SafeWifi currentWifi = LockationHelper.getConnectedWifi(getApplicationContext());
                        if(safeWifis.stream().noneMatch(sw -> sw.id.equals(currentWifi.id)))
                        {
                            if (lockedApps.contains(e.getPackageName()) && !activeApps.contains(e.getPackageName()))
                            {
                                showLockScreen(e.getPackageName());
                            }
                        }
                    }
                }
            }
            // Repeat this runnable code block again every ... min
            mHandler.postDelayed(lockerService, LOCKER_SERVICE_INTERVAL);
        }
    };


    private final Runnable premiumService = new Runnable()
    {
        @Override
        public void run()
        {
            long expiryTime = prefs.getLong(Prefs.SUBSCRIPTION_EXPIRY, -1L);

            boolean newIsSubscribed = new Date().getTime() <= expiryTime;

            if(newIsSubscribed != isSubscribed)
            {
                isSubscribed = newIsSubscribed;
                if(!isSubscribed) prefs.edit().putLong(Prefs.SUBSCRIPTION_EXPIRY, -1L).apply();
            }

            // Repeat this runnable code block again every ... min
            mHandler.postDelayed(premiumService, PREMIUM_SERVICE_INTERVAL);
        }
    };


    // Register a BroadcastReceiver to receive results from the service (optional)
    private final BroadcastReceiver activeAppReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String result = intent.getStringExtra("package");
            activeApps.add(result);
        }
    };
    private final BroadcastReceiver forceUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String result = intent.getStringExtra("package");
            activeApps.add(result);
        }
    };
    private final BroadcastReceiver screenOffReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            activeApps.clear();
        }
    };

}