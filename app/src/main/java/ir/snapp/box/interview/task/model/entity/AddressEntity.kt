package ir.snapp.box.interview.task.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddressEntity(

    val entityId: Int = 0,

    @SerializedName("attribution")
    @Expose
    val attribution: String?,
    @SerializedName("features")
    @Expose
    val features: List<FeatureEntity?>?,
    @SerializedName("query")
    @Expose
    val query: List<Double?>?,
    @SerializedName("type")
    @Expose
    val type: String?
)
