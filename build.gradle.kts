plugins {
    kotlin("jvm") version "2.0.20"

    application
}

group = "lol.dap.asgard"
version = "1.0-SNAPSHOT"

val ktorVersion = "2.3.7"

application {
    mainClass.set("lol.dap.asgard.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.3")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation(kotlin("reflect"))

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")

    implementation("com.github.luben:zstd-jni:1.5.6-3")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "lol.dap.asgard.MainKt"
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        configurations.compileClasspath.get().forEach {
            from(if (it.isDirectory) it else zipTree(it))
        }
    }
}

kotlin {
    jvmToolchain(21)
}