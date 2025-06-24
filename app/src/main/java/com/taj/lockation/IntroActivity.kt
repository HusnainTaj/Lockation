package com.taj.lockation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.taj.lockation.helpers.PermissionHelper
import com.taj.lockation.helpers.Prefs
import com.taj.lockation.helpers.set
import com.taj.lockation.ui.theme.LockationTheme
import java.lang.Math.abs

class IntroActivity : ComponentActivity()
{
	lateinit var prefs: SharedPreferences

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		prefs = getSharedPreferences(Prefs.NAME, Context.MODE_PRIVATE);

		setContent { IntroContent() }
	}

	@OptIn(ExperimentalMaterial3Api::class)
	@Preview(showBackground = true,showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_MASK)
	@Composable
	fun IntroContent()
	{
		val pages = mutableListOf<page>();
		pages.add(page("Lockation", getString(R.string.intro_page1_text), {Icon(painter = painterResource(id = R.drawable.lockation_icon_cropped), null, modifier = it)}))
//		pages.add(page("Disclaimer", getString(R.string.intro_page1_5_text),  {Icon(Icons.Default.BugReport, null, modifier = it)}))
		pages.add(page("Note", getString(R.string.intro_page2_text),  {Icon(Icons.Default.Warning, null, modifier = it)}))
		pages.add(page("Permissions", getString(R.string.intro_page3_text),  {Icon(Icons.Default.Lock, null, modifier = it)}))

		var activePage by remember { mutableStateOf(0) }
		var swipeDirection by remember { mutableStateOf(-1)}

		fun incActivePage()
		{
			if(activePage + 1 >= pages.size)
			{
				activePage = pages.size - 1;
				prefs.set(Prefs.INTRO_SHOWN, true);

				var intent = Intent(applicationContext, MainActivity::class.java)

				if(!PermissionHelper.hasAllPermissions(applicationContext))
					intent = Intent(applicationContext, PermissionActivity::class.java)

				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
				startActivity(intent)
			}
			else ++activePage
		}
		fun decActivePage()
		{
			if(activePage - 1 < 0) activePage = 0;
			else --activePage
		}

		LockationTheme()
		{
			Scaffold() {
				Column(
					modifier = Modifier
						.padding(it)
						.fillMaxSize(1f)
						.pointerInput(Unit)
						{
							detectDragGestures(onDragEnd = {
								when (swipeDirection){
									0 -> {
										decActivePage()
									}
									1 -> {
										incActivePage()
									}
								}
							}){ change, dragAmount ->
								change.consume()

								val (x, y) = dragAmount
								if(abs(x) > abs(y)){
									when {
										x > 0 -> {
											//right
											swipeDirection = 0
										}
										x < 0 -> {
											// left
											swipeDirection = 1
										}
									}
								}else{
									when {
										y > 0 -> {
											// down
											swipeDirection = 2
										}
										y < 0 -> {
											// up
											swipeDirection = 3
										}
									}
								}
							}
						}
						.padding(32.dp),
					verticalArrangement = Arrangement.SpaceBetween,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Row() {}
					Column(
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						pages[activePage].icon(Modifier.size(120.dp))
						Text(pages[activePage].title, style = MaterialTheme.typography.headlineLarge)
						Text(pages[activePage].content, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
					}
					Row() {
						IconButton(onClick = { decActivePage() }) { Icon(Icons.Default.KeyboardArrowLeft, null) }
						for (i in 0 until pages.size) RadioButton(selected = i == activePage, onClick = { }, enabled = false)
						IconButton(onClick = { incActivePage() }) { Icon(Icons.Default.KeyboardArrowRight, null) }
					}
				}
			}
		}
	}

	class page(var title:String, var content:String, var icon:@Composable() (Modifier) -> Unit);
}


