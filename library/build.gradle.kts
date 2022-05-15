plugins {
    kotlin("plugin.serialization") version "1.6.10"
}

val junitVersion: String? by extra
val hamkrestVersion: String? by extra
val mockkVersion: String? by extra
val guiceVersion: String? by extra
val kotlinGuiceVersion: String? by extra
val externalLibraryVersion: String? by extra

dependencies {
    implementation("il.ac.technion.cs.softwaredesign", "primitive-storage-layer", externalLibraryVersion)

    implementation("com.google.inject", "guice", guiceVersion)
    implementation("dev.misfitlabs.kotlinguice4", "kotlin-guice", kotlinGuiceVersion)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    testImplementation("org.junit.jupiter", "junit-jupiter-params", junitVersion)
    testImplementation("com.natpryce", "hamkrest", hamkrestVersion)

    testImplementation("io.mockk", "mockk", mockkVersion)
}