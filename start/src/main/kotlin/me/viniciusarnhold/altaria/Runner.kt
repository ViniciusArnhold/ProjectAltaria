@file:JvmName("Runner")

package me.viniciusarnhold.altaria

import me.viniciusarnhold.altaria.core.App

/**
 * @author Vinicius Pegorini Arnhold.
 */
fun main(args: Array<String>) {
    loop@ while (true) {
        val result = App.execute(args)
        if (!result.shouldRestart()) {
            //TODO Reorganize restart logic
        }
    }
}
