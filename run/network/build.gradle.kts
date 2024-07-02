plugins {
    alias(libs.plugins.runkeeper.android.library)
    alias(libs.plugins.runkeeper.jvm.ktor)
}

android {
    namespace = "cz.dcervenka.run.network"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
}