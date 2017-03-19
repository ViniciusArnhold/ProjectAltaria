package me.viniciusarnhold.altaria.command.pool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sx.blah.discord.handle.impl.obj.Channel;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.impl.obj.PrivateChannel;
import sx.blah.discord.handle.obj.IMessage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
final class PoolCommandTest {
    public static IMessage createMessage(String content) {
        return createMessage(content, false);
    }

    public static IMessage createMessage(String content, boolean isPrivate) {
        return new Message(null, "01", content, null,

                isPrivate
                        ? new PrivateChannel(null, null, null)
                        : new Channel(null, null, null, null, null, 0, null, null),

                null, null, false, null, null, null, false, null, null, null);
    }

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    final void getInstance() {
        assertNotNull(PoolCommand.getInstance());
        assertSame(PoolCommand.getInstance().getClass(), PoolCommand.class);
    }

    @Test
    void command() {
        assertNotNull(PoolCommand.getInstance().command());
        assertTrue(!PoolCommand.getInstance().command().trim().isEmpty());
    }

    @Test
    void aliases() {
        assertNotNull(PoolCommand.getInstance().aliases());
    }

    @Test
    void description() {
        assertNotNull(PoolCommand.getInstance().description());
        assertTrue(!PoolCommand.getInstance().description().trim().isEmpty());
    }

    @Test
    void type() {
        assertNotNull(PoolCommand.getInstance().type());
    }

    @Test
    void permissions() {
        assertNotNull(PoolCommand.getInstance().permissions());
        assertTrue(!PoolCommand.getInstance().permissions().isEmpty());
    }

    @Test
    void handle() {
        //TODO Test
    }
}