import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.ubunuworks.kloudsales.pc.externalprinter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

repositories {
    maven("https://jitpack.io")
}



dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    val voyagerVersion = "1.0.0"
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



}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KloudSalesPrinter"
            packageVersion = "1.0.0"
        }
    }
}
