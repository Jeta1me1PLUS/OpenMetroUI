import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    // AGP 9 内置 Kotlin，无需 org.jetbrains.kotlin.android；Compose 编译器仍需该插件。
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
    `signing`
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

// Maven Central 要求每个 artifact 都带 javadoc。Android 库没有 javadoc，
// 用一个空的 jar 占位，Portal 校验只检查存在性。
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
            // Central Portal 需要上传一个仓库 bundle，所以先生成到本地目录
            maven {
                name = "centralRepo"
                url = uri(rootProject.layout.buildDirectory.dir("central-repo"))
            }
        }
    }

    // 没有密钥时就跳过签名，本地构建与 dist 发布照常
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
