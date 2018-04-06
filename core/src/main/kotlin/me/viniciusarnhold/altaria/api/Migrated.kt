package me.viniciusarnhold.altaria.api

@Target(AnnotationTarget.CLASS)
annotation class Migrated(val migrationStatus: MigrationStatus)