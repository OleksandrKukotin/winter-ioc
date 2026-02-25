plugins {
    id("java")
}

group = "org.github.oleksandrkukotin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("io.github.classgraph:classgraph:4.8.184")
}

tasks.test {
    useJUnitPlatform()
}