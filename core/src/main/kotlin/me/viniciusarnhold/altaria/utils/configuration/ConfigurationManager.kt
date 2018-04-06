package me.viniciusarnhold.altaria.utils.configuration

class ConfigurationManager(val config: APIConfiguration) {
    companion object {
        var currentInstance: ConfigurationManager? = null
            get() {
                if (field == null) throw IllegalStateException("Configuration is not initialized")
                return field
            }
    }
}
