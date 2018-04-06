package me.viniciusarnhold.altaria.apis

import okhttp3.OkHttpClient


/**
 * Created by Vinicius.

 * @since 1.0
 */
class HttpManagerService {
    val defaultClient: OkHttpClient = OkHttpClient.Builder().build()
}
