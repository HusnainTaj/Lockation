package com.taj.lockation.helpers;

import static android.content.Context.WIFI_SERVICE;

import static com.taj.lockation.helpers.LockationKtHelperKt.DrawableToImgBitmap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import androidx.core.graphics.drawable.DrawableKt;

import com.taj.lockation.db.entities.SafeLocation;
import com.taj.lockation.db.entities.SafeWifi;
import com.taj.lockation.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class LockationHelper
{
    public static void showToast(Context ctx, String msg)
    {
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
    }
    public static List<AppInfo> getInstalledApps(PackageManager packageManager)
    {
        List<AppInfo> installedApps = new ArrayList<>();
        // Get a list of all installed packages on the device
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        // Iterate through the list and filter out system apps
        for (PackageInfo packageInfo : packages)
        {
            AppInfo appInfo = new AppInfo(
                    packageInfo.applicationInfo.loadLabel(packageManager).toString(),
                    packageInfo.applicationInfo.packageName,
                    (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0,
                    DrawableToImgBitmap(packageInfo.applicationInfo.loadIcon(packageManager)));

            installedApps.add(appInfo);
        }

        return installedApps;
    }

    public static boolean isLocationOn(Context ctx)
    {
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public static SafeLocation getLastLocation(Context ctx)
    {
        try
        {
            LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            Location l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            SafeLocation safeLoc = new SafeLocation();
            safeLoc.longitude = l.getLongitude();
            safeLoc.latitude = l.getLatitude();

            return safeLoc;
        }
        catch ( SecurityException e)
        {

        }

        return null;
    }

    public static SafeWifi getConnectedWifi(Context ctx)
    {
        WifiManager wm = (WifiManager) ctx.getSystemService(WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();

        SafeWifi sw = new SafeWifi();

        sw.id = info.getBSSID();
        sw.ssid = info.getSSID().replace("\"", "").replace("\"", "");

        return sw;
    }
}
