plugins {
    alias(libs.plugins.runkeeper.jvm.library)
}

dependencies {
    implementation(projects.core.domain)
}