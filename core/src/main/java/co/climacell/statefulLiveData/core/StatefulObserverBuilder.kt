package co.climacell.statefulLiveData.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import java.lang.ref.WeakReference

/**
 * Create flexible observation for [StatefulLiveData].
 *
 * Call this subscribe with [LifecycleOwner], followed by state observation(s), and finally [observe][StatefulObserverBuilder.observe]
 *
 * Available state observations:
 *
 *  • [onSuccess][StatefulObserverBuilder.onSuccess]
 *
 *  • [onLoading][StatefulObserverBuilder.onLoading]
 *
 *  • [onError][StatefulObserverBuilder.onError]
 *
 * Example:
 *
 *      myStatefulLiveData.subscribe(this)
 *          .onSuccess { it: T ->
 *              // success actions
 *          }.onLoading { it: Any? ->
 *              // loading actions
 *          }.onError { it: Throwable ->
 *              // error actions
 *          }.observe()
 */
fun <T> StatefulLiveData<T>.subscribe(owner: LifecycleOwner) = StatefulObserverBuilder(this, owner)

/**
 * Used to create flexible observation for [StatefulLiveData].
 *
 * Create this using [subscribe], followed by state observation(s), and finally [observe][StatefulObserverBuilder.observe]
 *
 * Available state observations:
 *
 *  • [onSuccess][StatefulObserverBuilder.onSuccess]
 *
 *  • [onLoading][StatefulObserverBuilder.onLoading]
 *
 *  • [onError][StatefulObserverBuilder.onError]
 *
 * Example:
 *
 *      myStatefulLiveData.subscribe(this)
 *          .onSuccess { it: T ->
 *              // success actions
 *          }.onLoading { it: Any? ->
 *              // loading actions
 *          }.onError { it: Throwable ->
 *              // error actions
 *          }.observe()
 */
class StatefulObserverBuilder<T>internal constructor(statefulLiveData: StatefulLiveData<T>, owner: LifecycleOwner) {

    private val statefulLiveDataToObserve: WeakReference<StatefulLiveData<T>> =
        WeakReference(statefulLiveData)
    private val lifecycleOwner: WeakReference<LifecycleOwner> = WeakReference(owner)

    private var successBlock: ((T) -> Unit)? = null
    private var loadingBlock: ((Any?) -> Unit)? = null
    private var errorBlock: ((Throwable) -> Unit)? = null

    /**
     * This will be invoked when the [StatefulLiveData] result is [StatefulData.Success][StatefulData.Success]
     */
    fun onSuccess(successBlock: (T) -> Unit): StatefulObserverBuilder<T> {
        this.successBlock = successBlock

        return this
    }

    /**
     * This will be invoked when the [StatefulLiveData] result is [StatefulData.Loading][StatefulData.Loading]
     */
    fun onLoading(loadingBlock: (Any?) -> Unit): StatefulObserverBuilder<T> {
        this.loadingBlock = loadingBlock

        return this
    }

    /**
     * This will be invoked when the [StatefulLiveData] result is [StatefulData.Error][StatefulData.Error]
     */
    fun onError(errorBlock: (Throwable) -> Unit): StatefulObserverBuilder<T> {
        this.errorBlock = errorBlock

        return this
    }

    /**
     * Begins observation of [StatefulLiveData] with subscribed [LifecycleOwner] and used state observations.
     *
     * @throws NullPointerException if either this [StatefulLiveData] is null or if subscribed [LifecycleOwner] is null.
     */
    @Throws(NullPointerException::class)
    fun observe() {
        val statefulLiveData = statefulLiveDataToObserve.get()
        val owner = lifecycleOwner.get()

        if (statefulLiveData == null) {
            throw NullPointerException("Can't observe on a null StatefulLiveData object.")
        }

        if (owner == null) {
            throw NullPointerException("Can't observe with a null LifecycleOwner.")
        }

        statefulLiveData.observe(owner, Observer {
            when (it) {
                is StatefulData.Success -> successBlock?.invoke(it.data)
                is StatefulData.Loading -> loadingBlock?.invoke(it.loadingData)
                is StatefulData.Error -> errorBlock?.invoke(it.throwable)
            }
        })
    }
}