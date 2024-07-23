plugins {
    alias(libs.plugins.runkeeper.jvm.library)
    alias(libs.plugins.runkeeper.jvm.junit5)
}

dependencies {
    implementation(projects.core.domain)
}