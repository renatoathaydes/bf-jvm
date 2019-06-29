import org.gradle.jvm.tasks.Jar

buildscript {
    repositories {
        mavenLocal()
        jcenter()
    }
}

plugins {
    kotlin("jvm") version "1.3.40"
}

repositories {
    mavenLocal()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testCompile("junit:junit:4.12")
}

val helloFile = "src/test/resources/hello.bf"

task("runJava", JavaExec::class) {
    main = if (project.hasProperty("original")) "bf" else "Bf"
    args = listOf(
            project.properties["file"]?.toString() ?: helloFile,
            project.properties.getOrDefault("count", "4").toString())
    classpath = sourceSets["main"]!!.runtimeClasspath
}

task("runKotlin", JavaExec::class) {
    main = "BfKotlin"
    args = listOf(project.properties["file"]?.toString() ?: helloFile,
            project.properties.getOrDefault("count", "4").toString())
    classpath = sourceSets["main"]!!.runtimeClasspath
}

task("printKotlinClasspath") {
    doLast {
        val jarFiles = setOf(tasks.withType<Jar>().first().archiveFile.get().asFile)
        println((jarFiles + configurations.runtimeClasspath.get().files).joinToString(File.pathSeparator))
    }
}
