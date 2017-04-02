package me.viniciusarnhold.altaria.command;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import javax.annotation.Nullable;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
@SuppressWarnings("ConstantConditions")
class AbstractCommandTest {

    @Nullable
    private AbstractCommand command;

    @BeforeEach
    void setUp() {
        command = new AbstractCommand() {
            @Override
            public void handle(MessageReceivedEvent event) {

            }
        };
    }

    @AfterEach
    void tearDown() {
        command = null;
    }

    @Test
    void command() {
        Assertions.assertNotNull(command.command());
        Assertions.assertTrue(command.command().length() == 0);
    }

    @Test
    void aliases() {
        Assertions.assertNotNull(command.aliases());
        Assert.assertTrue(command.aliases().isEmpty());
    }

    @Test
    void description() {
        Assertions.assertNotNull(command.description());
        Assertions.assertTrue(command.description().length() == 0);
    }

    @Test
    void type() {
        Assertions.assertNotNull(command.type());
    }

    @Test
    void permissions() {
        Assertions.assertNotNull(command.permissions());
        Assertions.assertTrue(command.permissions().isEmpty());
    }

    @Test
    void publicConstructor() {
        try {
            AbstractCommand.class.getConstructor();
        } catch (NoSuchMethodException e) {
            Assert.fail("No public constructor");
        }
    }

}