package com.taj.lockation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.taj.lockation.services.AppLockService

class StartServiceReceiver : BroadcastReceiver()
{
	override fun onReceive(context: Context, intent: Intent)
	{
		ContextCompat.startForegroundService(context, Intent(context, AppLockService::class.java));
	}
}