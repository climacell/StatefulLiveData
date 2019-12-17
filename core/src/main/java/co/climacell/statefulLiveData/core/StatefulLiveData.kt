package co.climacell.statefulLiveData.core

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

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
 * Returns a [StatefulLiveData] mapped from the [@receiver] [StatefulLiveData], while preserving its original state.
 * In the case where the original state is [StatefulData.Success], the [mapFunction] is applied to each value set
 * on the receiver. In other cases, the original state is preserved with its original data (i.e the original [StatefulData].)
 *
 * @receiver the [StatefulLiveData] to map from
 * @param mapFunction the lambda to apply to each value set on [@receiver] [StatefulLiveData] in case its state is [StatefulData.Success]
 *
 * @param [X] the generic type parameter of [@receiver] [StatefulLiveData]
 * @param [Y] the generic type parameter of the returned [StatefulLiveData]
 * @return a StatefulLiveData mapped from [@receiver] [StatefulLiveData] to type [Y] by applying
 * [mapFunction] to each value set in case the state is [StatefulData.Success].
 */
fun <T> MutableStatefulLiveData<T>.putLoading(loadingFunction: (() -> Any?)) {
    this.putLoading(loadingFunction())
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

/**
 * Maps StatefulLiveData to LiveData:
 *
 * When State is:
 *
 * • [StatefulData.Success][StatefulData.Success] -> returns data of type [T]
 *
 * • [StatefulData.Error][StatefulData.Error] -> returns result of overridden [errorMapFunction] or null
 *
 * • [StatefulData.Loading][StatefulData.Loading] -> returns result of overridden [loadingMapFunction] or null
 *
 * • To add support to other states of [StatefulData] override [fallbackMapFunction] (default result for other types is null).
 *
 * @return A [LiveData] of data type [T]
 */
fun <T> StatefulLiveData<T>.mapToLiveData(
    errorMapFunction: (Throwable) -> T? = { _ -> null },
    loadingMapFunction: (Any?) -> T? = { _ -> null },
    fallbackMapFunction: () -> T? = { null }
): LiveData<T> {
    return Transformations.map(this) {
        when (it) {
            is StatefulData.Success -> it.data
            is StatefulData.Error -> errorMapFunction(it.throwable)
            is StatefulData.Loading -> loadingMapFunction(it.loadingData)
            else -> fallbackMapFunction()
        }
    }
}

@MainThread
inline fun <reified T> StatefulLiveData<Any>.mapToTypedStatefulLiveData(): StatefulLiveData<T> {
    return Transformations.switchMap(this) {
        val mutableLiveData = MutableStatefulLiveData<T>()
        when (it) {
            is StatefulData.Success -> {
                if (it.data is T) {
                    mutableLiveData.putData(it.data as T)
                } else {
                    mutableLiveData.putError(StatefulLiveDataTypeMismatchException(T::class.java, it.data))
                }
            }
            is StatefulData.Loading -> mutableLiveData.putLoading { it.loadingData }
            is StatefulData.Error -> mutableLiveData.putError(it.throwable)
        }
        mutableLiveData
    }
}

