package me.viniciusarnhold.altaria.events.utils;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class CommandTest {
    Command fullCommand;
    Command simpleCommand;

    @Before
    public void setUp() throws Exception {
        simpleCommand = new CommandBuilder().command("").create();
        fullCommand = new CommandBuilder().command("cmd").helpText("help").regex(RegexEvents.SIMPLE_ARGS).create();
    }

    @After
    public void tearDown() throws Exception {
        fullCommand = null;
        simpleCommand = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam() throws Exception {
        new CommandBuilder().create();
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(new CommandBuilder().command("").create(), new CommandBuilder().command("").create());
        assertEquals(new CommandBuilder().command("").create(), simpleCommand);
        assertNotEquals(simpleCommand, null);
        assertNotEquals(simpleCommand, fullCommand);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(fullCommand.hashCode(),
                new HashCodeBuilder(17, 37).append(fullCommand.getCommand()).toHashCode());
        assertEquals(simpleCommand.hashCode(),
                new HashCodeBuilder(17, 37).append(simpleCommand.getCommand()).toHashCode());
    }

    @Test
    public void testCompareTo() throws Exception {
        assertThat(new CommandBuilder().command("cmd").create().compareTo(fullCommand), CoreMatchers.is(0));
    }

}