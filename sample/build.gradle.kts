plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.openmetro.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.openmetro.sample"
        minSdk = 24
        targetSdk = 36
        // MINOR * 100 + PATCH：0.11.0 -> 1100。
        // 旧规则 MINOR + PATCH 在 0.11.0 会算出 11，小于 0.10.10 的 20，不单调。
        versionCode = 1407
        // 与三个 library 模块共用 gradle.properties 里的唯一真源。
        versionName = providers.gradleProperty("OM_VERSION").get()
    }

    buildTypes {
        release {
            // 仅用于本机性能对比（debug 构建慢 2–3 倍，不能用来判断卡顿）：
            // 用 debug 签名，不混淆，产物可直接安装。
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        // 设置页显示版本号，直接读 BuildConfig，避免手写字面量过期
        buildConfig = true
    }

    lint {
        disable += "AndroidGradlePluginVersion"
        disable += "GradleDependency"
        disable += "NewerVersionAvailable"
        // targetSdk 不能高于 compileSdk，而 compileSdk 被 AGP 9.1.0 卡在 36。
        disable += "OldTargetApi"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)

    implementation(project(":omui-core"))
    implementation(project(":omui-components"))
    implementation(project(":omui-icons"))

    debugImplementation(libs.androidx.compose.ui.tooling)
}
