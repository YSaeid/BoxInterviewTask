package ir.snapp.box.interview.task.model.domain

data class FeatureModel(
    val bbox: List<Double?>?,
    val center: List<Double?>?,
    val context: List<ContextModel?>?,
    val geometry: GeometryModel?,
    val id: String?,
    val language: String?,
    val language_fa: String?,
    val place_name: String?,
    val place_name_fa: String?,
    val place_type: List<String?>?,
    val properties: PropertiesModel?,
    val relevance: Int?,
    val text: String?,
    val text_fa: String?,
    val type: String?
)
