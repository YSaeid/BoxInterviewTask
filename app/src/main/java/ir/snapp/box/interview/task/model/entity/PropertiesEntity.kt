package ir.snapp.box.interview.task.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PropertiesEntity(
    @SerializedName("short_code")
    @Expose
    val short_code: String?,
    @SerializedName("wikidata")
    @Expose
    val wikidata: String?
)
