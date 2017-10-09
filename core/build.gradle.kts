description = "Core Project - ${project.version}"

dependencies {
    "compile"("com.google.guava:guava:${properties["guava_version"]}")
    "compile"("commons-io:commons-io:${properties["commons_io_version"]}")
    "compile"("com.diffplug.durian:durian:${properties["durian_version"]}")

    "compile"("com.vdurmont:emoji-java:3.2.0")

    "compile"("com.github.austinv11:Discord4j:2.8.+")

    "compile"("com.squareup.okhttp3:okhttp:3.6.0")

    "compile"("org.jsoup:jsoup:1.10.2")

    "compile"("com.rometools:rome-fetcher:1.7.1")
    "compile"("com.rometools:rome:1.7.1")

    "compile"("com.google.apis:google-api-services-urlshortener:v1-rev47-1.22.0")
    "compile"("com.github.Omertron:api-imdb:api-imdb-1.5")
    "compile"("com.google.api-client:google-api-client:1.22.0")
    "compile"("com.google.oauth-client:google-oauth-client:1.22.0")
    "compile"("com.google.http-client:google-http-client:1.22.0")
    "compile"("com.google.http-client:google-http-client-jackson2:1.22.0")
    "compile"("com.google.apis:google-api-services-customsearch:v1-rev53-1.22.0")
    "compile"("com.google.apis:google-api-services-surveys:v2-rev5-1.22.0")
    "compile"("com.google.oauth-client:google-oauth-client-jetty:1.22.0")
    "compile"("com.google.apis:google-api-services-sheets:v4-rev462-1.22.0")
    "compile"("com.google.apis:google-api-services-script:v1-rev65-1.22.0")

    "compile"("com.github.Omertron:api-themoviedb:themoviedbapi-4.3")

    "compile"("com.fasterxml.jackson.core:jackson-core:${properties["jackson_version"]}")
    "compile"("com.fasterxml.jackson.core:jackson-annotations:${properties["jackson_version"]}")
    "compile"("com.fasterxml.jackson.core:jackson-databind:${properties["jackson_version"]}")

    "compile"("commons-cli:commons-cli:1.3.1")

    "compile"("org.jetbrains:annotations:15.0")

    "compile"("org.apache.commons:commons-configuration2:${properties["commons_configuration2_version"]}")

    "compile"("commons-validator:commons-validator:${properties["commons_validator_version"]}")


}