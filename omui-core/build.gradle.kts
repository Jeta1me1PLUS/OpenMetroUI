import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    // AGP 9 内置 Kotlin，无需 org.jetbrains.kotlin.android；Compose 编译器仍需该插件。
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

android {
    namespace = "io.openmetro.ui.core"
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
        // 版本天花板：见 gradle/libs.versions.toml 的注释。AGP 9.1.0 最高支持
        // compileSdk 36，一批依赖必须停在旧版本上。这几条检查只会反复提醒
        // 一件已经知道的事，不是漏更新。
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
    // 组件的公开签名会暴露 Dp / Color / TextStyle，所以这些依赖用 api 导出。
    // 每个都带显式版本：发布出去的 POM 里若缺版本，宿主解析会失败。
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.text)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.animation)

    // 只测纯逻辑：对比度计算与栅格列宽。不引 Robolectric。
    testImplementation(libs.junit)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = providers.gradleProperty("OM_GROUP").get()
                artifactId = "omui-core"
                version = providers.gradleProperty("OM_VERSION").get()
                pom {
                    name.set("OpenMetroUI Core")
                    description.set("Metro × Swiss 设计语言的 token 层、交互原语与布局原语")
                }
            }
        }

        // 产物落在项目内的 dist/maven，宿主可以直接把 dist 拷走当本地仓库用
        repositories {
            maven {
                name = "dist"
                url = uri(rootProject.file("dist/maven"))
            }
        }
    }
}
