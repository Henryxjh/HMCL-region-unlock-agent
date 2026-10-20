plugins {
    java
}

group = "io.github.henryxjh.hmclregionunlock"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.9")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
}

tasks.jar {
    archiveBaseName.set("hmcl-region-unlock-agent")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Premain-Class" to "io.github.henryxjh.hmclregionunlock.RegionUnlockAgent",
            "Can-Redefine-Classes" to "false",
            "Can-Retransform-Classes" to "false"
        )
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

tasks.register<JavaExec>("integrationTest") {
    dependsOn(tasks.jar, tasks.testClasses)
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("io.github.henryxjh.hmclregionunlock.AgentIntegrationTest")
    jvmArgs("-javaagent:${tasks.jar.get().archiveFile.get().asFile.absolutePath}")
}

tasks.test {
    enabled = false
}

tasks.check {
    dependsOn("integrationTest")
}
