package ir.snapp.box.interview.task.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.mapbox.geojson.Point
import ir.snapp.box.interview.task.model.domain.AddressModel
import ir.snapp.box.interview.task.model.domain.PointModel
import ir.snapp.box.interview.task.model.entity.PointEntity
import ir.snapp.box.interview.task.model.mapper.AddressMapper
import ir.snapp.box.interview.task.model.mapper.PointMapper
import ir.snapp.box.interview.task.repository.datasource.DataState
import ir.snapp.box.interview.task.repository.datasource.retrofit.ApiServices
import ir.snapp.box.interview.task.utils.createUrlFromPoint
import ir.snapp.box.interview.task.utils.sendRequest
import org.mapstruct.factory.Mappers

class AppRepositoryImpl(
    private val apiServices: ApiServices
) : AppRepository {

    private val _pointData = MutableLiveData<PointModel?>()
    val pointData: LiveData<PointModel?> = _pointData


    // call everytime when FCM push received
    fun onDataReceived(value: RemoteMessage) {
        val json = Gson().toJson(value.data)
        val model = mapEntityModelToDomainModel(json)
        _pointData.postValue(model)
    }

    // map from entity model to domain model
    override fun mapEntityModelToDomainModel(json: String?): PointModel? {
        val entity = Gson().fromJson(json, PointEntity::class.java)
        return Mappers.getMapper(PointMapper::class.java)
            .mapFromEntityToDomainModel(entity = entity)
    }

    override suspend fun getAddressFromPoint(point: Point?): DataState<AddressModel?> {
        return sendRequest(AddressMapper::class.java) {
            apiServices.getAddressFromPoint(
                createUrlFromPoint(point?.longitude()!!, point.latitude())
            )
        }
    }
}