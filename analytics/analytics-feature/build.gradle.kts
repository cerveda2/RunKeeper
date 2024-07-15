plugins {
    alias(libs.plugins.runkeeper.android.dynamic.feature)
}
android {
    namespace = "cz.dcervenka.analytics.analytics_feature"
}

dependencies {
    implementation(project(":app"))

    api(projects.analytics.presentation)
    implementation(projects.analytics.domain)
    implementation(projects.analytics.data)
    implementation(projects.core.database)
}