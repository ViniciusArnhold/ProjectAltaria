package me.viniciusarnhold.altaria.events.utils;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class Command implements Comparable<Command> {

    private final String command;
    private String helpText;
    private RegexEvents regex;

    Command(@NotNull String command, String helpText, RegexEvents regex) {
        this.regex = regex;
        this.command = command;
        this.helpText = helpText;
    }

    public String getCommand() {
        return command;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(@NotNull String helpText) {
        this.helpText = helpText;
    }

    public RegexEvents getRegex() {
        return regex;
    }

    public void setRegex(@NotNull RegexEvents regex) {
        this.regex = regex;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Command command1 = (Command) o;

        return new EqualsBuilder()
                .append(command, command1.command)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(command)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("command", command)
                .add("helpText", helpText)
                .add("regex", regex)
                .toString();
    }

    @Override
    public int compareTo(@NotNull Command o) {
        return this.command.compareTo(o.command);
    }
}
