package me.viniciusarnhold.altaria.api.dispatch

import me.viniciusarnhold.altaria.api.AltariaException

/**
 * @author Vinicius Pegorini Arnhold.
 */
class CommandHandleException(message: String, cause: Throwable) : AltariaException(message, cause)