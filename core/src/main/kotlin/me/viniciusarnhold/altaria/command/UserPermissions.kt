package me.viniciusarnhold.altaria.command

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
enum class UserPermissions constructor(private val key: String) {

    MANAGE_POOL("altaria.util.pools");

    fun key(): String {
        return this.key
    }
}
