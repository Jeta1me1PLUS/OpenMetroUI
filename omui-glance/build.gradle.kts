import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
    `signing`
}

android {
    namespace = "io.openmetro.ui.glance"
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
    // 小组件必须用 Glance：RemoteViews 那套无法复用 Compose 的布局能力，
    // 而 Glance 在宿主进程里跑，能直接读同一套 token。
    api(libs.androidx.glance.appwidget)
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
                artifactId = "omui-glance"
                version = providers.gradleProperty("OM_VERSION").get()
                pom {
                    name.set("OpenMetroUI Glance")
                    description.set("桌面磁贴小组件：把 Metro 磁贴搬到 App Widget")
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
            key != null && password != null -> {
                useInMemoryPgpKeys(key, password)
                sign(publishing.publications["release"])
            }
            useGpg -> {
                useGpgCmd()
                sign(publishing.publications["release"])
            }
            else -> Unit
        }
    }
}
