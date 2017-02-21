package me.viniciusarnhold.altaria.events.utils;

import org.junit.Test;

import static me.viniciusarnhold.altaria.events.utils.RegexEvents.NO_ARGS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class RegexEventsTest {

    @Test
    public void SIMPLE_ARGSTest() {

        assertTrue(RegexEvents.SIMPLE_ARGS.getRegex().matcher("!alt Google::Annon   \"Oh  my god\" ".trim()).find());

        assertTrue(RegexEvents.SIMPLE_ARGS.getRegex().matcher("!alt Google::   \"Oh  my god\" ".trim()).find());

        assertFalse(RegexEvents.SIMPLE_ARGS.getRegex().matcher("!alt   Google::Annon   ".trim()).find());

        assertFalse(RegexEvents.SIMPLE_ARGS.getRegex().matcher("!alt ::Annon   ".trim()).find());

        assertFalse(RegexEvents.SIMPLE_ARGS.getRegex().matcher("!alt").find());

        assertFalse(RegexEvents.SIMPLE_ARGS.getRegex().matcher("").find());
    }

    @Test
    public void noArgsTest() {

        assertTrue(NO_ARGS.getRegex().matcher("!alt Info").find());

        assertTrue(NO_ARGS.getRegex().matcher("!alt Info").find());

        assertTrue(NO_ARGS.getRegex().matcher("!alt Info::").find());

        assertTrue(NO_ARGS.getRegex().matcher("!alt ::Info::").find());

        assertTrue(NO_ARGS.getRegex().matcher("!alt :Info:").find());

        assertTrue(NO_ARGS.getRegex().matcher("!alt Help").find());

        assertFalse(NO_ARGS.getRegex().matcher("!alt Info \"args\"").find());

        assertFalse(NO_ARGS.getRegex().matcher("!alt ").find());

        assertFalse(NO_ARGS.getRegex().matcher("!alt  \"args\"").find());

        assertFalse(NO_ARGS.getRegex().matcher("!alt Info:: \"args\"").find());

        assertFalse(NO_ARGS.getRegex().matcher("!alt Pesquisa::Google \"args\"").find());
    }
}