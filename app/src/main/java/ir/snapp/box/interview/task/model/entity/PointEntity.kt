package ir.snapp.box.interview.task.model.entity

import com.google.gson.annotations.SerializedName

data class PointEntity(
    val entityId: Int = 0,
    @SerializedName("start_lat")
    val start_lat: String? = "",
    @SerializedName("start_lng")
    val start_lng: String? = "",
    @SerializedName("end_lat")
    val end_lat: String? = "",
    @SerializedName("end_lng")
    val end_lng: String? = "",
)
