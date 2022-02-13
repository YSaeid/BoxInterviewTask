package ir.snapp.box.interview.task.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FeatureEntity(
    @SerializedName("bbox")
    @Expose
    val bbox: List<Double?>?,
    @SerializedName("center")
    @Expose
    val center: List<Double?>?,
    @SerializedName("context")
    @Expose
    val context: List<ContextEntity?>?,
    @SerializedName("geometry")
    @Expose
    val geometry: GeometryEntity?,
    @SerializedName("id")
    @Expose
    val id: String?,
    @SerializedName("language")
    @Expose
    val language: String?,
    @SerializedName("language_fa")
    @Expose
    val language_fa: String?,
    @SerializedName("place_name")
    @Expose
    val place_name: String?,
    @SerializedName("place_name_fa")
    @Expose
    val place_name_fa: String?,
    @SerializedName("place_type")
    @Expose
    val place_type: List<String?>?,
    @SerializedName("properties")
    @Expose
    val properties: PropertiesEntity?,
    @SerializedName("relevance")
    @Expose
    val relevance: Int?,
    @SerializedName("text")
    @Expose
    val text: String?,
    @SerializedName("text_fa")
    @Expose
    val text_fa: String?,
    @SerializedName("type")
    @Expose
    val type: String?
)
