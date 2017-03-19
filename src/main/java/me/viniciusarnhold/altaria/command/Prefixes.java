package me.viniciusarnhold.altaria.command;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class Prefixes {

    @NotNull
    private static final String DEFAULT_PREFIX = "!";

    @NotNull
    private static Prefixes ourInstance = new Prefixes();
    @NotNull
    private String currentPrefix = DEFAULT_PREFIX;

    private Prefixes() {

    }

    @NotNull
    public static Prefixes getInstance() {
        return ourInstance;
    }

    @NotNull
    public String current() {
        return currentPrefix;
    }

    @NotNull
    public String changePrefix(@NotNull String newPrefix) {
        return this.currentPrefix = newPrefix;
    }
}
