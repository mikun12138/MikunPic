import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("dev.opensavvy.vite.kotlin") version "0.9.0"
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
        useEsModules()
    }

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        binaries.executable()
//    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.app.shared)

            implementation(libs.compose.ui)
        }
    }
}

vite {
    server {
        host = "127.0.0.1"
        port = 8081
    }
    plugin(
        packageName = "vite-plugin-node-polyfills",
        exportedAs = "nodePolyfills",
        version = "0.28.0",
        configuration = null,
        isNamedExport = true,
        isLocal = false
    )
}

tasks.register<Copy>("copyComposeResourcesToViteDist") {
    from(layout.buildDirectory.dir("vite/js/prod/kotlin/composeResources"))
    into(layout.buildDirectory.dir("vite/js/dist/composeResources"))
}

afterEvaluate {
    tasks.findByName("jsViteBuild")?.finalizedBy("copyComposeResourcesToViteDist")
}


tasks.named<KotlinWebpack>("jsBrowserDevelopmentRun") {
    devServerProperty.set(
        devServerProperty.get().copy(
            port = 8081
        )
    )
}

//tasks.named<KotlinWebpack>("wasmJsBrowserDevelopmentRun") {
//    devServerProperty.set(
//        devServerProperty.get().copy(
//            port = 8081,
//            open = false
//        )
//    )
//}