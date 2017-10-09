package me.viniciusarnhold.altaria.apis

import okhttp3.OkHttpClient


/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class HttpManager private constructor() {


    val defaultClient: OkHttpClient
        get() = defaultHttpClient

    companion object {

        private val defaultHttpClient: OkHttpClient

        val instance = HttpManager()

        init {
            defaultHttpClient = OkHttpClient.Builder().build()
        }
    }
}
