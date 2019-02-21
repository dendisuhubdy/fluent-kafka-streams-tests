buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.bakdata.gradle:sonar:1.1.1")
        classpath("com.bakdata.gradle:sonatype:1.1.1")
    }
}

plugins {
    // release
    id("net.researchgate.release") version "2.6.0"
//    id("com.bakdata.sonar") version "1.0.1"
//    id("com.bakdata.sonatype") version "1.1.1"
    id("org.hildan.github.changelog") version "0.8.0"
}

apply(plugin = "com.bakdata.sonatype")
apply(plugin = "com.bakdata.sonar")

allprojects {
    group = "com.bakdata.${rootProject.name}"

    tasks.withType<Test> {
        maxParallelForks = 4
    }

    repositories {
        mavenCentral()
    }
}

configure<com.bakdata.gradle.SonatypeSettings> {
    developers {
        developer {
            name.set("Arvid Heise")
            id.set("AHeise")
        }
        developer {
            name.set("Lawrence Benson")
            id.set("lawben")
        }
    }
}

configure<org.hildan.github.changelog.plugin.GitHubChangelogExtension> {
    githubUser = "bakdata"
    futureVersionTag = findProperty("changelog.releaseVersion")?.toString()
    sinceTag = findProperty("changelog.sinceTag")?.toString()
}

subprojects {
    apply(plugin = "java-library")
    // build fails for java 11, let"s wait for a newer lombok version
    configure<JavaPluginConvention> {
        sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11
        targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
    }

    tasks.withType<Javadoc> {
        options {
            (this as StandardJavadocDocletOptions).apply {
                addBooleanOption("html5", true)
                stylesheetFile(File("${rootDir}/src/main/javadoc/assertj-javadoc.css"))
                addBooleanOption("-allow-script-in-comments", true)
                header("<script src=\"http://cdn.jsdelivr.net/highlight.js/8.6/highlight.min.js\"></script>")
                footer("<script type=\"text/javascript\">\nhljs.initHighlightingOnLoad();\n</script>")
            }
        }
    }

    dependencies {
        val junitVersion = "5.3.0"
        "implementation"(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
        "testImplementation"(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
        "testRuntimeOnly"(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)

        "testImplementation"(group = "org.slf4j", name = "slf4j-log4j12", version = "1.7.25")
        "testImplementation"(group = "org.assertj", name = "assertj-core", version = "3.11.1")

        "compileOnly"("org.projectlombok:lombok:1.18.6")
        "annotationProcessor"("org.projectlombok:lombok:1.18.6")
        "testCompileOnly"("org.projectlombok:lombok:1.18.6")
        "testAnnotationProcessor"("org.projectlombok:lombok:1.18.6")
    }
}
