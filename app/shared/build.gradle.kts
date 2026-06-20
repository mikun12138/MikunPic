import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm()

    js {
        browser()
    }

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//    }

    androidLibrary {
       namespace = "me.mikun.mikunpic.app.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        jvmMain.dependencies {
        }
        commonMain.dependencies {
            implementation(projects.dto)

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            api(libs.androidx.navigation.navigationCompose)
//            implementation("org.jetbrains.compose.ui:ui-text-google-fonts:1.11.1")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
            implementation("com.russhwolf:multiplatform-settings:1.3.0")
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.3.0")
            implementation("com.russhwolf:multiplatform-settings-serialization:1.3.0")

            implementation("io.github.vinceglb:filekit-core:0.14.1")
            implementation("io.github.vinceglb:filekit-dialogs:0.14.1")
            implementation("io.github.vinceglb:filekit-dialogs-compose:0.14.1")
            implementation("io.github.vinceglb:filekit-coil:0.14.1")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}