plugins {
    alias(libs.plugins.runkeeper.android.library.compose)
}

android {
    namespace = "cz.dcervenka.core.presentation.designsystem_wear"

    defaultConfig {
        minSdk = 30
    }
}

dependencies {
    api(projects.core.presentation.designsystem)

    implementation(libs.androidx.wear.compose.material)
}