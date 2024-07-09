import com.android.build.api.dsl.ApplicationExtension
import cz.dcervenka.convention.ExtensionType
import cz.dcervenka.convention.configureBuildTypes
import cz.dcervenka.convention.configureKotlinAndroid
import cz.dcervenka.convention.libVersionInt
import cz.dcervenka.convention.libVersionString
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    applicationId = libVersionString("projectApplicationId")
                    targetSdk = libVersionInt("projectTargetSdkVersion")
                    versionCode = libVersionInt("projectVersionCode")
                    versionName = libVersionString("projectVersionName")
                }

                configureKotlinAndroid(this)

                configureBuildTypes(
                    commonExtension = this,
                    extensionType = ExtensionType.APPLICATION
                )
            }
        }
    }
}