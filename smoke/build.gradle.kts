plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

/**
 * 发布产物的接入验证模块。
 *
 * 它**只用 Maven 坐标**依赖本库（不使用 `project(...)`），所以能真正验证
 * `dist/maven` 里的 AAR、POM、module metadata 是否完整可用。
 *
 * 运行：`./gradlew :smoke:assembleDebug`
 */
android {
    namespace = "io.openmetro.smoke"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.openmetro.smoke"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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
        disable += "OldTargetApi"
    }
}

dependencies {
    // 关键：Maven 坐标，不是 project 依赖
    implementation("io.github.jeta1me1plus:omui-components:${providers.gradleProperty("OM_VERSION").get()}")
    implementation(libs.androidx.activity.compose)
}
