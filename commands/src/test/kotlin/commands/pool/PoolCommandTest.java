package commands.pool;

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
@SuppressWarnings("ConstantConditions")
final class PoolCommandTest {
    public static IMessage createMessage(String content) {
        return createMessage(content, false);
    }

    public static IMessage createMessage(String content, boolean isPrivate) {
        return new Message(null, 1, content, null,
                isPrivate
                        ? new PrivateChannel(null, null, 1)
                        : new Channel(null, null, 1, null, null, 0, null, null),

                null, null, false, null, null, null, false, null, null, 0);
    }

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    final void getInstance() {
        assertNotNull(PoolCommand.Companion.getInstance());
        assertSame(PoolCommand.Companion.getInstance().getClass(), PoolCommand.class);
    }

    @Test
    void command() {
        assertNotNull(PoolCommand.Companion.getInstance().command());
        assertTrue(!PoolCommand.Companion.getInstance().command().trim().isEmpty());
    }

    @Test
    void aliases() {
        assertNotNull(PoolCommand.Companion.getInstance().aliases());
    }

    @Test
    void description() {
        assertNotNull(PoolCommand.Companion.getInstance().description());
        assertTrue(!PoolCommand.Companion.getInstance().description().trim().isEmpty());
    }

    @Test
    void type() {
        assertNotNull(PoolCommand.Companion.getInstance().type());
    }

    @Test
    void permissions() {
        assertNotNull(PoolCommand.Companion.getInstance().permissions());
        assertTrue(!PoolCommand.Companion.getInstance().permissions().isEmpty());
    }

    @Test
    void handle() {
        //TODO Test
    }
}