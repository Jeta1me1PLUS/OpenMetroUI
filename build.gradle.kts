import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    // AGP 9 内置 Kotlin，不再应用 org.jetbrains.kotlin.android；
    // 但 Compose 编译器仍需该插件，且版本必须与内置 Kotlin 对齐。
    alias(libs.plugins.kotlin.compose) apply false
}

/**
 * 把所有模块的发布产物汇总到 `dist/maven`。
 *
 * 宿主不必依赖 mavenLocal：把 `dist` 整个目录拷走，在 settings 里指向
 * `dist/maven` 即可解析到全部坐标。说明见 `dist/README.md`。
 */
tasks.register("publishToDist") {
    group = "publishing"
    description = "把四个模块发布到 dist/maven（可直接拷走当本地仓库）"
    dependsOn(
        ":omui-core:publishAllPublicationsToDistRepository",
        ":omui-icons:publishAllPublicationsToDistRepository",
        ":omui-components:publishAllPublicationsToDistRepository",
        ":omui-test:publishAllPublicationsToDistRepository",
    )
}

/**
 * 统一的 POM 元数据。
 *
 * Maven Central 要求 `url` / `licenses` / `developers` / `scm` 四项齐全，
 * 缺任何一项都会被 Portal 拒收。各模块只负责自己的 name 与 description，
 * 公共字段在这里补，避免四个模块各写一遍还写得不一致。
 */
subprojects {
    plugins.withId("maven-publish") {
        afterEvaluate {
            extensions.configure<PublishingExtension> {
                publications.withType<MavenPublication>().configureEach {
                    pom {
                        url.set(providers.gradleProperty("OM_REPO_URL").get())

                        licenses {
                            license {
                                name.set(providers.gradleProperty("OM_LICENSE_NAME").get())
                                url.set(providers.gradleProperty("OM_LICENSE_URL").get())
                                distribution.set("repo")
                            }
                        }

                        developers {
                            developer {
                                id.set(providers.gradleProperty("OM_DEVELOPER_ID").get())
                                name.set(providers.gradleProperty("OM_DEVELOPER_NAME").get())
                            }
                        }

                        scm {
                            val repo = providers.gradleProperty("OM_REPO_URL").get()
                            url.set(repo)
                            connection.set("scm:git:$repo.git")
                            developerConnection.set("scm:git:$repo.git")
                        }
                    }
                }

                // GitHub Packages：只在拿到凭证时挂上仓库，否则本地构建会被卡在配置阶段。
                // 本地用 ~/.gradle/gradle.properties 的 gpr.user / gpr.key；
                // CI 里用 GITHUB_ACTOR / GITHUB_TOKEN。
                val ghUser = System.getenv("GITHUB_ACTOR")
                    ?: providers.gradleProperty("gpr.user").orNull
                val ghToken = System.getenv("GITHUB_TOKEN")
                    ?: providers.gradleProperty("gpr.key").orNull
                if (ghUser != null && ghToken != null) {
                    repositories {
                        maven {
                            name = "github"
                            url = uri(
                                "https://maven.pkg.github.com/" +
                                    providers.gradleProperty("OM_GITHUB_REPO").get(),
                            )
                            credentials {
                                username = ghUser
                                password = ghToken
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 一次发布全部模块到 GitHub Packages。
 * 用法：`./gradlew publishToGithub`（需要 gpr.user / gpr.key 或 CI 的 GITHUB_TOKEN）
 */
tasks.register("publishToGithub") {
    group = "publishing"
    description = "把四个模块发布到 GitHub Packages"

    val hasCredentials = !System.getenv("GITHUB_TOKEN").isNullOrBlank() ||
        providers.gradleProperty("gpr.key").orNull != null

    if (hasCredentials) {
        dependsOn(
            ":omui-core:publishAllPublicationsToGithubRepository",
            ":omui-icons:publishAllPublicationsToGithubRepository",
            ":omui-components:publishAllPublicationsToGithubRepository",
            ":omui-test:publishAllPublicationsToGithubRepository",
        )
    } else {
        // 没有凭证时不挂任务，直接给出可读的提示，而不是 "Task not found"
        doFirst {
            throw GradleException(
                "缺少 GitHub Packages 凭证。请在 ~/.gradle/gradle.properties 配置：\n" +
                    "  gpr.user=<GitHub 用户名>\n" +
                    "  gpr.key=<带 write:packages 权限的 PAT>",
            )
        }
    }
}

/**
 * 生成带 GPG 签名的 Maven Central 仓库。
 *
 * Central Portal 不接受直接推送，需要上传一个仓库 bundle，所以先生成到
 * `build/central-repo`，再由 `bundleForCentral` 打成 zip。
 * 没有配置 signingKey / signingPassword 时不签名，这个任务只用于本地验证。
 */
tasks.register("publishToCentral") {
    group = "publishing"
    description = "生成 Maven Central 仓库（build/central-repo，有密钥时带签名）"
    dependsOn(
        ":omui-core:publishAllPublicationsToCentralRepoRepository",
        ":omui-icons:publishAllPublicationsToCentralRepoRepository",
        ":omui-components:publishAllPublicationsToCentralRepoRepository",
        ":omui-test:publishAllPublicationsToCentralRepoRepository",
    )
}

tasks.register<org.gradle.api.tasks.bundling.Zip>("bundleForCentral") {
    group = "publishing"
    description = "把 build/central-repo 打成可上传到 Central Portal 的 zip"
    dependsOn("publishToCentral")
    val currentVersion = providers.gradleProperty("OM_VERSION").get()
    archiveFileName.set("openmetroui-$currentVersion.zip")
    destinationDirectory.set(layout.buildDirectory.dir("central-bundle"))
    // central-repo 会累积所有历史版本，这里只打包当前版本，
    // 否则 bundle 会越来越大，也容易把已发布的旧版本重复提交
    from(layout.buildDirectory.dir("central-repo")) {
        include("io/github/jeta1me1plus/*/$currentVersion/**")
    }
}
