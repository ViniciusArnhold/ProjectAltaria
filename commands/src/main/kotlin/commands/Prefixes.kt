package commands

import java.util.*

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
object Prefixes {
    var currentPrefix = "!"

    fun current(): String {
        return currentPrefix
    }

    fun changePrefix(newPrefix: String): String {
        Objects.requireNonNull(newPrefix)
        this.currentPrefix = newPrefix
        return this.currentPrefix
    }
}
