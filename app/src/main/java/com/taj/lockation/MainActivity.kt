package com.taj.lockation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.taj.lockation.db.LockationDatabase
import com.taj.lockation.db.entities.LockedApp
import com.taj.lockation.db.entities.SafeLocation
import com.taj.lockation.db.entities.SafeWifi
import com.taj.lockation.helpers.LockationHelper
import com.taj.lockation.helpers.NotificationHelper
import com.taj.lockation.helpers.PermissionHelper
import com.taj.lockation.helpers.Prefs
import com.taj.lockation.helpers.set
import com.taj.lockation.models.AppInfo
import com.taj.lockation.receivers.StartServiceReceiver
import com.taj.lockation.ui.theme.LockationTheme
import com.taj.lockation.ui.theme.VSpacer
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity()
{
	private val WEBSITE_URL = "https://lockation.github.io"
	private val PRIVACY_POLICY_URL = "https://lockation.github.io/privacy.html"
	private val DEV_EMAIL = "husnain.taj@gmail.com";

	private val CHANNEL_ID = "LockationUI"

	private lateinit var installedApps: List<AppInfo>;
	private lateinit var lockationDb: LockationDatabase
	private lateinit var notificationHelper: NotificationHelper
	private lateinit var prefs: SharedPreferences

	private val acknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener { isSubscribed() }
	private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
		if (purchases != null)
		{
			for (purchase in purchases)
			{
				if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged)
				{
					val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
						.setPurchaseToken(purchase.purchaseToken)
						.build()
					billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener)
				}
			}
		}
	}
	private lateinit var billingClient:BillingClient

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState);

		prefs = getSharedPreferences(Prefs.NAME, Context.MODE_PRIVATE);

		if (!prefs.getBoolean(Prefs.INTRO_SHOWN, false))
		{
			finishAffinity()
			startActivity(Intent(this, IntroActivity::class.java))
		}
		else
		{
			if(verifyPermissions())
			{
				sendBroadcast(Intent(this, StartServiceReceiver::class.java))

				installedApps = LockationHelper.getInstalledApps(packageManager).sortedBy { it.name }
				lockationDb =
					Room.databaseBuilder(applicationContext, LockationDatabase::class.java, "lockation-db")
						.allowMainThreadQueries().fallbackToDestructiveMigration().build();
				notificationHelper = NotificationHelper(this, CHANNEL_ID, "Lockation UI")

				setContent { MainContent() }
				reportFullyDrawn()
			}
		}
	}

	fun verifyPermissions():Boolean
	{
		if(!PermissionHelper.hasAllPermissions(applicationContext))
		{
			val intent = Intent(this, PermissionActivity::class.java)
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//			finishAffinity();
			startActivity(intent)
			return false;
		}

		return true;
	}

	fun purchaseSubscription(subscription:ProductDetails, plan: ProductDetails.SubscriptionOfferDetails):BillingResult
	{
		val productDetailsParamsList = listOf(
			BillingFlowParams.ProductDetailsParams.newBuilder()
			.setProductDetails(subscription)
			.setOfferToken(plan.offerToken)
			.build()
		)

		val billingFlowParams = BillingFlowParams.newBuilder()
			.setProductDetailsParamsList(productDetailsParamsList)
			.build()

		return billingClient.launchBillingFlow(this@MainActivity, billingFlowParams)
	}

	private var isSubscribed by mutableStateOf(false);
	private var showPremiumAd by mutableStateOf(false)

	fun isSubscribed()
	{
		if(billingClient.isReady)
		{
			billingClient.queryPurchasesAsync(
				QueryPurchasesParams.newBuilder()
					.setProductType(ProductType.SUBS)
					.build()
			)
			{ nbillingResult, purchases ->
				if(nbillingResult.responseCode == BillingClient.BillingResponseCode.OK)
				{
					try
					{
						val purchase = purchases.first { it.isAcknowledged };
						isSubscribed = true;
						showPremiumAd = false;

//						prefs.set(Prefs.SUBSCRIPTION_EXPIRY, purchase.purchaseTime + (5 * 60 * 1000L)) // for testing
						prefs.set(Prefs.SUBSCRIPTION_EXPIRY, purchase.purchaseTime + (31 * 24 * 60 * 60 * 1000L))
					}
					catch (e:NoSuchElementException)
					{
						isSubscribed = false;

						prefs.set(Prefs.SUBSCRIPTION_EXPIRY, -1L)
					}
				}
			}
		}
	}
	override fun onStart()
	{
		super.onStart()

		// billing
		billingClient = BillingClient.newBuilder(this)
			.setListener(purchasesUpdatedListener)
			.enablePendingPurchases()
			.build()


		billingClient.startConnection(object : BillingClientStateListener
		{
			override fun onBillingSetupFinished(billingResult: BillingResult) {
				if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK)
				{
					isSubscribed()
				}
				else
				{
					runOnUiThread { LockationHelper.showToast(applicationContext, "Subscriptions not supported by your device") }
				}
			}
			override fun onBillingServiceDisconnected() {

//					runOnUiThread { LockationHelper.showToast(applicationContext, "idk"); }

				// Try to restart the connection on the next request to
				// Google Play by calling the startConnection() method.
			}
		})
	}

	override fun onResume()
	{
		super.onResume()
		verifyPermissions()
	}

	override fun onDestroy()
	{
		if(::billingClient.isInitialized) billingClient.endConnection()
		super.onDestroy()
	}

	override fun onBackPressed()
	{
		if(showPremiumAd) showPremiumAd = false
		else super.onBackPressed()
	}

	@Composable
	fun NavDrawerBtn(label:String, icon:ImageVector, onClick: ()->Unit)
	{
		TextButton(onClick = { onClick() }, modifier = Modifier
			.fillMaxWidth()
			.height(56.dp),
			shape = RectangleShape
		) {
			Icon(icon, null, modifier = Modifier
				.padding(start = 12.dp)
				.size(24.dp))
			Spacer(modifier = Modifier.width(12.dp))
			Text(label,modifier = Modifier.fillMaxWidth())
		}
		Divider()

	}
	@OptIn(ExperimentalMaterial3Api::class)
	@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_MASK)
	@Composable
	fun PremiumAd()
	{
		Scaffold {
			var yearlySub by remember { mutableStateOf(true) }
			Column(
				modifier = Modifier
					.padding(it)
					.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Icon(
					painter = painterResource(id = R.drawable.lockation_icon_cropped),
					null,
					modifier = Modifier.size(120.dp)
				)
				Text("Premium", style = MaterialTheme.typography.headlineLarge)
				VSpacer(32.dp);
				Row(
					verticalAlignment = Alignment.CenterVertically,
				) {
					Text("Monthly", style = MaterialTheme.typography.bodyMedium,modifier = Modifier.width(60.dp), textAlign = TextAlign.End)
					Switch(checked = yearlySub, onCheckedChange = { yearlySub = it }, modifier = Modifier.padding(horizontal = 16.dp))
					Text("Yearly ", style = MaterialTheme.typography.bodyMedium,modifier = Modifier.width(60.dp))
				}
				VSpacer(32.dp);
				Text(if(yearlySub) "save $4, two months free!" else "", style = MaterialTheme.typography.labelSmall)

				Text(if(yearlySub) "$20" else "$2", style = MaterialTheme.typography.displayMedium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))


				Text("auto renews, cancel anytime", style = MaterialTheme.typography.labelSmall)
				VSpacer(24.dp);
				Text("No ads", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
				Text("Unlimited Safe Locations", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
   				Text("Unlimited Safe WiFis", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
				Text("Faster Unlocks", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
				VSpacer(24.dp);

				Button(onClick = {
					val productList = ArrayList<QueryProductDetailsParams.Product>()
					productList.add(QueryProductDetailsParams.Product.newBuilder()
						.setProductId("premium")
						.setProductType(BillingClient.ProductType.SUBS)
						.build())

					val queryProductDetailsParams =
						QueryProductDetailsParams.newBuilder()
							.setProductList(productList)
							.build()

					billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
							nbillingResult,
							productDetailsList ->
						// check billingResult
						if (nbillingResult.responseCode ==  BillingClient.BillingResponseCode.OK)
						{
							try
							{
								val premiumSub = productDetailsList.first { it.productId == "premium" }
								val offer = premiumSub!!.subscriptionOfferDetails?.find { it.basePlanId == if(yearlySub) "yearly" else "monthly" }
								if(offer != null) purchaseSubscription(premiumSub, offer)
							}
							catch (e:NoSuchElementException)
							{
								LockationHelper.showToast(applicationContext, "Something went wrong. Try later.")
							}
						}
					}
				}) { Text("Subscribe") }
				TextButton(onClick = { showPremiumAd = false }) {
					Text("Cancel")
				}
			}
		}
	}

	@SuppressLint("UnrememberedMutableState")
	@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
	@Composable
	fun MainContent()
	{
		var showLocationAlert by remember { mutableStateOf(false) }
		var showHelpAlert by remember { mutableStateOf(false) }

		var activeScreen by remember { mutableStateOf("Apps") }

		val safeLocations = mutableStateListOf<SafeLocation>();
		safeLocations.addAll(lockationDb.safeLocationDao().all);

		val safeWifis = mutableStateListOf<SafeWifi>();
		safeWifis.addAll(lockationDb.safeWifiDao().all);

//		val coroutineScope = rememberCoroutineScope()
//
//		LaunchedEffect(key1 = true, block = {
//			coroutineScope.launch {
//				delay(5000)
//				isSubscribed = true;
//			}
//		})

		LockationTheme() {
			val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
			val scope = rememberCoroutineScope()

			Box {
			ModalNavigationDrawer(
				drawerState = drawerState,
				drawerContent = {
					ModalDrawerSheet(modifier = Modifier)
					{
						BackHandler(drawerState.isOpen) {
							scope.launch {
								drawerState.apply {
									close()
								}
							}
						}
						Column(
							modifier = Modifier
								.fillMaxWidth()
								.padding(vertical = 32.dp),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Icon(if(isSubscribed) Icons.Default.PersonAddAlt1 else Icons.Default.Person,null, modifier = Modifier.size(75.dp))

							Text(if(isSubscribed) "Premium User" else "Free User")
							Text("v"+packageManager.getPackageInfo(packageName, 0).versionName)
						}
						Divider()

						if(isSubscribed)
						{
							NavDrawerBtn(label = "Manage Subscription", icon = Icons.Default.Star) {
								startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions?sku=premium&package=${applicationContext.packageName}")))
							}
						}
						else
						{
							NavDrawerBtn(label = "Get Premium", icon = Icons.Default.Star) { showPremiumAd = true }
						}

						NavDrawerBtn(label = "Manage Notifications", icon = Icons.Default.Notifications) {
							startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
								.putExtra(Settings.EXTRA_APP_PACKAGE, packageName))
						}

						NavDrawerBtn(label = "Privacy Policy", icon = Icons.Default.Policy)
						{
							startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL)))
						}

						NavDrawerBtn(label = "Write a Review", icon = Icons.Default.RateReview) {
							startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
						}

						NavDrawerBtn(label = "Report an Issue", icon = Icons.Default.BugReport)
						{
							startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$DEV_EMAIL?subject=${Uri.encode(getString(R.string.email_bug_report_subject))}&body=${Uri.encode(getString(R.string.email_bug_report_body))}")))
						}

						NavDrawerBtn(label = "Suggest Improvements", icon = Icons.Default.AddComment)
						{
							startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$DEV_EMAIL?subject=${Uri.encode(getString(R.string.email_improvement_subject))}&body=${Uri.encode(getString(R.string.email_improvement_body))}")))
						}

						val currentTheme = prefs.getInt(Prefs.THEME, 0);
						NavDrawerBtn(label = "Theme: ${when (currentTheme) { 1 -> "Light"; 2-> "Dark"; else -> "Auto"}}", icon = when (currentTheme) { 1 -> Icons.Default.LightMode; 2-> Icons.Default.DarkMode; else -> Icons.Default.BrightnessAuto})
						{
							when(currentTheme)
							{
								0 -> prefs.set(Prefs.THEME, 1)
								1 -> prefs.set(Prefs.THEME, 2)
								2 -> prefs.set(Prefs.THEME, 0)
							}

							startActivity(Intent(applicationContext, MainActivity::class.java))
							finishAffinity()
						}

						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.Center
						) {
							IconButton(onClick = { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE_URL))) }) {
								Icon(Icons.Default.Public, null)
							}
							IconButton(onClick = { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$DEV_EMAIL"))) }) {
								Icon(Icons.Default.Email, null)
							}
						}
					}
				},
			) {
				Scaffold(
					topBar = {
						if(activeScreen != "Apps")
						{
							CenterAlignedTopAppBar(
								title = {
									Text(if(activeScreen == "Location") "Safe Locations" else "Safe WiFis");
								},
								navigationIcon = {
									IconButton(onClick = {
										scope.launch {
											drawerState.apply {
												if (isClosed) open() else close()
											}
										}
									})
									{
										Icon(Icons.Default.Menu, null)
									}
								},
								actions = {
									IconButton(onClick = { showHelpAlert = true }) {
										Icon(
											imageVector = Icons.Default.QuestionMark,
											contentDescription = "Localized description"
										)
									}
								},
								colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
								modifier = Modifier
							)
						}
					},
					bottomBar = {
						NavBar(activeScreen, onScreenChange = { activeScreen = it });
					},
					floatingActionButton = {

						AnimatedVisibility(
							visible = activeScreen != "Apps",
							enter = scaleIn(),
							exit = scaleOut()
						) {
							FloatingActionButton(onClick = {
								if(LockationHelper.isLocationOn(applicationContext))
								{
									if(activeScreen == "Location")
									{
										if(!isSubscribed && lockationDb.safeLocationDao().count >= 1)
										{
											Toast.makeText(applicationContext, "Free Limit Reached", Toast.LENGTH_SHORT).show();
											showPremiumAd = true;
										}
										else showLocationAlert = true;
									}
									else if(activeScreen == "WiFi")
									{
										if(!isSubscribed && lockationDb.safeWifiDao().count >= 1)
										{
											Toast.makeText(applicationContext, "Free Limit Reached", Toast.LENGTH_SHORT).show();
											showPremiumAd = true;
										}
										else
										{
											val sw = LockationHelper.getConnectedWifi(applicationContext);
											if(sw != null)
											{
												if(safeWifis.any{it.id == sw.id})
												{
													Toast.makeText(applicationContext, sw.ssid + " already exists", Toast.LENGTH_SHORT).show();
												}
												else
												{
													lockationDb.safeWifiDao().insertAll(sw);
													safeWifis.add(sw)
												}
											}
										}

									}
								}
								else
								{
									Toast.makeText(applicationContext, "Failed: Location Inaccessible.", Toast.LENGTH_SHORT).show()
								}
							}) {
								Icon(Icons.Default.Add, null)
							}
						}
					}
				)
				{
					when (activeScreen)
					{
						"Apps" -> HomeScreen(modifier = Modifier.padding(it)) {
							scope.launch {
								drawerState.apply {
									if (isClosed) open() else close()
								}
							}
						}

						"Location" -> LocationScreen(modifier = Modifier.padding(it), safeLocations) {itemId ->
							lockationDb.safeLocationDao().deleteById(itemId);
							safeLocations.removeIf{it.id == itemId}
						}
						"WiFi" -> WifiScreen(modifier = Modifier.padding(it), safeWifis){itemId ->
							lockationDb.safeWifiDao().deleteById(itemId);
							safeWifis.removeIf{it.id == itemId}
						}
					}

					if(showLocationAlert)
					{
						var alertTextInput by remember { mutableStateOf("") }
						var alertRadiusInput by remember { mutableStateOf(20F) }

						AlertDialog(
							text = {
								Column() {
									OutlinedTextField(
										label = {Text("Name")},
										value = alertTextInput, onValueChange = { alertTextInput = it;}
									)
									Row(
										modifier = Modifier.padding(top = 16.dp),
										verticalAlignment = Alignment.CenterVertically) {
										Text(alertRadiusInput.toInt().toString() + "m")
										Slider(
											modifier = Modifier.padding(start = 16.dp),
											steps = 4,
											valueRange = 20f..120f,
											value = alertRadiusInput, onValueChange = {alertRadiusInput = it},
										)
									}
								}
							},
							icon = {Text("New Safe Location", style = MaterialTheme.typography.titleMedium)},
							confirmButton = {
								Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
								{
									Button(onClick = {
										if(alertTextInput.length > 0)
										{
											val sl = LockationHelper.getLastLocation(applicationContext);
											if(sl != null)
											{
												sl.radius = alertRadiusInput.toInt();
												sl.title = alertTextInput;

												lockationDb.safeLocationDao().insertAll(sl);
												sl.id = lockationDb.safeLocationDao().lastId;
												safeLocations.add(sl)
											}
											showLocationAlert = false;
										}
									}) {
										Text("Create")
									}
								}
							},
							onDismissRequest = { showLocationAlert = false; }
						)
					}

					if(showHelpAlert)
					{
						AlertDialog(
							text = { Text(if(activeScreen == "Location") getString(R.string.location_help_text) else getString(R.string.wifi_help_text), textAlign = TextAlign.Center) },
							icon = { Text(if(activeScreen == "Location") "Safe Locations" else "Safe WiFis", style = MaterialTheme.typography.titleMedium) },
							confirmButton = {
								Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
								{
									Button(onClick = { showHelpAlert = false; }) { Text("Dismiss") }
								}
							},
							onDismissRequest = { showHelpAlert = false; }
						)
					}
				}
			}

				AnimatedVisibility(
					visible = showPremiumAd,
					enter = slideInVertically { it },
					exit = slideOutVertically { it },
				)
				{
					PremiumAd()
				}

			}
		}
	}

	@SuppressLint("UnrememberedMutableState")
	@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
		ExperimentalAnimationApi::class
	)
	@Composable
	fun HomeScreen(modifier: Modifier, drawerMenuClicked:()->Unit)
	{
		val focusManager = LocalFocusManager.current

		var searchText by remember { mutableStateOf("")};
		var showSystemApps by remember { mutableStateOf(false)}
		var allApps by remember { mutableStateOf(installedApps)};

		val lockedApps = mutableStateListOf<String>()

		allApps = installedApps.filter {  ita ->
			ita.name.lowercase(Locale.getDefault()).contains(searchText.lowercase(Locale.getDefault())) && (showSystemApps || (!showSystemApps && !ita.isSystem))
		}
		lockedApps.addAll(lockationDb.lockedAppDao().allPackageNames)

		LazyColumn(
			modifier = modifier
				.fillMaxWidth()
		)
		{
			stickyHeader {

				OutlinedTextField(value = searchText, onValueChange = { it ->
					searchText = it;
					allApps = installedApps.filter {  ita ->
						ita.name.lowercase(Locale.getDefault()).contains(searchText.lowercase(Locale.getDefault())) && ita.isSystem == showSystemApps
					}
				},
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
					keyboardActions = KeyboardActions(onDone = {
						focusManager.clearFocus(true)
					}),
					label = { Text(text = "App Name")},
					trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
					leadingIcon = {
						IconButton(onClick = { drawerMenuClicked() }) {
							Icon(Icons.Default.Menu, contentDescription = null)
						}
					},
					modifier = Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colorScheme.background)
						.padding(16.dp, 0.dp, 16.dp, 8.dp)

				)
			}
			items(allApps.size, key = {allApps[it].packageName})
			{
				ListItem({
					Text(text = allApps[it].name)
				},
				leadingContent = {
					Image(bitmap = allApps[it].icon,
						contentDescription = null,
						modifier = Modifier.size(50.dp)
					)
				},
				trailingContent = {
					AnimatedVisibility(
						visible = lockedApps.contains(allApps[it].packageName),
						enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)),
						exit = scaleOut() + fadeOut()
					) {
						Icon(Icons.Default.Lock, contentDescription = null)
					}

				},
				modifier = Modifier
					.animateItemPlacement()
					.clickable {
						focusManager.clearFocus(true)

						if (lockedApps.contains(allApps[it].packageName))
						{
							lockationDb
								.lockedAppDao()
								.deleteById(allApps[it].packageName);
							lockedApps.remove(allApps[it].packageName);
						} else
						{
							val newLockedApp = LockedApp()
							newLockedApp.id = allApps[it].packageName;
							newLockedApp.name = allApps[it].name;

							lockationDb
								.lockedAppDao()
								.insertAll(newLockedApp);

							lockedApps.add(allApps[it].packageName);
						}

					})
			}

			item {
				Row(
					modifier= Modifier
						.fillMaxWidth()
						.padding(vertical = 16.dp),
					horizontalArrangement = Arrangement.Center
				)
				{
					OutlinedButton(onClick = {
						focusManager.clearFocus(true)
						showSystemApps = !showSystemApps
					}) {
						Text(text = if(showSystemApps) "Hide System Apps" else "Show All Apps")
					}
				}
			}

		}

	}

	@SuppressLint("UnrememberedMutableState")
	@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
	@Composable
	fun LocationScreen(modifier: Modifier, safeLocations: MutableList<SafeLocation>, onRemoveSafeLocation: (Int) -> Unit)
	{
		LazyColumn(
			modifier = modifier.fillMaxWidth()
		)
		{
			items(safeLocations.size)
			{
				if(!isSubscribed && it == 1)
				{
					ListItem({
						Text("Disabled", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
					},
						modifier = Modifier.clickable{ showPremiumAd = true },
						tonalElevation = 1.dp,
						supportingText = {
							Text("Get Premium to use unlimited Locations", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
						},
						trailingContent = {

						})
					Divider()
				}

				ListItem({
					Text(text = safeLocations[it].title)
				},
				modifier = Modifier.clickable(!isSubscribed && it > 0) { showPremiumAd = true },
				supportingText = {
					Text(
						text = safeLocations[it].radius.toString() + "m in " + safeLocations[it].longitude + "\u00B0, " + safeLocations[it].latitude + "\u00B0",
						fontSize = MaterialTheme.typography.bodySmall.fontSize,
//							color= MaterialTheme.colorScheme.secondary
					)
				},
				trailingContent = {
					val itemId = safeLocations[it].id;
					IconButton(
						onClick = {
							if(isSubscribed || (!isSubscribed && it == 0))
								onRemoveSafeLocation(itemId);
							else
								showPremiumAd = true
						},
					) {
						Icon(if(isSubscribed || (!isSubscribed && it == 0)) Icons.Default.Delete else Icons.Default.Lock, null)
					}
				})
				Divider()
			}
		}

		if(safeLocations.size == 0)
		{
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(horizontal = 32.dp),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text("Press + in the bottom right corner to add a new Safe Location", textAlign = TextAlign.Center)
			}
		}
	}

	@SuppressLint("UnrememberedMutableState")
	@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
	@Composable
	fun WifiScreen(modifier: Modifier, safeWifis: MutableList<SafeWifi>, onRemoveSafeWifi: (String) -> Unit)
	{
		LazyColumn(
			modifier = modifier.fillMaxWidth()
		)
		{
			items(safeWifis.size)
			{
				if(!isSubscribed && it == 1)
				{
					ListItem({
						Text("Disabled", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
					},
						modifier = Modifier.clickable{ showPremiumAd = true },
						tonalElevation = 1.dp,
						supportingText = {
							Text("Get Premium to use unlimited WiFis", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
						},
						trailingContent = {

						})
					Divider()
				}

				ListItem({
					Text(text = safeWifis[it].ssid)
				},
					modifier = Modifier.clickable(!isSubscribed && it > 0) { showPremiumAd = true },
					supportingText = {
						Text(
							text = safeWifis[it].id,
							fontSize = MaterialTheme.typography.bodySmall.fontSize,
						)
					},
					trailingContent = {
						val itemId = safeWifis[it].id;
						IconButton(
							onClick = {
								if(isSubscribed || (!isSubscribed && it == 0))
									onRemoveSafeWifi(itemId);
								else
									showPremiumAd = true
							},
						) {
							Icon(if(isSubscribed || (!isSubscribed && it == 0)) Icons.Default.Delete else Icons.Default.Lock, null)
						}
					})
				Divider()
			}
		}

		if(safeWifis.size == 0)
		{
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(horizontal = 32.dp),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text("Press + in the bottom right corner to add a new Safe WiFi", textAlign = TextAlign.Center)
			}
		}
	}

	@Composable
	fun NavBar(activeScreen:String, onScreenChange: (String) -> Unit)
	{
		NavigationBar(modifier = Modifier
			.fillMaxWidth()
		) {
			NavigationBarItem(
				selected = activeScreen == "Apps",
				onClick = {
					onScreenChange("Apps")
				},
				icon = { Icon(Icons.Default.Apps, contentDescription = null) },
				label =  {
					Text(text = "Apps")
				}
			)
			NavigationBarItem(
				selected = activeScreen == "Location",
				onClick = {
					onScreenChange("Location")
				},
				icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
				label =  {
					Text(text = "Location")
				}
			)
			NavigationBarItem(
				selected = activeScreen == "WiFi",
				onClick = {
					onScreenChange("WiFi")
				},
				icon = {
					Icon(Icons.Default.Wifi, contentDescription = null)
				},
				label =  {
					Text(text = "WiFi")
				}
			)
			if(!isSubscribed)
			{
				NavigationBarItem(
					selected = false,
					onClick = {
						showPremiumAd = true
					},
					icon = {
						Icon(Icons.Default.Star, contentDescription = null)
					},
					label =  {
						Text(text = "Premium")
					}
				)
			}
		}
	}
}

