package co.climacell.statefulLiveData.core

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * StatefulLiveData builds upon [LiveData] and acts very much alike, but it's also data state-aware.
 *
 * This enables the observer to distinguish between different states the data can be in, such as [StatefulData.Success], [StatefulData.Error] etc.
 *
 * That means it emits updates when a new data is set or when the [state][StatefulData] of the data is changed.
 *
 * Similar to [LiveData], StatefulLiveData emits updates only when observed.
 *
 */
typealias StatefulLiveData<T> = LiveData<StatefulData<T>>

typealias MutableStatefulLiveData<T> = MutableLiveData<StatefulData<T>>

typealias MediatorStatefulLiveData<T> = MediatorLiveData<StatefulData<T>>

/**
 * Sets a [StatefulData.Success] state with the given value of type [T] on the main thread, using [MutableLiveData.putValue].
 *
 * @param data The new value
 */
fun <T> MutableStatefulLiveData<T>.putData(data: T) {
    this.putValue(StatefulData.Success(data))
}

/**
 * Sets a [StatefulData.Error] state with the given value of type [Throwable] on the main thread, using [MutableLiveData.putValue].
 *
 * @param error The new value
 */
fun <T> MutableStatefulLiveData<T>.putError(error: Throwable) {
    this.putValue(StatefulData.Error(error))
}

fun <T> MutableStatefulLiveData<T>.putLoading(loadingData: Any? = null) {
    this.putValue(StatefulData.Loading(loadingData))
}

/**
 * Similar to [LiveData.observe], but this observer only receives events if the changed data is in the [StatefulData.Success] state.
 * The parameter of the [Observer.onChanged] method is already unwrapped of [StatefulData] and is of type [T].
 *
 * Intended to be used in cases where only [StatefulData.Success] state is of interest.
 * In cases where more than one state is of interest, [LiveData.observe] or [StatefulLiveData.subscribe] should be used.
 *
 * @param owner The LifecycleOwner which controls the observer
 * @param observer The observer that will receive the events
 */
@MainThread
fun <T> StatefulLiveData<T>.observeSuccess(owner: LifecycleOwner, observer: Observer<in T>) {
    observe(owner, Observer<StatefulData<T>> {
        if (it is StatefulData.Success) {
            observer.onChanged(it.data)
        }
    })
}


/**
 * Similar to [LiveData.observe], but this observer only receives events if the changed data is in the [StatefulData.Error] state.
 * The parameter of the [Observer.onChanged] method is already unwrapped of [StatefulData] and is of type [Throwable].
 *
 * Intended to be used in cases where only [StatefulData.Error] state is of interest.
 * In cases where more than one state is of interest, [LiveData.observe] or [StatefulLiveData.subscribe] should be used.
 *
 * @param owner The LifecycleOwner which controls the observer
 * @param observer The observer that will receive the events
 */
@MainThread
fun <T> StatefulLiveData<T>.observeError(owner: LifecycleOwner, observer: Observer<in Throwable>) {
    observe(owner, Observer<StatefulData<T>> {
        if (it is StatefulData.Error) {
            observer.onChanged(it.throwable)
        }
    })
}


/**
 * Similar to [LiveData.observe], but this observer only receives events if the changed data is in the [StatefulData.Loading] state.
 * The parameter of the [Observer.onChanged] method is already unwrapped of [StatefulData] and is of type [Any?] [Any].
 *
 * Intended to be used in cases where only [StatefulData.Loading] state is of interest.
 * In cases where more than one state is of interest, [LiveData.observe] or [StatefulLiveData.subscribe] should be used.
 *
 * @param owner The LifecycleOwner which controls the observer
 * @param observer The observer that will receive the events
 */
@MainThread
fun <T> StatefulLiveData<T>.observeLoading(owner: LifecycleOwner, observer: Observer<in Any?>) {
    observe(owner, Observer<StatefulData<T>> {
        if (it is StatefulData.Loading) {
            observer.onChanged(it.loadingData)
        }
    })
}