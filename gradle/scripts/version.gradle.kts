val version by extra {
    mapOf(
            "commons_validator".to("1.6"),
            "assertj".to("3.8.0"),
            "mockito".to("2.8.47"),
            "junit_platform".to("1.0.0"),
            "commons_io".to("2.5"),
            "durian".to("3.4.0"),
            "junit".to("5.0.0"),
            "commons_configuration2".to("2.1.1"),
            "guava".to("21.0"),
            "junit_plugin".to("1.0.0"),
            "jackson".to("2.9.0"),
            "log4j".to("2.8.2"),
            "kotlin".to("1.1.51"),
            "spek".to("1.1.5"),
            "truth".to("0.39"),
            "commons_cli".to("1.3.1")
    )
}

for ((key, value) in version) {
    project.extra["${key}_version"] = value
}