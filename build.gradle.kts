plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false

    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.android.lint) apply false
}

subprojects {
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")

            ktlint("1.8.0")
                .editorConfigOverride(mapOf(
                ))
        }
    }
}

tasks.register("runJsServer") {

    dependsOn(":app:webApp:jsBrowserDevelopmentRun")
    dependsOn(":server:run")
}