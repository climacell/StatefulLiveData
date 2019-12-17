package co.climacell.statefulLiveData.core

/**
 * Base class for representing a state of data.
 *
 * StatefulData ships with three built-in states: [Success], [Error] and [Loading].
 *
 *• [Success] represents a state where the data has been retrieved successfully,
 * and has [Success.data] to hold the data of type [T].
 *
 *• [Error] represents a state where an error has occurred, and has [Error.throwable] to hold an instance of [Throwable].
 *
 *• [Loading] represents an intermediate state where the data retrieval is in progress,
 * and has [Loading.loadingData] of type [Any?][Any] to hold a partial data.
 *
 * This state should be followed by a definitive state, such as [Success] or [Error].
 *
 * @param [T] The type of data held by this instance
 */
abstract class StatefulData<T> {

    /**
     * Represents a state where the data has been retrieved successfully,
     * and has [Success.data] to hold the data of type [T].
     */
    class Success<T>(val data: T) : StatefulData<T>()

    /**
     * Represents a state where an error has occurred, and has [Error.throwable] to hold an instance of [Throwable].
     */
    class Error<T>(val throwable: Throwable) : StatefulData<T>()

    /**
     * Represents an intermediate state where the data retrieval is in progress,
     * and has [Loading.loadingData] of type [Any?][Any] to hold a partial data.
     *
     * This state should be followed by a definitive state, such as [Success] or [Error].
     */
    class Loading<T>(val loadingData: Any? = null) : StatefulData<T>()
}