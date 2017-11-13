package me.viniciusarnhold.altaria.command

import java.util.*

/**
 * @author Vinicius Pegorini Arnhold.
 */
interface ICommand {

    val command: String

    val aliases: Set<String>

    val description: String

    val type: EnumSet<me.viniciusarnhold.altaria.command.CommandType>

    val permissions: EnumSet<UserPermissions>
}