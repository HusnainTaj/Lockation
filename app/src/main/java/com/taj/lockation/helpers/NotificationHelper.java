package com.taj.lockation.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.content.ContextCompat;

import com.taj.lockation.R;

import java.util.Objects;

public class NotificationHelper
{
    public static final int ID_DEFAULT = 1001;
    public static final int ID_APPS_LOCKED_STATUS = 2001;
    public static final int ID_LOCATION_STATUS_CHANGED = 2002;

    private NotificationManager nm;
    private Context ctx;
    private String CHANNEL_ID;

    public NotificationHelper(Context ctx, String channelId, String channelName )
    {
        this.ctx = ctx;
        nm = ctx.getSystemService(NotificationManager.class);
        CHANNEL_ID = channelId;

        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
        nm.createNotificationChannel(channel);
    }

    public void showNotification(String title, String text, int notificationId)
    {
        Notification.Builder builder = new Notification.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
//                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_stat_name))
                .setContentTitle(title)
                .setContentText(text);

        nm.notify(notificationId, builder.build());
    }

    public void showNotification(String title, String text)
    {
        showNotification(title, text, ID_DEFAULT);
    }
}
