plugins {
    alias(libs.plugins.runkeeper.android.feature.ui)
}

android {
    namespace = "cz.dcervenka.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
}