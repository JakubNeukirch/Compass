package eu.jakubneukirch.compass.utils


class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    /**
     * Get content if it wasn't used yet
     */
    val pendingContent: T?
        get() {
            return if (hasBeenHandled) {
                null
            } else {
                hasBeenHandled = true
                content
            }
        }

    fun peekContent(): T = content

    override fun equals(other: Any?): Boolean {
        return if (other is Event<*>) {
            content == other.peekContent()
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + hasBeenHandled.hashCode()
        return result
    }
}

fun <T> T.toEvent() = Event(this)