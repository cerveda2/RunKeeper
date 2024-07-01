plugins {
    alias(libs.plugins.runkeeper.android.library)
}

android {
    namespace = "cz.dcervenka.core.database"
}

dependencies {
    implementation(libs.org.mongodb.bson)

    implementation(projects.core.domain)
}