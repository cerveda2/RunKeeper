plugins {
    alias(libs.plugins.runkeeper.android.library)
}

android {
    namespace = "cz.dcervenka.wear.run.data"

    defaultConfig {
        minSdk = 30
    }
}

dependencies {
    implementation(libs.androidx.health.services.client)
    implementation(libs.bundles.koin)
}