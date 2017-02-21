package me.viniciusarnhold.altaria.excpetions;

/**
 * Created by Vinicius.
 *
 * @since 1.0
 */
public class BotLoginFailedException extends RuntimeException {

    public BotLoginFailedException(Exception e) {
        super(e);
    }
}
