package me.viniciusarnhold.altaria.utils;

import org.hamcrest.core.Is;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
class TimeUtilsTest {

    @Rule
    private final ExpectedException exception = ExpectedException.none();

    @Test
    void formatToString() {
        assertThat(TimeUtils.formatToString(30, TimeUnit.NANOSECONDS), Is.is("30 ns"));
        assertThat(TimeUtils.formatToString(30, TimeUnit.MICROSECONDS), Is.is("30 \u03bcs"));
        assertThat(TimeUtils.formatToString(30, TimeUnit.MILLISECONDS), Is.is("30 ms"));
        assertThat(TimeUtils.formatToString(30, TimeUnit.SECONDS), Is.is("30 s"));
        assertThat(TimeUtils.formatToString(30, TimeUnit.MINUTES), Is.is("30 min"));
        assertThat(TimeUtils.formatToString(5, TimeUnit.HOURS), Is.is("5 h"));
        assertThat(TimeUtils.formatToString(30, TimeUnit.DAYS), Is.is("30 d"));
    }

    @Test
    void formatToString1() {
        assertThat(TimeUtils.formatToString(30), Is.is("30 ns"));
        assertThat(TimeUtils.formatToString(30000), Is.is("30 \u03bcs"));
        assertThat(TimeUtils.formatToString(30000000), Is.is("30 ms"));
        assertThat(TimeUtils.formatToString(30000000000L), Is.is("30 s"));
        assertThat(TimeUtils.formatToString(1800000000000L), Is.is("30 min"));
        assertThat(TimeUtils.formatToString(18000000000000L), Is.is("5 h"));
        assertThat(TimeUtils.formatToString(2592000000000000L), Is.is("30 d"));
    }

    @Test
    void abbreviate() {
        assertNotNull(TimeUtils.abbreviate(TimeUnit.NANOSECONDS));
        assertNotNull(TimeUtils.abbreviate(TimeUnit.MICROSECONDS));
        assertNotNull(TimeUtils.abbreviate(TimeUnit.MILLISECONDS));
        assertNotNull(TimeUtils.abbreviate(TimeUnit.SECONDS));
        assertNotNull(TimeUtils.abbreviate(TimeUnit.MINUTES));
        assertNotNull(TimeUtils.abbreviate(TimeUnit.HOURS));
        assertNotNull(TimeUtils.abbreviate(TimeUnit.DAYS));
    }

    @Test
    void invalidCases() {
        assertThrows(IllegalArgumentException.class, () -> TimeUtils.abbreviate(null));

        @NotNull Class<?> clazz = TimeUtils.class;
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
                Assertions.fail("Private constructor should throw exception");
            } catch (InvocationTargetException t) {
                assertTrue(t.getTargetException() instanceof Error);
            }
            constructor.setAccessible(false);
        } catch (Exception e) {
            Assert.fail("No defaultConstructor");
        }
    }

}