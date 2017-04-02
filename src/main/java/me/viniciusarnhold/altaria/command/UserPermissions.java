package me.viniciusarnhold.altaria.command;


import org.jetbrains.annotations.NotNull;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public enum UserPermissions {

    MANAGE_POOL("altaria.util.pools");

    private final String key;

    UserPermissions(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }

    public static class Manager {

        private static final Manager instance = new Manager();

        private Manager() {

        }


        @NotNull
        public static Manager getInstance() {
            return instance;
        }


    }

}
