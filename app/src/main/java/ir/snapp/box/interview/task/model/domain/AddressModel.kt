package ir.snapp.box.interview.task.model.domain

data class AddressModel(
    var modelId: Int = 0,
    val attribution: String?,
    val features: List<FeatureModel?>?,
    val query: List<Double?>?,
    val type: String?
)
