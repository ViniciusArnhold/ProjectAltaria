package commands

import java.util.*

/**
 * @author Vinicius Pegorini Arnhold.
 */
interface ICommand {

    val command: String

    val aliases: Set<String>

    val description: String

    val type: EnumSet<commands.CommandType>

    val permissions: EnumSet<UserPermissions>
}