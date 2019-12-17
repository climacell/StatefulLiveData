package co.climacell.statefulLiveData.coroutines

import androidx.annotation.MainThread
import co.climacell.statefulLiveData.core.MediatorStatefulLiveData
import co.climacell.statefulLiveData.core.StatefulData
import co.climacell.statefulLiveData.core.StatefulLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CoroutineStatefulTransformations {
    /**
     * Performs a transformation to this [StatefulLiveData] when state is
     * [StatefulData.Success][StatefulData.Success] from type [X] to [Y] similar to [map].
     *
     * Unlike [map], the mapping occurs inside a coroutine with [Dispatchers.Default]
     * and the result is set in [Dispatchers.Main].
     *
     * @param X in data type
     * @param Y out data type
     * @param coroutineScope the coroutine scope to perform the mapping.
     * @return [StatefulLiveData] with out data type [Y]
     */
    @MainThread
    fun <X, Y> map(
        source: StatefulLiveData<X>,
        coroutineScope: CoroutineScope,
        mapFunction: (X) -> Y
    ): StatefulLiveData<Y> {
        val mediator = MediatorStatefulLiveData<Y>()
        mediator.addSource(source) {
            when (it) {
                is StatefulData.Success -> {
                    coroutineScope.launch(Dispatchers.Default) {
                        try {
                            kotlinx.coroutines.coroutineScope {
                                val result = mapFunction(it.data)
                                withContext(Dispatchers.Main) {
                                    mediator.value = StatefulData.Success(result)
                                }
                            }
                        } catch (e: Exception) {
                            mediator.value = StatefulData.Error(e)
                        }
                    }
                }
                is StatefulData.Error -> mediator.value = StatefulData.Error(it.throwable)
                is StatefulData.Loading -> mediator.value = StatefulData.Loading(it.loadingData)
            }
        }
        return mediator
    }
}