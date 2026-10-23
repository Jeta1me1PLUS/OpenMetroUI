import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
    `signing`
}

android {
    namespace = "io.openmetro.ui.test"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        disable += "AndroidGradlePluginVersion"
        disable += "GradleDependency"
        disable += "NewerVersionAvailable"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    // 使用方的单元测试需要这些断言，故用 api 暴露
    api(project(":omui-core"))
    api(libs.junit)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(layout.buildDirectory.dir("empty-javadoc"))
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                artifact(javadocJar)
                groupId = providers.gradleProperty("OM_GROUP").get()
                artifactId = "omui-test"
                version = providers.gradleProperty("OM_VERSION").get()
                pom {
                    name.set("OpenMetroUI Test")
                    description.set("设计约束的自动校验：对比度、4dp 网格、触摸目标、字阶与行高")
                }
            }
        }

        repositories {
            maven {
                name = "dist"
                url = uri(rootProject.file("dist/maven"))
            }
            maven {
                name = "centralRepo"
                url = uri(rootProject.layout.buildDirectory.dir("central-repo"))
            }
        }
    }

    signing {
        val key = providers.gradleProperty("signingKey").orNull
        val password = providers.gradleProperty("signingPassword").orNull
        if (key != null && password != null) {
            useInMemoryPgpKeys(key, password)
            sign(publishing.publications["release"])
        }
    }
}
