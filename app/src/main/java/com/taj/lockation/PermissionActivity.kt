package com.taj.lockation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.taj.lockation.helpers.LockationHelper
import com.taj.lockation.helpers.PermissionHelper
import com.taj.lockation.ui.theme.LockationTheme

class PermissionActivity : ComponentActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContent {PermsContent()}
	}

	fun hasPerm(perm:String):Boolean
	{
		return ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;
	}

	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	fun permissionItem(index:Int, title:String, content:String, hasPermission:()->Boolean, requestPermission:String, specialPermission:Boolean = false)
	{
		var permissionGranted by remember { mutableStateOf(hasPermission()) }

		val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())
		{
			permissionGranted = it
			if(it) activeIndex = index + 1
		}

		val specialLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult())
		{
			if((requestPermission == Settings.ACTION_MANAGE_OVERLAY_PERMISSION && Settings.canDrawOverlays(applicationContext)) || (requestPermission == Settings.ACTION_USAGE_ACCESS_SETTINGS && PermissionHelper.hasUsageStatsPermission(applicationContext)))
			{
				permissionGranted = true;
				activeIndex = index + 1
			}
		}

		ListItem(modifier = Modifier
			.clickable { activeIndex = index }
			.padding(0.dp),
			headlineText = {Text(title)},
			tonalElevation = if(activeIndex == index) 1.dp else 0.dp,
			leadingContent = {
				Icon(if (activeIndex == index) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null)
			 },
			trailingContent = {
				if(permissionGranted)
					Icon(Icons.Default.CheckBox, null)
				else
					Icon(Icons.Default.CheckBoxOutlineBlank, null, modifier = Modifier.clickable {
						if(requestPermission == Manifest.permission.ACCESS_BACKGROUND_LOCATION && !PermissionHelper.hasPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION))
						{
							LockationHelper.showToast(applicationContext, "Grant previous Location permission first")
						}
						else
						{
							if(specialPermission)
								specialLauncher.launch(Intent(requestPermission, Uri.parse("package:$packageName")))
							else
								launcher.launch(requestPermission)
						}
					})
			})

		if(activeIndex == index)
		{
			Column(
				modifier = Modifier
					.fillMaxWidth(1f)
					.background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
			) {
				Text(content,
					style = MaterialTheme.typography.bodyMedium, modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp))
			}
		}
		Divider()
	}

	private var activeIndex by mutableStateOf(0)

	@OptIn(ExperimentalMaterial3Api::class)
	@Preview(showBackground = true,showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_MASK)
	@Composable
	fun PermsContent()
	{
		LockationTheme()
		{
			Scaffold {
				Column(
					modifier = Modifier
						.padding(it)
						.fillMaxSize(1f)
						.padding(vertical = 24.dp)
						.verticalScroll(ScrollState(0), true, null, false),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.SpaceBetween
				) {
					Column {
						Column(modifier = Modifier.padding(8.dp),
							horizontalAlignment = Alignment.CenterHorizontally) {
							Text("Permissions", style = MaterialTheme.typography.titleLarge)
							Text("You must allow all of these permissions to use Lockation", textAlign = TextAlign.Center)
							Text("Tap checkbox to grant permission",modifier = Modifier.padding(top = 16.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
						}
						Column {
							Divider();
							permissionItem(0,"Location", getString(R.string.perms_location_rationale), { hasPerm(Manifest.permission.ACCESS_FINE_LOCATION) },Manifest.permission.ACCESS_FINE_LOCATION)
							permissionItem(1,"Background Location", getString(R.string.perms_background_location_rationale), { hasPerm(Manifest.permission.ACCESS_BACKGROUND_LOCATION) },Manifest.permission.ACCESS_BACKGROUND_LOCATION)
							permissionItem(2,"Notifications", getString(R.string.perms_notifications_rationale),  { hasPerm(Manifest.permission.POST_NOTIFICATIONS) }, Manifest.permission.POST_NOTIFICATIONS)
							permissionItem(3,"Usage Stats", getString(R.string.perms_usage_stats_rationale), { PermissionHelper.hasUsageStatsPermission(applicationContext) }, Settings.ACTION_USAGE_ACCESS_SETTINGS, true)
							permissionItem(4,"Draw Over Other Apps", getString(R.string.perms_overlay_rationale), { Settings.canDrawOverlays(applicationContext) }, Settings.ACTION_MANAGE_OVERLAY_PERMISSION, true)
						}
					}
					Column (
						modifier = Modifier,
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.Center
					){
						Text("Lockation does not collect/share your personal information",modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
						Button(
							onClick = {
								if(PermissionHelper.hasAllPermissions(applicationContext))
								{
									finishAffinity();
									startActivity(Intent(applicationContext, MainActivity::class.java))
								}
								else
								{
									LockationHelper.showToast(applicationContext, "All of the permissions must be granted")
								}
							}) {
							Text("Get Started");
						}
					}
				}
			}

		}
	}
}
