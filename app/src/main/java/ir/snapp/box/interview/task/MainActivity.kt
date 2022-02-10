package ir.snapp.box.interview.task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.maps.Style
import ir.snapp.box.interview.task.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
    }
}