package me.viniciusarnhold.altaria.utils

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

/**
 * Created by Vinicius.

 * @since 1.0
 */
internal class TimeUtilsTest {

    @Test
    fun formatToString() {
        assertThat(TimeUtils.formatToString(30, TimeUnit.NANOSECONDS)).isEqualTo("30 ns")

        assertThat(TimeUtils.formatToString(30, TimeUnit.MICROSECONDS)).isEqualTo("30 \u03bcs")
        assertThat(TimeUtils.formatToString(30, TimeUnit.MILLISECONDS)).isEqualTo("30 ms")
        assertThat(TimeUtils.formatToString(30, TimeUnit.SECONDS)).isEqualTo("30 s")
        assertThat(TimeUtils.formatToString(30, TimeUnit.MINUTES)).isEqualTo("30 min")
        assertThat(TimeUtils.formatToString(5, TimeUnit.HOURS)).isEqualTo("5 h")
        assertThat(TimeUtils.formatToString(30, TimeUnit.DAYS)).isEqualTo("30 d")
    }

    @Test
    fun formatToString1() {
        assertThat(TimeUtils.formatToString(30)).isEqualTo("30 ns")
        assertThat(TimeUtils.formatToString(30000)).isEqualTo("30 \u03bcs")
        assertThat(TimeUtils.formatToString(30000000)).isEqualTo("30 ms")
        assertThat(TimeUtils.formatToString(30000000000L)).isEqualTo("30 s")
        assertThat(TimeUtils.formatToString(1800000000000L)).isEqualTo("30 min")
        assertThat(TimeUtils.formatToString(18000000000000L)).isEqualTo("5 h")
        assertThat(TimeUtils.formatToString(2592000000000000L)).isEqualTo("30 d")
    }

    @Test
    fun testNegative() {
        assertThatCode { TimeUtils.formatToString(-1) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun abbreviate() {
        assertThat(TimeUtils.abbreviate(TimeUnit.NANOSECONDS)).isNotNull()
        assertThat(TimeUtils.abbreviate(TimeUnit.MICROSECONDS)).isNotNull()
        assertThat(TimeUtils.abbreviate(TimeUnit.MILLISECONDS)).isNotNull()
        assertThat(TimeUtils.abbreviate(TimeUnit.SECONDS)).isNotNull()
        assertThat(TimeUtils.abbreviate(TimeUnit.MINUTES)).isNotNull()
        assertThat(TimeUtils.abbreviate(TimeUnit.HOURS)).isNotNull()
        assertThat(TimeUtils.abbreviate(TimeUnit.DAYS)).isNotNull()
    }
}