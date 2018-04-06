package me.viniciusarnhold.altaria.api

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * @author Vinicius Pegorini Arnhold.
 */
fun <T> Future<T>.toCompletable(): CompletableFuture<T> {
    return CompletableFuture.supplyAsync {
        this.get()
    }
}