plugins {
    java
}

group = "org.jackhuang.hmcl"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.9")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.jar {
    archiveBaseName.set("hmcl-region-unlock-agent")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Premain-Class" to "org.jackhuang.hmcl.agent.RegionUnlockAgent",
            "Can-Redefine-Classes" to "false",
            "Can-Retransform-Classes" to "false"
        )
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

tasks.register<JavaExec>("integrationTest") {
    dependsOn(tasks.jar, tasks.testClasses)
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("org.jackhuang.hmcl.agent.AgentIntegrationTest")
    jvmArgs("-javaagent:${tasks.jar.get().archiveFile.get().asFile.absolutePath}")
}

tasks.test {
    enabled = false
}

tasks.check {
    dependsOn("integrationTest")
}
