package ir.snapp.box.interview.task.ui

import android.os.Bundle
import com.mapbox.geojson.Point

// tasks and user actions that MainActivity must do
interface MainActivityTasks {
    fun observeLocation()
    fun observeLocation(extras: Bundle?)
    fun onRefreshLocation()
    fun getFirebaseToken()
    fun getLocationPermissionFromUser()
    fun showLocationPermissionAlert()
    fun checkLocationPermission()
    fun getUserLocation()
    fun addStartDestinationMarker()
    fun addEndDestinationMarker()
    fun addUserMarker(point: Point)
    fun onMapReady()
    fun zoomMapCameraToUserLocation()
    fun zoomMapCameraToStartDestinationLocation()
    fun zoomMapCameraToEndDestinationLocation()
    fun boundMapCameraToStartAndEndDestination()
    fun showOfferContent()
}