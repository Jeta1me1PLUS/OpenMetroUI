# 05 · 发布指南

坐标：`io.github.jeta1me1plus:omui-core / omui-components / omui-icons / omui-test`

分三层，按对外程度递增：

| 层 | 谁能用 | 代价 |
|---|---|---|
| 本地（dist / mavenLocal） | 只有你 | 零 |
| GitHub Packages | 有 token 的人 | 一个 PAT |
| Maven Central | 所有人，`mavenCentral()` 零配置 | GPG + Portal 账号 |

---

## 一、自己用

### （一）mavenLocal（推荐）

```bash
cd path/to/OpenMetroUI
./gradlew publishToMavenLocal
```

宿主项目 `settings.gradle.kts` 加 `mavenLocal()`，依赖 `io.github.jeta1me1plus:omui-components:<版本>`。

**三个坑**：

1. **版本号就是缓存键**。改了代码没改 `OM_VERSION`，Gradle 会直接用 `~/.m2` 里的旧包，表现为「改了没生效」。
2. `mavenLocal()` 是隐式仓库，会导致「本机能编、别人编不过」。
3. 清理：`rm -rf ~/.m2/repository/io/github/jeta1me1plus`。

### （二）dist（换机器、发给别人）

```bash
./gradlew publishToDist
```

产物在 `dist/`，整个目录可以压缩发人。

---

## 二、GitHub Packages（第一阶段）

### （一）先把仓库推上去

本机没有 `gh` CLI，两种做法：

**网页**：在 github.com 新建仓库 `OpenMetroUI`（public），然后：

```bash
cd path/to/OpenMetroUI
git remote add origin https://github.com/Jeta1me1PLUS/OpenMetroUI.git
git push -u origin main --tags
```

**装 gh 之后**：

```bash
brew install gh && gh auth login
gh repo create Jeta1me1PLUS/OpenMetroUI --public --source=. --push
git push --tags
```

> `dist/maven` 在 `.gitignore` 里，不会被推上去 —— 产物应该在 CI 或本地重新生成，不该进版本库。

### （二）配凭证（本地发布）

在 `~/.gradle/gradle.properties`（**不是项目里的那个**）加：

```properties
gpr.user=Jeta1me1PLUS
gpr.key=<你的 PAT，需要 write:packages 权限>
```

PAT 在 GitHub → Settings → Developer settings → Tokens (classic) 生成，勾 `write:packages` 与 `read:packages`。

### （三）发布

```bash
./gradlew publishToGithub
```

### （四）CI 自动发布

`.github/workflows/publish.yml` 已配好：**打 `v*` 标签时自动跑测试并发布**，也可以在 Actions 页面手动触发。

```bash
git tag -a v0.10.9 -m "v0.10.9"
git push origin v0.10.9
```

### （五）别人怎么依赖

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Jeta1me1PLUS/OpenMetroUI")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}
```

**注意**：GitHub Packages 即使包是 public，**拉取也要求 token**。这是它的硬限制，也是它不适合做「开源库分发」的原因。

---

## 三、Maven Central（面向所有人）

只有这里能做到「别人写一行 `mavenCentral()` 就能用」。四件前置：

| # | 事项 | 现状 | 怎么补 |
|---|---|---|---|
| 1 | **groupId 所有权** | `io.github.jeta1me1plus`，可通过 GitHub 验证 | Portal 里用 GitHub 账号验证 `io.github.` 命名空间（不需要域名） |
| 2 | **许可证** | Apache-2.0，已写入 POM | 确认是否用这个；改 `gradle.properties` 的 `OM_LICENSE_*` |
| 3 | **GPG 签名密钥** | 无 | `gpg --gen-key`，导出 ASCII-armored 私钥 |
| 4 | **Central Portal 账号** | 无 | 注册 central.sonatype.com，验证命名空间 |

### 拿到之后的步骤

**1. 密钥进 `~/.gradle/gradle.properties`**（不是项目文件）：

```properties
signingKey=<ASCII-armored 私钥，单行>
signingPassword=<口令>
centralUser=<Portal 用户名>
centralToken=<Portal token>
```

**2. 四个模块补 `signing`**：

```kotlin
plugins {
    `maven-publish`
    `signing`
}

signing {
    val key = providers.gradleProperty("signingKey").orNull
    val password = providers.gradleProperty("signingPassword").orNull
    if (key != null && password != null) {
        useInMemoryPgpKeys(key, password)
        sign(the<PublishingExtension>().publications["release"])
    }
}
```

写成「有密钥才签名」，没配密钥的机器照样能本地构建。

**3. 加 Central 仓库**：

```kotlin
repositories {
    maven {
        name = "central"
        url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        credentials {
            username = providers.gradleProperty("centralUser").orNull
            password = providers.gradleProperty("centralToken").orNull
        }
    }
}
```

**4. 发布并 release**：

```bash
./gradlew publish
```

然后在 Portal 上把四个组件**一起** release —— 分开发布会出现某个版本只上了一半。

---

## 四、每次发布前的检查清单

- [ ] `./build.sh --lint` 通过（21 个单测、5 个模块 lint 0 issue、零编译告警）
- [ ] `./gradlew publishToDist` 后 `./gradlew :smoke:assembleDebug` 通过（**必须分两步**）
- [ ] `OM_VERSION` 已按规则递增
- [ ] `CHANGELOG.md` 写了本版
- [ ] `04-progress.md` 的当前状态与版本历史已更新
- [ ] 提交 + 打标签，tag 名与 `OM_VERSION` 一致

## 五、版本号规则

| 变更 | 位置 | 是否推进进度 |
|---|---|---|
| 新增需求（一个算一个） | MINOR | 是 |
| 缺陷与调整（**按条数**递增） | PATCH | 否 |
| 文档、构建脚本、坐标迁移 | 不动 | 否 |

例：在 `0.10.5` 上修了 3 条 → `0.10.8`。
