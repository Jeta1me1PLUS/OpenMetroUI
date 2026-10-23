pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 发布产物仓库：dist/maven 由 ./gradlew publishToDist 生成
        maven { url = uri(rootDir.resolve("dist/maven")) }
    }
}

rootProject.name = "OpenMetroUI"

include(":omui-core")
include(":omui-components")
include(":omui-icons")
include(":omui-test")
include(":sample")

// 只在发布产物存在时才纳入：这个模块故意只用 Maven 坐标依赖本库，
// 用来验证 dist 里的产物真的能被外部项目消费（不是靠 project 依赖蒙混过关）。
if (file("dist/maven").exists()) {
    include(":smoke")
}
