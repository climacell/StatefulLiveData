package co.climacell.statefulLiveData.coroutines

import kotlin.coroutines.CoroutineContext

class ConcurrentEventHandler(
    val cancellationHandler: (() -> Unit)? = null,
    val exceptionHandler: (CoroutineContext, Throwable) -> Unit
) : CoroutineContext.Element {

    companion object Key :
        CoroutineContext.Key<ConcurrentEventHandler>

    override val key: CoroutineContext.Key<*> = Key
}