package me.viniciusarnhold.altaria.api

import java.util.*

/**
 * Created by Vinicius.

 * @since 1.0
 */
interface IMessageCommand : ICommand {
    val command: String

    val aliases: Set<String>

    val description: String

    val type: EnumSet<me.viniciusarnhold.altaria.api.CommandType>

    val permissions: EnumSet<UserPermissions>

    val public: Boolean

    fun onCommand(ctx: me.viniciusarnhold.altaria.api.MessageCommandContext)
}
