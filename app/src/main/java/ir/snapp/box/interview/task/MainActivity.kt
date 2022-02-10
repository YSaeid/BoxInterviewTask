package ir.snapp.box.interview.task

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.locationcomponent.location
import ir.snapp.box.interview.task.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val permission: ActivityResultContracts.RequestPermission =
        ActivityResultContracts.RequestPermission()

    private val activityLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(permission) { isGranted ->
            if (isGranted) {
                loadMap()
            } else {
                showGetPermissionFromUserDialog()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        activityLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showGetPermissionFromUserDialog() {
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

    private fun loadMap() {
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            binding.mapView.location.updateSettings {
                enabled = true
                pulsingEnabled = true
            }
        }
    }
}