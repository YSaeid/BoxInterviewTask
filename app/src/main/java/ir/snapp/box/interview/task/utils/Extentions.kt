package ir.snapp.box.interview.task.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import ir.snapp.box.interview.task.model.mapper.EntityMapper
import ir.snapp.box.interview.task.repository.datasource.DataState
import ir.snapp.box.interview.task.repository.datasource.retrofit.ErrorHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mapstruct.factory.Mappers
import java.net.SocketTimeoutException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

// separate amount
fun String.amountSeparator(amount: Long?): String {
    if (amount == null) {
        return "0"
    }
    val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
    val symbols = formatter.decimalFormatSymbols
    symbols.groupingSeparator = ','
    formatter.decimalFormatSymbols = symbols
    return formatter.format(amount)
}

// append amount and rial with style
// always amount text is bigger than rial
fun rialStyleConverter(value: String?): CharSequence? {
    if (value == null) {
        return null
    }
    val amountSpan = SpannableString(value.amountSeparator(value.toLong()))
    amountSpan.setSpan(RelativeSizeSpan(1f), 0, amountSpan.length, 0)
    amountSpan.setSpan(ForegroundColorSpan(Color.WHITE), 0, amountSpan.length, 0)
    val rialSpan = SpannableString("ریال")
    rialSpan.setSpan(RelativeSizeSpan(0.5f), 0, rialSpan.length, 0)
    rialSpan.setSpan(ForegroundColorSpan(Color.WHITE), 0, rialSpan.length, 0)
    return TextUtils.concat(amountSpan, " ", rialSpan)
}

// mapbox geocoding for retrieve location based on latitude and longitude
fun createUrlFromPoint(lng: Double, lat: Double): String {
    return "/geocoding/v5/mapbox.places/$lng,$lat.json?language=fa&access_token=pk.eyJ1Ijoicy15YXpkYW5pIiwiYSI6ImNremdwbWRqdDNyajYyb3B2ZWcwY3k1MmUifQ.SqEYog0GvYia26dVnAincQ"
}

// generic send request extension function,
// it take Entity and Domain model and mapper class
// with suspend function
// call suspend function inside coroutine scope and give request response with
// general error handling
suspend fun <E, D, T : EntityMapper<E, D>> sendRequest(
    mapperClass: Class<T>,
    body: suspend () -> E
): DataState<D?> {
    return withContext(Dispatchers.IO) {
        try {
            val response = body()
            if (response != null) {
                val data =
                    Mappers.getMapper(mapperClass).mapFromEntityToDomainModel(response)
                DataState.Success(data = data)
            } else {
                DataState.Error(ErrorHandler.handleErrors(SocketTimeoutException()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(ErrorHandler.handleErrors(e))
        }
    }
}