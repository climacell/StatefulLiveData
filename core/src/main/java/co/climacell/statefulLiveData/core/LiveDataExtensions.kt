package co.climacell.statefulLiveData.core

import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

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

/**
 * Observers [LiveData] object once, for a single event.
 *
 * If [retainForLoadingState] is true and [T] is [StatefulData] than it will keep observing if [StatefulData.Loading] is received,
 * waiting for a definitive result [StatefulData.Success] or [StatefulData.Error] (or any other type).
 *
 * @param T The type of the parameter
 * @param observer The observer that will receive the events
 * @param retainForLoadingState Default true.
 */
@MainThread
fun <T> LiveData<T>.observeOnce(observer: Observer<T>, retainForLoadingState: Boolean = true) {
    this.observeForever(object : Observer<T> {
        override fun onChanged(t: T) {
            if (t == null) {
                return
            }

            if (t !is StatefulData<*> || !retainForLoadingState || t !is StatefulData.Loading<*>) {
                removeObserver(this)
            }

            observer.onChanged(t)
        }
    })
}

private fun isMainThread() = Looper.getMainLooper().thread == Thread.currentThread()