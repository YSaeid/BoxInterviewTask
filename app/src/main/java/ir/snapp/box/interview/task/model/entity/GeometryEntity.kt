package ir.snapp.box.interview.task.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GeometryEntity(
    @SerializedName("coordinates")
    @Expose
    val coordinates: List<Double?>?,
    @SerializedName("type")
    @Expose
    val type: String?
)
