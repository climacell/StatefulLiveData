package co.climacell.statefulLiveData.core

class StatefulLiveDataTypeMismatchException(type: Class<*>, data: Any?) : Exception(constructMessage(type, data)) {

    companion object {
        private fun constructMessage(type: Class<*>, data: Any?): String {
            return when (data) {
                null -> "Expected: [$type], Found: [null]"
                else -> "Expected: [$type], Found: [${data::class.java}]"
            }
        }
    }
}