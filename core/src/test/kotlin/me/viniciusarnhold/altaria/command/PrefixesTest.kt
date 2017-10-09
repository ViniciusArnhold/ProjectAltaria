package me.viniciusarnhold.altaria.command

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
internal class PrefixesTest {

    @Test
    fun changePrefix() {
        var newPrefix = "!"
        assertThat(Prefixes.changePrefix(newPrefix)).isEqualTo(newPrefix)
        assertThat(Prefixes.current()).isEqualTo(newPrefix)

        newPrefix = "0"
        assertThat(Prefixes.changePrefix(newPrefix)).isEqualTo(newPrefix)
        assertThat(Prefixes.current()).isEqualTo(newPrefix)
    }
}