package me.viniciusarnhold.altaria.command;

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

        public static final Manager getInstance() {
            return instance;
        }


    }

}
