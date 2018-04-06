package me.viniciusarnhold.altaria.api

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

/**
 * Created by Vinicius.

 * @since 1.0
 */
abstract class AbstractMessageCommand : IMessageCommand {

    val logger: Logger = LogManager.getLogger()

    abstract override val command: String
    abstract override val description: String
    override val aliases: Set<String> = emptySet()
    override val type: EnumSet<me.viniciusarnhold.altaria.api.CommandType> = EnumSet.of(me.viniciusarnhold.altaria.api.CommandType.GENERAL)
    override val permissions: EnumSet<UserPermissions> = EnumSet.noneOf(UserPermissions::class.java)
    override val public: Boolean = true
}
