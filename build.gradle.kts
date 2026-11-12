plugins {
    // Plugin de Kotlin per a la JVM
    kotlin("jvm") version "2.2.20"

    // Plugin per poder executar l'aplicació amb ./gradlew run
    application

    // Plugin oficial de JavaFX per a Gradle (versió estable recomanada)
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    // Repositori on Gradle buscarà les dependències
    mavenCentral()
}

javafx {
    // Versió de JavaFX
    version = "21.0.8"

    // Mòduls de JavaFX que necessita el programa
    modules(
        "javafx.base",
        "javafx.graphics",
        "javafx.controls",
        "javafx.fxml"
    )
}

application {
    // Ruta completa de la classe Main dins del paquet com.quizchallenge
    mainClass.set("com.quizchallenge.MainKt")
}

kotlin {
    // Utilitzem Java 21
    jvmToolchain(21)
}

dependencies {
    // Llibreria Gson per gestionar la serialització i deserialització de JSON i fitxers
    implementation("com.google.code.gson:gson:2.10.1")

    // Driver JDBC per connectar amb MariaDB
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
}