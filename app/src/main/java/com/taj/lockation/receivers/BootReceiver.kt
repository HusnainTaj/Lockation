package com.taj.lockation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.taj.lockation.helpers.NotificationHelper
import com.taj.lockation.services.AppLockService

class BootReceiver : BroadcastReceiver()
{
	override fun onReceive(context: Context, intent: Intent)
	{
		context.sendBroadcast(Intent(context, StartServiceReceiver::class.java))
	}
}