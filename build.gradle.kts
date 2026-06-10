plugins {
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

allprojects {
    group = "me.mikun.mikunpic"
    version = "0.1.0"
}

subprojects {
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")

            ktlint("1.8.0")
                .editorConfigOverride(mapOf(
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                    "ktlint_standard_filename" to "disabled",
                    "ktlint_standard_kdoc" to "disabled"
                ))
        }
    }
}

tasks.register("runJsServer") {
    dependsOn(":app:webApp:jsBrowserDevelopmentRun")
    dependsOn(":server:run")
}

tasks.register("runDesktopServer") {
    dependsOn(":app:desktopApp:run")
    dependsOn(":server:run")
}