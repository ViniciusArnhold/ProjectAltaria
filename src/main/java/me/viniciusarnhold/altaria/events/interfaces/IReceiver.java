package me.viniciusarnhold.altaria.events.interfaces;

import org.jetbrains.annotations.NotNull;
import sx.blah.discord.api.events.Event;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public interface IReceiver {

    @NotNull
    Class<? extends Event> getEventType();

    void disable();

}
