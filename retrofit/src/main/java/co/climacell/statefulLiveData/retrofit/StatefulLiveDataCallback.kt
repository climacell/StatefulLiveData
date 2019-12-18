package co.climacell.statefulLiveData.retrofit

import android.accounts.NetworkErrorException
import co.climacell.statefulLiveData.core.MutableStatefulLiveData
import co.climacell.statefulLiveData.core.putData
import co.climacell.statefulLiveData.core.putError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.CancellationException

internal class StatefulLiveDataCallback<R>(
    private val statefulLiveDataResponse: MutableStatefulLiveData<R>,
    private val responseType: Type
) : Callback<R> {

    private companion object {
        const val RESPONSE_CODE_204_NO_CONTENT = 204
    }

    override fun onResponse(call: Call<R>, response: Response<R>) {
        when {
            call.isCanceled -> resolveCancel()
            response.isSuccessful -> resolveSuccess(response)
            else -> resolveError(response)
        }
    }

    override fun onFailure(call: Call<R>, t: Throwable) {
        statefulLiveDataResponse.putError(t)
    }

    private fun resolveCancel() {
        statefulLiveDataResponse.putError(CancellationException())
    }

    private fun resolveSuccess(response: Response<R>) {
        try {

            if (isResponseTypeUnit()) {
                @Suppress("UNCHECKED_CAST")
                statefulLiveDataResponse.putData(Unit as R)
                return
            }

            val responseBody = response.body()

            if (responseBody == null || response.code() == RESPONSE_CODE_204_NO_CONTENT) {
                resolveTypeMismatchError(response)
            } else {
                statefulLiveDataResponse.putData(responseBody)
            }
        } catch (e: Exception) {
            statefulLiveDataResponse.putError(e)
        }
    }

    private fun resolveTypeMismatchError(response: Response<R>) {
        val errorMessage =
            if (response.code() == RESPONSE_CODE_204_NO_CONTENT) {
                "Response code is $RESPONSE_CODE_204_NO_CONTENT while response type is not Unit."
            } else {
                "Response body is null while response type is $responseType."
            }
        statefulLiveDataResponse.putError(TypeCastException("Error while performing call (response code: ${response.code()}) - $errorMessage"))
    }

    private fun resolveError(response: Response<R>) {
        var errorMessage = try {
            response.errorBody()?.string()
        } catch (ignored: Exception) {
            // in case of IOException or OutOfMemoryException when trying to get the error.
            null
        }

        if (errorMessage.isNullOrBlank()) {
            errorMessage = response.message()
        }

        if (errorMessage.isNullOrBlank()) {
            errorMessage = "Unknown error"
        }

        statefulLiveDataResponse.putError(NetworkErrorException("Error while performing call (response code: ${response.code()}) - Error message: $errorMessage"))
    }

    private fun isResponseTypeUnit() = responseType == Unit::class.java
}