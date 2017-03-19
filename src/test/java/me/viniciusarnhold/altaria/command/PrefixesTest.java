package me.viniciusarnhold.altaria.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
class PrefixesTest {

    @Test
    void getInstance() {
        assertNotNull(Prefixes.getInstance());
        assertSame(Prefixes.getInstance().getClass(), Prefixes.class);
    }

    @Test
    void current() {
        assertNotNull(Prefixes.getInstance().current());
        assertSame(Prefixes.getInstance().current(), "@");
    }

    @Test
    void changePrefix() {
        assertThrows(IllegalArgumentException.class, () -> Prefixes.getInstance().changePrefix(null));

        assertEquals(Prefixes.getInstance().changePrefix("!"), "!");
        assertEquals(Prefixes.getInstance().current(), "!");

        assertEquals(Prefixes.getInstance().changePrefix("0"), "0");
        assertEquals(Prefixes.getInstance().current(), "0");

    }

}