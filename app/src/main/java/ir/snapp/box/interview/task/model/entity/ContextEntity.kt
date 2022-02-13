package ir.snapp.box.interview.task.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContextEntity(
    @SerializedName("id")
    @Expose
    val id: String?,
    @SerializedName("language")
    @Expose
    val language: String?,
    @SerializedName("language_fa")
    @Expose
    val language_fa: String?,
    @SerializedName("short_code")
    @Expose
    val short_code: String?,
    @SerializedName("text")
    @Expose
    val text: String?,
    @SerializedName("text_fa")
    @Expose
    val text_fa: String?,
    @SerializedName("wikidata")
    @Expose
    val wikidata: String?
)
