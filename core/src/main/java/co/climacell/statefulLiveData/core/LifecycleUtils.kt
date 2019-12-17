package co.climacell.statefulLiveData.core

import androidx.lifecycle.MutableLiveData

/**
 * A convenient way to create a [MutableLiveData] with an initial value.
 *
 * @return [MutableLiveData] with an initial value of type T.
 */
fun <T> mutableLiveDataOf(value: T): MutableLiveData<T> {
    val mutableLiveData = MutableLiveData<T>()
    mutableLiveData.putValue(value)
    return mutableLiveData
}

/**
 * A convenient way to create a [MutableStatefulLiveData] with an initial value.
 *
 * @return [MutableStatefulLiveData] with an initial value of type [StatefulData].
 */
fun <T> mutableStatefulLiveDataOf(value: StatefulData<T>): MutableStatefulLiveData<T> {
    val mutableStatefulLiveData = MutableStatefulLiveData<T>()
    mutableStatefulLiveData.putValue(value)
    return mutableStatefulLiveData
}