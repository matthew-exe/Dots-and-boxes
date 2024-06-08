plugins {
    id("org.jetbrains.kotlin.jvm")
}

val kotlin_version: String by project
val junitJupiterVersion: String by project

dependencies {
    api("io.github.pdvrieze.matrixlib:matrixlib:1.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

kotlin {

}

kotlin.target.compilations.all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
