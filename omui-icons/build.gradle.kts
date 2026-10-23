import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

android {
    namespace = "io.openmetro.ui.icon"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    lint {
        // 同 omui-core：版本天花板导致的固定告警。
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
    api(project(":omui-core"))
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = providers.gradleProperty("OM_GROUP").get()
                artifactId = "omui-icons"
                version = providers.gradleProperty("OM_VERSION").get()
                pom {
                    name.set("OpenMetroUI Icons")
                    description.set("24dp / 1.5dp 描边、方头端点的几何图标集")
                }
            }
        }

        repositories {
            maven {
                name = "dist"
                url = uri(rootProject.file("dist/maven"))
            }
        }
    }
}
