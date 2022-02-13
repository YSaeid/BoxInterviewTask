package ir.snapp.box.interview.task.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import ir.snapp.box.interview.task.data.AppRepositoryImpl
import ir.snapp.box.interview.task.model.domain.AddressModel
import ir.snapp.box.interview.task.model.domain.PointModel
import ir.snapp.box.interview.task.repository.datasource.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val appRepositoryImpl: AppRepositoryImpl
) : ViewModel() {

    // start and end location data based on LiveData for observation and  update UI
    val pointData = appRepositoryImpl.pointData

    // application View State
    // StateFlow is like LiveData
    // it emit data when values are not the same
    private val _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    // update location and observe from this LiveData
    private val _locationState = MutableLiveData<LocationState>()
    val locationState: LiveData<LocationState> = _locationState


    data class LocationState(
        val startDestination: Point? = null,
        val endDestination: Point? = null,
    )

    data class ViewState(
        val userLocation: Point? = null,
        val showLoading: Boolean = false,
        val startDestinationAddress: AddressModel? = null,
        val endDestinationAddress: AddressModel? = null,
        val error: String = ""
    )

    fun updateLocation(pointModel: PointModel?) {
        pointModel?.let { model ->
            val locationState = LocationState(
                startDestination = Point.fromLngLat(model.start_lng!!, model.start_lat!!),
                endDestination = Point.fromLngLat(model.end_lng!!, model.end_lat!!)
            )
            _locationState.value = locationState
        }
    }

    fun submitUserLocation(userLocation: Point) {
        viewModelScope.launch {
            _viewState.update {
                it.copy(userLocation = userLocation)
            }
        }
    }

    // update start and end location when app is not running
    // this method called when app is killed and launched from
    // alarm manager
    fun updateState(jsonData: String?) {
        jsonData.let {
            val model = appRepositoryImpl.mapEntityModelToDomainModel(it)
            model?.let {
                val locationState = LocationState(
                    startDestination = Point.fromLngLat(model.start_lng!!, model.start_lat!!),
                    endDestination = Point.fromLngLat(model.end_lng!!, model.end_lat!!)
                )
                _locationState.value = locationState
            }
        }
    }

    // get start destination address
    // and end destination address of terminals
    // and update ViewState
    fun getAddress() {
        val start = _locationState.value?.startDestination
        val end = _locationState.value?.endDestination
        viewModelScope.launch {
            when (val startAddress = appRepositoryImpl.getAddressFromPoint(start)) {
                is DataState.Success -> {
                    _viewState.update {
                        it.copy(
                            startDestinationAddress = startAddress.data
                        )
                    }
                }
                is DataState.Error -> {
                    _viewState.update {
                        it.copy(
                            error = startAddress.exceptionMessage
                        )
                    }
                }
            }
            when (val endAddress = appRepositoryImpl.getAddressFromPoint(end)) {
                is DataState.Success -> {
                    _viewState.update {
                        it.copy(
                            endDestinationAddress = endAddress.data
                        )
                    }
                }
                is DataState.Error -> {
                    _viewState.update {
                        it.copy(
                            error = endAddress.exceptionMessage
                        )
                    }
                }
            }
        }
    }
}