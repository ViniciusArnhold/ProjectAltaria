package me.viniciusarnhold.altaria.events.utils;

import java.util.regex.Pattern;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public enum RegexEvents {

    SIMPLE_ARGS(Pattern.compile("^(?:!alt)(?:\\s*)([A-Za-z0-9:=]*)(?:\\s*)(?:\"((?:[A-Za-z0-9: ]|\\n)+)\")$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)),

    NO_ARGS(Pattern.compile("^(?:!alt)(?:\\s*)([A-Za-z0-9:=]+)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL));

    private final Pattern regex;

    RegexEvents(Pattern regex) {
        this.regex = regex;
    }

    public Pattern getRegex() {
        return regex;
    }
}
