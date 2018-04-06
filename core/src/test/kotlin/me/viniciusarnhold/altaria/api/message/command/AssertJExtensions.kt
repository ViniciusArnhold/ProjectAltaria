package me.viniciusarnhold.altaria.api.message.command

import org.assertj.core.api.Assertions

/**
 * @author Vinicius Pegorini Arnhold.
 */
fun <E> E?.assertNonNull(): E {
    Assertions.assertThat(this).isNotNull()
    return this ?: throw AssertionError()
}