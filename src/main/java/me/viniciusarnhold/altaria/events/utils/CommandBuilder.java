package me.viniciusarnhold.altaria.events.utils;

import org.jetbrains.annotations.NotNull;

public class CommandBuilder {
    private String command;
    private String helpText;
    private RegexEvents regex;

    public CommandBuilder command(@NotNull String command) {
        this.command = command;
        return this;
    }

    public CommandBuilder helpText(String helpText) {
        this.helpText = helpText;
        return this;
    }

    public CommandBuilder regex(RegexEvents regex) {
        this.regex = regex;
        return this;
    }

    public Command create() {
        return new Command(command, helpText, regex);
    }
}