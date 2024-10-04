import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.nio.file.Paths

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("io.realm.kotlin") version "1.16.0"
    id("app.cash.sqldelight") version "2.0.2"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "com.ubunuworks.kloudsales.pc.externalprinter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}



sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.ubunuworks.kloudsales.pc.externalprinter")

            // Define schema output directory on Desktop
            schemaOutputDirectory = file("${System.getProperty("user.home")}/Desktop/receipts/AppDatabase")
        }
    }
}



repositories {
    maven("https://jitpack.io")
}



java {
    sourceCompatibility = JavaVersion.VERSION_17 // Set to Java version you are using
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Set to match Java version
    }
}
tasks.register("printJavaVersion") {
    doLast {
        println("Java version: ${System.getProperty("java.version")}")
        println("Java vendor: ${System.getProperty("java.vendor")}")
    }
}
compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KloudSalesPrinter"
            packageVersion = "1.0.0"
            description = "A printer utility application"
            vendor = "UbunuWorks"
            copyright = "UbuniWorks © 2024"

            linux {
                // Linux-specific settings
                debMaintainer = "support@ubunuworks.com"

                    // Include java.sql module
                modules.addAll(listOf("java.sql", "java.desktop"))

            }

            windows {
                // Windows-specific settings
                menuGroup = "UbunuWorks"
                shortcut = true
                console = false
            }

        }
    }
}

tasks.create("packageComposeApp") {
    dependsOn("createDistributable")
    doLast {
        println("Compose Desktop JAR and binaries have been packaged.")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "MainKt" // Replace with your actual main class
    }

    from({
        configurations.runtimeClasspath.get().filter {
            it.name.endsWith("jar")
        }.map { zipTree(it) }
    })

    // Exclude signature files
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}




dependencies {

    val voyagerVersion = "1.0.0"
    val ktor_version = "2.3.8"
    implementation(compose.desktop.currentOs)

    //PDF
    implementation("org.xhtmlrenderer:flying-saucer-core:9.1.22")
    implementation("org.xhtmlrenderer:flying-saucer-pdf-itext5:9.1.22")
    implementation("org.apache.pdfbox:pdfbox:2.0.27")
    implementation("com.itextpdf:itextpdf:5.5.13.3")
    //QR CODE
    implementation("com.google.zxing:core:3.3.0")
    implementation("com.google.zxing:javase:3.3.0")
    // Multiplatform Voyager
    // Navigator
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
    // Screen Model
    implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")
    // Transitions
    implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
    // Kodein integration
    implementation("cafe.adriel.voyager:voyager-kodein:$voyagerVersion")
    //ImageLoading
    api("io.github.qdsfdhvh:image-loader:1.2.1")
    //Extended material icons
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.3.0")
    //RealmDb
    implementation("io.realm.kotlin:library-base:1.16.0")
    // If using Device Sync
    implementation("io.realm.kotlin:library-sync:1.16.0")
    // If using coroutines with the SDK
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
    //Room
    implementation("com.squareup.sqldelight:runtime:1.5.3")
    implementation("com.squareup.sqldelight:coroutines-extensions:1.5.3")
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
    //Kotlinx Serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    //Kafka
    //Ktor
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    // Logging plugin
    implementation("io.ktor:ktor-client-logging:2.3.4")
    //Serialization
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    implementation("com.github.hkirk:java-html2image:0.9")
    implementation("org.apache.pdfbox:pdfbox:2.0.24") // Adjust the version as needed
    implementation("org.apache.pdfbox:pdfbox-tools:2.0.24") // Required for PDF rendering
    implementation("com.itextpdf:itext7-core:7.1.16")

    implementation(files("libs/jcl.jar"))
    implementation(files("libs/jpos113-controls.jar"))
    implementation(files("libs/xercesimpl.jar"))
    implementation(files("libs/xml-apis.jar"))
    implementation(files("libs/JposPrinterJavaPOS.jar"))
    //Html to pdf
    implementation("com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10")
    implementation("com.openhtmltopdf:openhtmltopdf-slf4j:1.0.10")
    //Printing html
    implementation("org.xhtmlrenderer:flying-saucer-pdf:9.9.5")








}


