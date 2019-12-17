package co.climacell.statefulLiveData.googleTasks

import co.climacell.statefulLiveData.core.MutableStatefulLiveData
import co.climacell.statefulLiveData.core.StatefulLiveData
import co.climacell.statefulLiveData.core.putData
import co.climacell.statefulLiveData.core.putError
import co.climacell.statefulLiveData.core.putLoading
import com.google.android.gms.tasks.Task

fun <T> Task<T>.toStatefulLiveData(): StatefulLiveData<T> {
    return this.toStatefulLiveData { it }
}

fun <T, K> Task<T>.toStatefulLiveData(mapFunction: ((T) -> K)): StatefulLiveData<K> {
    val statefulLiveData = MutableStatefulLiveData<K>().apply {
        putLoading()
    }
    this.addOnSuccessListener {
        try {
            statefulLiveData.putData(mapFunction(it))
        } catch (e: Exception) {
            statefulLiveData.putError(e)
        }
    }.addOnFailureListener {
        statefulLiveData.putError(it)
    }.addOnCanceledListener {
        statefulLiveData.putError(TaskCancellationException())
    }

    return statefulLiveData
}