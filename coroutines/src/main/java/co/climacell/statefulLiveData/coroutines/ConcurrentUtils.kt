package co.climacell.statefulLiveData.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.launchAndForget(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(context) {
        try {
            coroutineScope {
                block()
            }
        } catch (throwable: Throwable) {
            val concurrentEventHandler = context[ConcurrentEventHandler]
            handleThrowable(concurrentEventHandler, throwable, context)
        }
    }
}

class ConcurrentEventHandler(
    val cancellationHandler: (() -> Unit)? = null,
    val exceptionHandler: (CoroutineContext, Throwable) -> Unit
) : CoroutineContext.Element {

    companion object Key : CoroutineContext.Key<ConcurrentEventHandler>

    override val key: CoroutineContext.Key<*> = Key
}

fun Throwable.isCoroutineCancellationException() = this is CancellationException

private fun handleThrowable(
    concurrentEventHandler: ConcurrentEventHandler?,
    throwable: Throwable,
    context: CoroutineContext
) {
    if (concurrentEventHandler != null) {
        handleThrowableUsingConcurrentEventHandler(throwable, concurrentEventHandler, context)
    } else {
        context[CoroutineExceptionHandler]?.handleException(context, throwable)
    }
}

private fun handleThrowableUsingConcurrentEventHandler(
    throwable: Throwable,
    concurrentEventHandler: ConcurrentEventHandler,
    context: CoroutineContext
) {
    if (throwable.isCoroutineCancellationException()) {
        concurrentEventHandler.cancellationHandler?.invoke()
    } else {
        concurrentEventHandler.exceptionHandler.invoke(context, throwable)
    }
}