package co.climacell.statefulLiveData.retrofit

import android.util.Log
import androidx.lifecycle.LiveData
import co.climacell.statefulLiveData.core.StatefulData
import co.climacell.statefulLiveData.core.StatefulLiveData
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A call adapter which uses [StatefulLiveData].
 *
 * [StatefulLiveData] allows for a lifecycle-aware observation with state updates.
 *
 * IMPORTANT NOTE: It is recommended to replace all retrofit calls response types to [StatefulLiveData].
 *
 * If you do not wish to replace the response types,
 * make sure you add StatefulLiveDataCallAdapterFactory first before any other adapter
 * when creating the retrofit client.
 *
 * Usage example:
 *
 *    interface MyRetrofitService {
 *        @GET("weather/current")
 *        fun getWeather(): StatefulLiveData<Weather>
 *    }
 *
 */
class StatefulLiveDataCallAdapterFactory private constructor() : CallAdapter.Factory() {

    /**
     * A call adapter which uses [StatefulLiveData].
     *
     * [StatefulLiveData] allows for a lifecycle-aware observation with state updates.
     *
     * IMPORTANT NOTE: It is recommended to replace all retrofit calls response types to [StatefulLiveData].
     *
     * If you do not wish to replace the response types,
     * make sure you add StatefulLiveDataCallAdapterFactory first before any other adapter
     * when creating the retrofit client.
     *
     * Usage example:
     *
     *    interface MyRetrofitService {
     *        @GET("weather/current")
     *        fun getWeather(): StatefulLiveData<Weather>
     *    }
     *
     */
    companion object {
        /**
         * Creates an instance of StatefulLiveDataCallAdapterFactory that allows for asynchronous [StatefulLiveData] observations.
         *
         * IMPORTANT NOTE: It is recommended to replace all retrofit calls response types to [StatefulLiveData].
         *
         * If you do not wish to replace the response types,
         * make sure you add StatefulLiveDataCallAdapterFactory first before any other adapter
         * when creating the retrofit client.
         */
        fun create() = StatefulLiveDataCallAdapterFactory()

        private const val TAG = "StatefulAdapterFactory"
    }

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)
        if (rawObservableType != StatefulData::class.java) {
            logOtherTypesOfLiveDataCalls()
            return null
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("Resource must be parameterized")
        }
        val bodyType = getParameterUpperBound(0, observableType)
        return StatefulLiveDataCallAdapter<Any>(bodyType)
    }

    private fun logOtherTypesOfLiveDataCalls() {
        if (BuildConfig.DEBUG) {
            Log.e(
                TAG, "A LiveData call which is not StatefulLiveData has been detected. " +
                "It is recommended to replace all retrofit calls response types to StatefulLiveData. " +
                "If you do not wish to replace the response types, " +
                "make sure you add StatefulLiveDataCallAdapterFactory first before any other adapter " +
                "when creating the retrofit client."
            )
        }
    }
}