package ir.snapp.box.interview.task.repository.datasource

// state of data from request
// if response success return Success
// otherwise return Error
sealed class DataState<out R> {
    data class Success<out T>(val data: T?) : DataState<T?>()
    data class Error(val exceptionMessage: String) : DataState<Nothing>()
}
