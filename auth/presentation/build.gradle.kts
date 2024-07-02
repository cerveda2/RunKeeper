plugins {
    alias(libs.plugins.runkeeper.android.feature.ui)
}

android {
    namespace = "cz.dcervenka.auth.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}