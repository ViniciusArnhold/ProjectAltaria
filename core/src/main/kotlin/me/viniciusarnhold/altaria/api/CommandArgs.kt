package me.viniciusarnhold.altaria.api

import com.google.common.collect.ForwardingList
import com.google.common.collect.ImmutableList
import javax.annotation.concurrent.Immutable

/**
 * @author Vinicius Pegorini Arnhold.
 */
@Immutable
class CommandArgs(args: Sequence<Arg>, private val knownParameters: Map<String, Arg?>) : ForwardingList<Arg>() {

    private val delegate: List<Arg> = ImmutableList.copyOf(args.iterator())

    companion object {
        fun from(args: Sequence<Arg>): CommandArgs {
            val knownParameters = mutableMapOf<String, Arg?>()
            val iterator = args.iterator()
            while (iterator.hasNext()) {
                val arg = iterator.next()
                toParameter(arg.value)?.let {
                    when {
                        iterator.hasNext() -> {
                            val next = iterator.next()
                            when {
                                toParameter(next.value) == null -> knownParameters.put(it, next)
                                else -> knownParameters.put(it, null)
                            }
                        }
                        else -> knownParameters.put(it, null)
                    }
                }
            }
            return CommandArgs(args, knownParameters)
        }

        private fun toParameter(value: String): String? = when {
            value.startsWith("--") -> value.substring(2)
            value.startsWith("-") -> value.substring(1)
            else -> null
        }
    }

    override fun delegate(): List<Arg> = delegate

    fun hasParameter(parameter: String): Boolean = knownParameters.containsKey(parameter)

    fun getParameterValue(parameter: String): Arg? = knownParameters[parameter]
}