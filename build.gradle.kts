import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("app.cash.sqldelight") version "2.0.2"
}
group = "com.dantesys"
version = "1.1.1"
repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)
    implementation(compose.materialIconsExtended)
    val voyagerVersion = "1.1.0-beta02"
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
    implementation("app.cash.sqldelight:androidx-paging3-extensions:2.0.2")
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
    implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
    implementation("org.apache.pdfbox:pdfbox:3.0.3")
    implementation("com.github.vandeseer:easytable:1.0.2")
    implementation("sh.calvin.reorderable:reorderable:2.4.0")
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.dantesys")
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "sistemadetabelas"
            packageVersion = "$version"
            windows {
                includeAllModules = true
                dirChooser = true
                iconFile.set(project.file("src/main/resources/icon.ico"))
            }
        }
    }
}
