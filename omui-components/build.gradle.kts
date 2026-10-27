import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
    `signing`
}

android {
    namespace = "io.openmetro.ui.component"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    // @Preview 注解只需要编译期可见，不进运行时 classpath
    compileOnly(libs.androidx.compose.ui.tooling.preview)

    // 组件行为测试：跑在设备 / 模拟器上
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
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
                artifactId = "omui-components"
                version = providers.gradleProperty("OM_VERSION").get()
                pom {
                    name.set("OpenMetroUI Components")
                    description.set("Metro × Swiss 设计语言的 38 个组件")
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
