plugins {
    alias(libs.plugins.runkeeper.jvm.library)
    alias(libs.plugins.runkeeper.jvm.junit5)
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)
    implementation(projects.run.domain)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.junit5.api)
    implementation(libs.coroutines.test)
}