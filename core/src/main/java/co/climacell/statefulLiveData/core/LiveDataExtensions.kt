package co.climacell.statefulLiveData.core

import android.os.Looper
import androidx.lifecycle.MutableLiveData

/**
 * Sets the given value on the main thread.
 * If the current thread is the main thread, then the given value is set immediately.
 * If the current thread is not the main thread, a task is posted to the main thread to set the given value.
 *
 * @param value The new value
 */
fun <T> MutableLiveData<T>.putValue(value: T) {
    if (isMainThread()) {
        this.value = value
    } else {
        this.postValue(value)
    }
}

private fun isMainThread() = Looper.getMainLooper().thread == Thread.currentThread()