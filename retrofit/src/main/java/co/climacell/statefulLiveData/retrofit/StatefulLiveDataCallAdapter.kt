package co.climacell.statefulLiveData.retrofit

import co.climacell.statefulLiveData.core.MutableStatefulLiveData
import co.climacell.statefulLiveData.core.StatefulLiveData
import co.climacell.statefulLiveData.core.putLoading
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

internal class StatefulLiveDataCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, StatefulLiveData<R>> {

    override fun adapt(call: Call<R>): StatefulLiveData<R> =
        object : MutableStatefulLiveData<R>() {
            private var isStarted = AtomicBoolean(false)

            override fun onActive() {
                super.onActive()
                if (isStarted.compareAndSet(false, true)) {
                    putLoading()
                    call.enqueue(StatefulLiveDataCallback(this, responseType))
                }
            }
        }

    override fun responseType() = responseType

}