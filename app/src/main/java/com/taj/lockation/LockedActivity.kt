package com.taj.lockation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.CancellationSignal
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.taj.lockation.helpers.LockationHelper
import com.taj.lockation.helpers.Prefs
import com.taj.lockation.ui.theme.LockationTheme
import java.util.Date
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class LockedActivity : ComponentActivity()
{
	private lateinit var packageName: String
	private lateinit var lbm: LocalBroadcastManager
	private lateinit var biometricManager: BiometricManager

	private var isSubscribed = false;

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		isSubscribed = Date().time <= getSharedPreferences(Prefs.NAME, Context.MODE_PRIVATE).getLong(Prefs.SUBSCRIPTION_EXPIRY, -1L)

		packageName = intent.getStringExtra("package")!!

		setContent { LockedScreen() }

		lbm = LocalBroadcastManager.getInstance(this);

		biometricManager = getSystemService(BIOMETRIC_SERVICE) as BiometricManager

		if(!hasLockscreen())
		{
			LockationHelper.showToast(this, "Set up a Lockscreen password to protect your apps.")
		}

		if(isSubscribed && hasLockscreen()) startAuth()

	}

	private fun authenticate()
	{
		val resultIntent = Intent("lockation.intents.appActivated")
		resultIntent.putExtra("package", packageName)
		lbm.sendBroadcast(resultIntent)
		finish()
	}

	private fun hasLockscreen(): Boolean
	{
		return biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS;
	}

	private fun hasFingerprint(): Boolean
	{
		return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
	}

	private fun startAuth()
	{
		if(!hasLockscreen())
		{
			authenticate();
			return;
		}

		val executor: Executor = Executors.newSingleThreadExecutor()
		val biometricPrompt = BiometricPrompt.Builder(this)
			.setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL or if (hasFingerprint()) BiometricManager.Authenticators.BIOMETRIC_WEAK else 0)
			.setTitle("App Locked")
			.setDescription("Scan your Fingerprint or use a PIN")
			.build()

		// Start the fingerprint authentication
		biometricPrompt.authenticate(
			CancellationSignal(),
			executor,
			object : BiometricPrompt.AuthenticationCallback()
			{
				override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult)
				{
					super.onAuthenticationSucceeded(result)
					authenticate()
				}

				override fun onAuthenticationFailed()
				{
					super.onAuthenticationFailed()
					// Authentication failed
				}
			})
	}

	@OptIn(ExperimentalMaterial3Api::class)
	@Preview(showBackground = true,showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_MASK)
	@Composable
	fun LockedScreen()
	{
		LockationTheme()
		{
			Scaffold() {
				Column(
					modifier = Modifier
						.clickable { startAuth() }
						.padding(it)
						.fillMaxSize(1f)
						.padding(32.dp),
				verticalArrangement = Arrangement.SpaceAround,
					horizontalAlignment = Alignment.CenterHorizontally
				) {

					Column (
						horizontalAlignment = Alignment.CenterHorizontally
					){
						Image(packageManager.getApplicationIcon(packageName).toBitmap().asImageBitmap(), contentDescription = "$packageName's icon")
						Spacer(modifier = Modifier.height(16.dp))
						Text("${packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager)}", style = MaterialTheme.typography.headlineSmall);
					}

					Icon(Icons.Default.Fingerprint,null, modifier = Modifier.size(75.dp))
					Text("Tap to Unlock", style = MaterialTheme.typography.headlineSmall)
				}
			}
		}
	}
}
