package co.climacell.statefulLiveData.core

import androidx.annotation.MainThread
import androidx.lifecycle.Transformations

object StatefulTransformations {

    /**
     * Returns a [StatefulLiveData] mapped from the input [source], while preserving its original state.
     * In the case where the original state is [StatefulData.Success], the [mapFunction] is applied to each value set
     * on [source]. In other cases, the original state is preserved with its original data (i.e the original [StatefulData].)
     *
     * @param source the [StatefulLiveData] to map from
     * @param mapFunction the lambda to apply to each value set on [source] in case the state of [source] is [StatefulData.Success]
     *
     * @param [X] the generic type parameter of [source]
     * @param [Y] the generic type parameter of the returned [StatefulLiveData]
     * @return a StatefulLiveData mapped from [source] to type [Y] by applying
     * [mapFunction] to each value set in case the state of [source] is [StatefulData.Success].
     */
    @MainThread
    fun <X, Y> map(source: StatefulLiveData<X>, mapFunction: (X) -> Y): StatefulLiveData<Y> {
        val mediator = MediatorStatefulLiveData<Y>()
        mediator.addSource(source) {
            when (it) {
                is StatefulData.Success -> mediator.value = StatefulData.Success(mapFunction(it.data))
                is StatefulData.Error -> mediator.value = StatefulData.Error(it.throwable)
                is StatefulData.Loading -> mediator.value = StatefulData.Loading(it.loadingData)
            }
        }
        return mediator
    }

    /**
     * Returns a [StatefulLiveData] mapped from the input [source], while preserving its original state.
     * In the case where the original state is [StatefulData.Success], the [mapFunction] is applied to each value set
     * on [source]. In other cases, the original state is preserved with its original data (i.e the original [StatefulData].)
     *
     * In the case where the original state is [StatefulData.Success],
     * the returned [StatefulLiveData] delegates to the most recent [StatefulLiveData] created by
     * calling [mapFunction] with the most recent value set to [source], without
     * changing the reference. This way, [mapFunction] can change the 'backing'
     * [StatefulLiveData] transparently to any observer registered to the [StatefulLiveData] returned
     * by [switchMap]. In other cases, the original state is preserved with its original data (i.e the original [StatefulData].)
     *
     * @param source the [StatefulLiveData] to map from
     * @param mapFunction the lambda to apply to each value set on [source] in case the state of [source] is [StatefulData.Success],
     *                    in order to create a new delegate StatefulLiveData for the returned one
     *
     * @param [X] the generic type parameter of [source]
     * @param [Y] the generic type parameter of the returned [StatefulLiveData]
     * @return a StatefulLiveData mapped from [source] to type [Y] by delegating to the StatefulLiveData returned by
     *         applying [mapFunction] to each value set in case the state of [source] is [StatefulData.Success].
     */
    @MainThread
    fun <X, Y> switchMap(source: StatefulLiveData<X>, mapFunction: (X) -> StatefulLiveData<Y>): StatefulLiveData<Y> {
        return Transformations.switchMap(source) {
            var statefulLiveData: StatefulLiveData<Y> = MutableStatefulLiveData()
            when (it) {
                is StatefulData.Success -> statefulLiveData = mapFunction(it.data)
                is StatefulData.Loading -> statefulLiveData =
                    mutableStatefulLiveDataOf(StatefulData.Loading(it.loadingData))
                is StatefulData.Error -> statefulLiveData = mutableStatefulLiveDataOf(StatefulData.Error(it.throwable))
            }
            statefulLiveData
        }
    }
}