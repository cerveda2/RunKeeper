plugins {
    alias(libs.plugins.runkeeper.jvm.library)
    alias(libs.plugins.runkeeper.jvm.junit5)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)

    testImplementation(projects.core.test)
}