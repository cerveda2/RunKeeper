plugins {
    alias(libs.plugins.runkeeper.android.library)
    alias(libs.plugins.runkeeper.android.room)
}

android {
    namespace = "cz.dcervenka.core.database"
}

dependencies {
    implementation(libs.org.mongodb.bson)

    implementation(projects.core.domain)
}