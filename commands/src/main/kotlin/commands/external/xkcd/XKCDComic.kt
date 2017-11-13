package me.viniciusarnhold.altaria.apis.objects

import com.fasterxml.jackson.annotation.JsonProperty
import me.viniciusarnhold.altaria.apis.HttpManager
import me.viniciusarnhold.altaria.core.App
import okhttp3.Request
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class XKCDComic {

    @JsonProperty("month")
    var month: String? = null
    @JsonProperty("num")
    var comicNumber: Int = 0
    @JsonProperty("link")
    var link: String? = null
    @JsonProperty("year")
    var year: String? = null
    @JsonProperty("news")
    var news: String? = null
    @JsonProperty("safe_title")
    var safeTitle: String? = null
    @JsonProperty("transcript")
    var transcript: String? = null
    @JsonProperty("alt")
    var altText: String? = null
    @JsonProperty("img")
            //Ok to throw exception
    var imageLink: String? = null
        set(imageLink) {
            var imageLink = imageLink
            imageLink = StringUtils.defaultString(imageLink, "")
            if (imageLink.endsWith(".png")) {
                try {
                    val biggerImage = imageLink.replace(".png", "_2x.png")
                    val request = Request.Builder()
                            .url(biggerImage)
                            .head()
                            .build()

                    val response = HttpManager.instance.defaultClient
                            .newBuilder()
                            .followRedirects(false)
                            .followSslRedirects(false)
                            .build()
                            .newCall(request)
                            .execute()

                    if (response.code() < 300) {
                        field = biggerImage
                    } else {
                        field = imageLink
                    }
                } catch (e: Exception) {
                    LogManager.getLogger(App::class.java).trace(e)
                    field = imageLink
                }

            } else {
                field = imageLink
            }
        }
    @JsonProperty("title")
    var title: String? = null
    @JsonProperty("day")
    var day: String? = null
}
