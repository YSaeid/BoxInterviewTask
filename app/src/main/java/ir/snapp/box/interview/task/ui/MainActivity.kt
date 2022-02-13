package ir.snapp.box.interview.task.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.messaging.FirebaseMessaging
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.*
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import github.nisrulz.screenshott.ScreenShott
import ir.snapp.box.interview.task.R
import ir.snapp.box.interview.task.const.Constants
import ir.snapp.box.interview.task.databinding.ActivityMainBinding
import ir.snapp.box.interview.task.databinding.DestinationMarkerBinding
import ir.snapp.box.interview.task.utils.CheckPermission
import ir.snapp.box.interview.task.utils.rialStyleConverter
import ir.snapp.box.interview.task.widget.AcceptOfferButton
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess


class MainActivity : BaseActivity(), MainActivityTasks, AcceptOfferButton.LongClickListener {

    private val viewModel: MainViewModel by viewModel()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var mapView: MapView
    private lateinit var mapBoxMap: MapboxMap

    private val permission: ActivityResultContracts.RequestPermission =
        ActivityResultContracts.RequestPermission()

    // handle permission request result
    private val activityLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(permission) { isGranted ->
            if (isGranted) {
                getUserLocation()
            } else {
                showLocationPermissionAlert()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mapView = binding.mapView
        mapBoxMap = mapView.getMapboxMap()

        // subscribe to LiveData observer when app is in foreground
        onRefreshLocation()

        // subscribe to LiveData observer when app is in background
        observeLocation()

        // load map and make it ready to use
        onMapReady()

        // get token from firebase
        getFirebaseToken()

        // zoom camera to points and restrict camera bound
        binding.centerCamera.setOnClickListener { boundMapCameraToStartAndEndDestination() }
    }

    // subscribe to LiveData observer when app is in foreground
    override fun onRefreshLocation() {
        viewModel.locationState.observe(this) {
            addStartDestinationMarker()
        }
    }

    // subscribe to LiveData observer when app is in foreground
    override fun observeLocation() {
        viewModel.pointData.observe(this) {
            viewModel.updateLocation(it)
        }
    }

    // load map and make it ready to use
    override fun onMapReady() {
        mapBoxMap.loadStyleUri(Style.MAPBOX_STREETS) {
            mapView.location.updateSettings {
                enabled = false
                pulsingEnabled = false
                locationPuck = LocationPuck2D(
                    topImage = AppCompatResources.getDrawable(
                        this@MainActivity,
                        R.drawable.header_marker
                    ),
                    bearingImage = AppCompatResources.getDrawable(
                        this@MainActivity,
                        R.drawable.line
                    ),
                    shadowImage = AppCompatResources.getDrawable(
                        this@MainActivity,
                        R.drawable.shadow
                    )
                )
                checkLocationPermission()
            }
        }
    }

    // check if android version is 6 and above
    // get runtime permission and if permission
    // not granted, show permission request or dialog
    override fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CheckPermission.isLocationPermissionGranted(this)) {
                getUserLocation()
            } else {
                getLocationPermissionFromUser()
            }
        } else {
            getUserLocation()
        }
    }

    // get user location when location permission is granted
    @SuppressLint("MissingPermission")
    override fun getUserLocation() {
        val locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        val locationEngineRequest = LocationEngineRequest.Builder(1000L)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setFastestInterval(1000L)
            .build()
        locationEngine.requestLocationUpdates(locationEngineRequest, object :
            LocationEngineCallback<LocationEngineResult> {
            override fun onSuccess(locationResult: LocationEngineResult?) {
                if (locationResult != null) {
                    if (locationResult.lastLocation != null) {
                        viewModel.submitUserLocation(
                            Point.fromLngLat(
                                locationResult.lastLocation?.longitude!!,
                                locationResult.lastLocation?.latitude!!
                            )
                        )
                        addUserMarker(
                            Point.fromLngLat(
                                locationResult.lastLocation?.longitude!!,
                                locationResult.lastLocation?.latitude!!
                            )
                        )
                        locationEngine.removeLocationUpdates(this)
                    }
                }
            }

            override fun onFailure(e: Exception) {
                e.printStackTrace()
            }

        }, Looper.getMainLooper())
    }

    // launch permission request for user
    override fun getLocationPermissionFromUser() {
        activityLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // add user marker on map when location permission is granted
    // and get latest updated user location from location engine of MapBox SDK
    override fun addUserMarker(point: Point) {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.user_icon)
        bitmap?.let {
            val annotationApi = mapView.annotations
            val pointAnnotationManager =
                annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
            pointAnnotationManager.addClickListener(OnPointAnnotationClickListener {
                zoomMapCameraToUserLocation()
                true
            })
        }
        observeLocation(intent.extras)
    }

    // zoom camera to location of user when on marker clicked
    override fun zoomMapCameraToUserLocation() {
        val userPoint = viewModel.viewState.value.userLocation
        val cameraOption = CameraOptions.Builder().center(userPoint).zoom(15.5).build()
        mapBoxMap.easeTo(
            cameraOption,
            MapAnimationOptions.mapAnimationOptions {
                duration(1000L)
            }
        )
    }

    // subscribe to LiveData observer when app is in background
    override fun observeLocation(extras: Bundle?) {
        val jsonData = extras?.getString(Constants.EXTRA_DATA)
        viewModel.updateState(jsonData)
    }

    // add terminal start destination marker on map
    override fun addStartDestinationMarker() {
        val lng = viewModel.locationState.value?.startDestination?.longitude()!!
        val lat = viewModel.locationState.value?.startDestination?.latitude()!!
        bitmapFromDrawableRes(this, R.drawable.start_destination_marker)?.let {
            Log.e("Saeid", "addStartDestinationMarker: $lng")
            val annotationApi = mapView.annotations
            val pointAnnotationManager =
                annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(lng, lat))
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
            pointAnnotationManager.addClickListener(OnPointAnnotationClickListener {
                zoomMapCameraToStartDestinationLocation()
                true
            })
        }
        addEndDestinationMarker()
    }

    // add terminal end destination marker on map
    override fun addEndDestinationMarker() {
        val lng = viewModel.locationState.value?.endDestination?.longitude()!!
        val lat = viewModel.locationState.value?.endDestination?.latitude()!!
        val markerBinding = DestinationMarkerBinding.inflate(layoutInflater)
        val b = ScreenShott.getInstance().takeScreenShotOfJustView(markerBinding.root)
        b.let {
            val annotationApi = mapView.annotations
            val pointAnnotationManager =
                annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(lng, lat))
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
            pointAnnotationManager.addClickListener(OnPointAnnotationClickListener {
                zoomMapCameraToEndDestinationLocation()
                true
            })
        }
        showOfferContent()
    }

    // show offer page when FCM push received and
    // terminal start destination and end destination
    // are up to dated
    override fun showOfferContent() {
        binding.offerPage.visibility = VISIBLE
        binding.tvOfferAmount.text = rialStyleConverter("35000")
        // get terminal start address and end address
        // collecting data from kotlin StateFlow
        // must be from lifecycleScope
        // DO NOT COLLECT DATA ON MAIN THREAD
        // IT MAY CAUSE TO CRASH APPLICATION
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.viewState.onEach {
                    if (it.error.isNotEmpty()) {
                        Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_LONG).show()
                    } else {
                        binding.tvStartDestinationAddress.text =
                            it.startDestinationAddress?.features?.firstOrNull()?.place_name_fa
                        binding.tvEndDestinationAddress.text =
                            it.endDestinationAddress?.features?.firstOrNull()?.place_name_fa
                    }
                }.launchIn(lifecycleScope)
            }
        }
        // call get address service to obtain terminal start and end address automatically
        viewModel.getAddress()
        binding.tvStartDestinationAddress.setOnClickListener {
            zoomMapCameraToUserLocation()
        }
        binding.tvEndDestinationAddress.setOnClickListener {
            zoomMapCameraToEndDestinationLocation()
        }
        binding.acceptOfferButton.setLongClickListener(this)

        // wait for view to initialize and draw
        Handler(Looper.getMainLooper()).postDelayed(
            { binding.acceptOfferButton.startInterval() },
            200
        )

        // restrict camera to 3 points (user, terminal start address, terminal end address)
        boundMapCameraToStartAndEndDestination()
    }

    // zoom camera to terminal start destination location by clicking on marker
    override fun zoomMapCameraToStartDestinationLocation() {
        val destinationPoint = viewModel.locationState.value?.startDestination
        val cameraOption = CameraOptions.Builder().center(destinationPoint).zoom(15.5).build()
        mapBoxMap.easeTo(
            cameraOption,
            MapAnimationOptions.mapAnimationOptions {
                duration(1000L)
            }
        )
    }

    // zoom camera to terminal end destination location by clicking on marker
    override fun zoomMapCameraToEndDestinationLocation() {
        val destinationPoint = viewModel.locationState.value?.endDestination
        val cameraOption = CameraOptions.Builder().center(destinationPoint).zoom(15.5).build()
        mapBoxMap.easeTo(
            cameraOption,
            MapAnimationOptions.mapAnimationOptions {
                duration(1000L)
            }
        )
    }

    // bound camera to center of 3 points (user, terminal start address, terminal end address)
    override fun boundMapCameraToStartAndEndDestination() {
        val userLocation = viewModel.viewState.value.userLocation
        val startDestination = viewModel.locationState.value?.startDestination
        val endDestination = viewModel.locationState.value?.endDestination
        if (startDestination == null || endDestination == null || userLocation == null) {
            return
        }
        val triangleCoordinates = listOf(
            listOf(
                Point.fromLngLat(startDestination.longitude(), startDestination.latitude()),
                Point.fromLngLat(endDestination.longitude(), endDestination.latitude()),
                Point.fromLngLat(userLocation.longitude(), userLocation.latitude())
            )
        )

        val polygon = Polygon.fromLngLats(triangleCoordinates)
        val cameraPosition =
            mapBoxMap.cameraForGeometry(polygon, EdgeInsets(300.0, 200.0, 900.0, 200.0))
        mapBoxMap.easeTo(
            cameraPosition,
            MapAnimationOptions.mapAnimationOptions {
                duration(2000L)
            }
        )
    }

    // show a dialog when user denied location permission
    override fun showLocationPermissionAlert() {
        AlertDialog.Builder(this)
            .setTitle(R.string.location_permission_title_dialog)
            .setMessage(R.string.location_permission_message_dialog)
            .setPositiveButton(R.string.location_permission_positive_button_dialog) { dialog, _ ->
                dialog.dismiss()
                val applicationSettingIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(
                        "package:$packageName"
                    )
                )
                applicationSettingIntent.addCategory(Intent.CATEGORY_DEFAULT)
                applicationSettingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(applicationSettingIntent)
            }.setNegativeButton(R.string.location_permission_negative_button_dialog) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    // get token from firebase
    override fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM token:", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM token:", "handle Firebase token: $token")
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    // on long click listener for accept offer button
    // finish and clear task
    override fun onPerform() {
        finishAndRemoveTask()
        exitProcess(0)
    }

    // handle back stack and change offer page visibility
    override fun onBackPressed() {
        if (binding.offerPage.visibility == VISIBLE) {
            binding.offerPage.visibility = GONE
        } else {
            super.onBackPressed()
            finishAndRemoveTask()
            exitProcess(0)
        }
    }
}