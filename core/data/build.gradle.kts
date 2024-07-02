plugins {
    alias(libs.plugins.runkeeper.android.library)
    alias(libs.plugins.runkeeper.jvm.ktor)
}

android {
    namespace = "cz.dcervenka.core.data"
}

dependencies {
    implementation(libs.timber)

    implementation(projects.core.domain)
    implementation(projects.core.database)
}