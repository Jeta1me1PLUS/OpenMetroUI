import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

android {
    namespace = "io.openmetro.ui.component"
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
    // 组件需要默认的导航图标（返回箭头）。只依赖 core 的宿主不受影响：
    // 图标是 Kotlin 代码而非资源，不增加包体负担。
    api(project(":omui-icons"))
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = providers.gradleProperty("OM_GROUP").get()
                artifactId = "omui-components"
                version = providers.gradleProperty("OM_VERSION").get()
                pom {
                    name.set("OpenMetroUI Components")
                    description.set("Metro × Swiss 设计语言的 37 个组件")
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
