package ir.snapp.box.interview.task.data

import com.mapbox.geojson.Point
import ir.snapp.box.interview.task.model.domain.AddressModel
import ir.snapp.box.interview.task.model.domain.PointModel
import ir.snapp.box.interview.task.repository.datasource.DataState

interface AppRepository {

    fun mapEntityModelToDomainModel(json: String?): PointModel?

    // get start and end destination from mapbox service
    suspend fun getAddressFromPoint(point: Point?): DataState<AddressModel?>

}