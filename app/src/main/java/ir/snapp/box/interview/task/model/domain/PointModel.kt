package ir.snapp.box.interview.task.model.domain

data class PointModel (
    var modelId: Int = 0,
    var start_lat: Double? = null,
    var start_lng: Double? = null,
    var end_lat: Double? = null,
    var end_lng: Double? = null,
)
