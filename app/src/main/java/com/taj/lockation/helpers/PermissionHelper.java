package com.taj.lockation.helpers;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper
{
//    private static ArrayList<String> perms = new ArrayList<String>();
//
//    public static boolean allPermsGranted(Context ctx)
//    {
//        perms.add()
//        ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//    }
    public static boolean hasAllRequiredPermissions(Context ctx)
    {
        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return false;
        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) return false;
        if(!hasUsageStatsPermission(ctx)) return false;
        if(!Settings.canDrawOverlays(ctx)) return false;

        return true;
    }
    public static boolean hasPermission(Context ctx, String permission)
    {
        return (ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED);
    }
    public static boolean hasAllPermissions(Context ctx)
    {
        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return false;
        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return false;
        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) return false;
        if(!hasUsageStatsPermission(ctx)) return false;
        if(!Settings.canDrawOverlays(ctx)) return false;

        return true;
    }
    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.unsafeCheckOp(AppOpsManager.OPSTR_GET_USAGE_STATS, context.getApplicationInfo().uid, context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public static void openUsageStatsSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openDrawOverlaySettings(Context context)
    {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
