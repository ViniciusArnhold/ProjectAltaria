package me.viniciusarnhold.altaria.api

import java.net.URL

/**
 * @author Vinicius Pegorini Arnhold.
 */

open class Arg(value: String) {
    var value: String
        private set

    init {
        this.value = value.trim()
    }
}

open class LongArg(val longValue: Long) : Arg(longValue.toString())

open class URLArg(val urlValue: URL) : Arg(urlValue.toString())
