package ir.snapp.box.interview.task.repository.datasource.retrofit

import ir.snapp.box.interview.task.R
import ir.snapp.box.interview.task.utils.AppContext
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// general error handler
object ErrorHandler {

    fun handleErrors(throwable: Throwable): String {
        val context = AppContext.get().context
        return when (throwable) {
            is UnknownHostException -> context.getString(R.string.network_error_no_internet)
            is SocketTimeoutException -> context.getString(R.string.network_error_timeout)
            is InterruptedIOException -> context.getString(R.string.network_error_timeout)
            is HttpException -> {
                when (throwable.code()) {
                    401 -> context.getString(R.string.network_error_code_401)
                    500 -> context.getString(R.string.network_error_code_500)
                    else -> context.getString(R.string.network_error_general)
                }
            }
            else -> context.getString(R.string.network_error_general)
        }
    }

}