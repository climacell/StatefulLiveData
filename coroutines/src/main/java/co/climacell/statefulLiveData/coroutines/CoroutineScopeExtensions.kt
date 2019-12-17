package co.climacell.statefulLiveData.coroutines

import co.climacell.statefulLiveData.core.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Similar to [launch], with the exception of updating events to the [StatefulLiveData] from the coroutine.
 *
 * @param T result type of [block]
 * @param context additional to CoroutineScope.coroutineContext context of the coroutine.
 * @param start coroutine start option. The default value is CoroutineStart.DEFAULT.
 * @param block the coroutine code which will be invoked in the context of the provided scope, Return [T].
 * @return [StatefulLiveData] with the following states:
 *
 * • [StatefulData.Loading][StatefulData.Loading] when beginning consumption of [block]
 *
 * • [StatefulData.Success][StatefulData.Success] upon coroutine completion
 *
 * • [StatefulData.Error][StatefulData.Error] upon error
 */
fun <T> CoroutineScope.launchToStatefulLiveData(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): StatefulLiveData<T> {
    val statefulLiveData = MutableStatefulLiveData<T>().apply {
        putLoading()
    }

    launch(context, start) {
        try {
            coroutineScope {
                val result = block()
                statefulLiveData.putData(result)
            }
        } catch (throwable: Throwable) {
            val concurrentEventHandler = context[ConcurrentEventHandler]
            handleThrowable(concurrentEventHandler, throwable, context)
            statefulLiveData.putError(throwable)
        }
    }

    return statefulLiveData
}

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