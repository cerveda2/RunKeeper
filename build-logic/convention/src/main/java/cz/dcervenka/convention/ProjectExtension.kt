package cz.dcervenka.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.libVersionString(alias: String): String {
    return libs.findVersion(alias).get().toString()
}

fun Project.libVersionInt(alias: String): Int {
    return libVersionString(alias).toInt()
}

