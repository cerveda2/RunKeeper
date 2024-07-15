plugins {
    alias(libs.plugins.runkeeper.android.library)
}

android {
    namespace = "cz.dcervenka.analytics.data"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.koin)

    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.analytics.domain)
}