plugins {
    alias(libs.plugins.runkeeper.android.library)
}

android {
    namespace = "cz.dcervenka.run.network"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
}