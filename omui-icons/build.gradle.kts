import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
    `signing`
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
            maven {
                name = "centralRepo"
                url = uri(rootProject.layout.buildDirectory.dir("central-repo"))
            }
        }
    }

    signing {
        val key = providers.gradleProperty("signingKey").orNull
        val password = providers.gradleProperty("signingPassword").orNull
        val useGpg = providers.gradleProperty("signingUseGpgCmd").orNull == "true"
        when {
            // CI 用：把私钥塞进属性，不依赖本机 gpg
            key != null && password != null -> {
                useInMemoryPgpKeys(key, password)
                sign(publishing.publications["release"])
            }
            // 本地用：直接调 gpg，口令由 gpg-agent 弹窗，私钥不出 ~/.gnupg
            useGpg -> {
                useGpgCmd()
                sign(publishing.publications["release"])
            }
            else -> Unit
        }
    }
}
