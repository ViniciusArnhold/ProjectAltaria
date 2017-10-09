package me.viniciusarnhold.altaria.command.interfaces

import me.viniciusarnhold.altaria.command.CommandType
import me.viniciusarnhold.altaria.command.UserPermissions
import java.util.*

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
interface ICommand {

    fun command(): String

    fun aliases(): Set<String>

    fun description(): String

    fun type(): CommandType

    fun permissions(): EnumSet<UserPermissions>


}
