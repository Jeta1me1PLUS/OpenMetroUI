#!/usr/bin/env bash
#
# OpenMetroUI 一键编译 demo
#
# 做四件事：从 gradle.properties 读版本号 → 跑单元测试 → 编译 sample → 归档并按
# 「只保留最新一版」删旧包。
#
# 关键设计：**任一步失败立即中止，并且不删除任何旧包。**
# 删旧包只能发生在「新包已构建并校验过」之后 —— 宁可留下多余文件，也不能在一次
# 失败的构建后把上一次可用的产物删掉。
#
# 用法见 ./build.sh --help。
#
# ⚠️ 改这个脚本时唯一必须守住的规则：**变量引用一律加花括号**，写 ${VAR} 而不是 $VAR。
# 理由：中文文案里变量后常紧跟全角字符（如「版本 ${VERSION_NAME}（…）」），bash 3.2
# （macOS 自带）在 UTF-8 locale 下会把全角字符的首字节当成标识符的一部分吞掉，
# 于是去找一个不存在的变量并直接报 unbound variable。同一脚本在 LC_CTYPE=C 下却正常，
# 所以「在谁机器上能跑」取决于终端 locale 而不是代码本身。

set -euo pipefail

cd "$(dirname "$0")"

readonly PROPERTIES_FILE="gradle.properties"
readonly MAIN_ACTIVITY="io.openmetro.sample/.MainActivity"
readonly LOG_FILE="build.log"

if [ -t 1 ]; then
  readonly C_BLUE=$'\033[34m' C_GREEN=$'\033[32m' C_YELLOW=$'\033[33m'
  readonly C_RED=$'\033[31m' C_DIM=$'\033[2m' C_OFF=$'\033[0m'
else
  readonly C_BLUE='' C_GREEN='' C_YELLOW='' C_RED='' C_DIM='' C_OFF=''
fi

step() { printf '%s==>%s %s\n' "${C_BLUE}" "${C_OFF}" "${*}"; }
note() { printf '    %s\n' "${*}"; }
warn() { printf '%s警告:%s %s\n' "${C_YELLOW}" "${C_OFF}" "${*}" >&2; }

die() {
  printf '%s失败:%s %s\n' "${C_RED}" "${C_OFF}" "${*}" >&2
  exit 1
}

usage() {
  cat <<'EOF'
OpenMetroUI 一键编译 demo

用法： ./build.sh [选项]

  （无选项）   跑单元测试 → 编译 debug → 归档为 OpenMetroUI-v<版本>-debug.apk → 删除更早的包
  --release    编译 release（debug 签名、不混淆）。判断卡顿是否只是 debug 构建时用
  --clean      先执行 gradlew clean 再构建（怀疑增量构建漏编时用）
  --lint       同时跑各模块的 lint（慢十几秒，但能发现新增问题）
  --install    构建完成后用 adb 安装并启动 demo（需要已连接设备或模拟器）
  -h, --help   显示本说明

版本号从 gradle.properties 的 OM_VERSION 读取，不需要手填。
产物归档在项目根目录，只保留最新一版；完整日志写入 build.log。
EOF
}

CLEAN=0
RUN_LINT=0
INSTALL=0
BUILD_TYPE="debug"
while [ ${#} -gt 0 ]; do
  case "${1}" in
    --release) BUILD_TYPE="release" ;;
    --clean) CLEAN=1 ;;
    --lint) RUN_LINT=1 ;;
    --install) INSTALL=1 ;;
    -h | --help)
      usage
      exit 0
      ;;
    *)
      printf '未知参数：%s\n\n' "${1}" >&2
      usage >&2
      exit 2
      ;;
  esac
  shift
done

# ── 构建类型 ────────────────────────────────────────────────────
if [ "${BUILD_TYPE}" = "release" ]; then
  readonly BUILT_APK="sample/build/outputs/apk/release/sample-release.apk"
  readonly ASSEMBLE_TASK=":sample:assembleRelease"
else
  readonly BUILT_APK="sample/build/outputs/apk/debug/sample-debug.apk"
  readonly ASSEMBLE_TASK=":sample:assembleDebug"
fi

# ── 前置检查 ────────────────────────────────────────────────────
[ -x ./gradlew ] || die "找不到可执行的 ./gradlew。请先执行 chmod +x gradlew"
[ -f "${PROPERTIES_FILE}" ] || die "找不到 ${PROPERTIES_FILE} —— 这个目录看起来不是 OpenMetroUI 项目"

if [ ! -f local.properties ] && [ -z "${ANDROID_HOME:-}" ] && [ -z "${ANDROID_SDK_ROOT:-}" ]; then
  warn "local.properties 不存在，也没有 ANDROID_HOME / ANDROID_SDK_ROOT"
  warn "Gradle 很可能找不到 Android SDK，构建会失败"
fi

# ── 读版本号 ────────────────────────────────────────────────────
# 用 awk 而不是 `sed | head -1`：BSD sed 不认 GNU 扩展且静默匹配不到；
# pipefail 下 head 提前退出还会让上游收到 SIGPIPE 把脚本带崩。
VERSION_NAME=$(awk -F'=' '/^[[:space:]]*OM_VERSION[[:space:]]*=/ { gsub(/[[:space:]]/, "", $2); print $2; exit }' "${PROPERTIES_FILE}")
[ -n "${VERSION_NAME}" ] || die "没能从 ${PROPERTIES_FILE} 里读到 OM_VERSION"

ARCHIVE="OpenMetroUI-v${VERSION_NAME}-${BUILD_TYPE}.apk"
step "版本 ${VERSION_NAME}（${BUILD_TYPE}）"

# ── 构建 ────────────────────────────────────────────────────────
if [ "${CLEAN}" = 1 ]; then
  step "清理构建产物"
  ./gradlew clean --console=plain >>"${LOG_FILE}" 2>&1 || die "clean 失败，详见 ${LOG_FILE}"
fi

if [ "${RUN_LINT}" = 1 ]; then
  TASKS=":omui-core:testDebugUnitTest :omui-test:testDebugUnitTest :omui-core:lintDebug :omui-components:lintDebug :omui-icons:lintDebug :omui-test:lintDebug :sample:lintDebug ${ASSEMBLE_TASK}"
  step "单元测试 + lint + 编译中（日志：${LOG_FILE}）"
else
  TASKS=":omui-core:testDebugUnitTest :omui-test:testDebugUnitTest ${ASSEMBLE_TASK}"
  step "单元测试 + 编译中（日志：${LOG_FILE}）"
fi

# shellcheck disable=SC2086  # TASKS 需要按空格拆成多个任务名；加引号会被当成单个任务
if ! ./gradlew ${TASKS} --console=plain >"${LOG_FILE}" 2>&1; then
  echo
  grep -E "^e: |^w: |error:|warning:|FAILED|tests completed|Lint found" "${LOG_FILE}" | head -40 || true
  echo
  die "构建失败（编译 / 单元测试 / lint 未通过），详见 ${LOG_FILE}。旧包未做任何改动"
fi

[ -f "${BUILT_APK}" ] || die "编译报告成功，但找不到 ${BUILT_APK}。旧包未做任何改动"

# 编译告警单独提示：Gradle 成功不代表零告警，这是本项目的交付标准之一
WARNINGS=$(grep -cE "^w: " "${LOG_FILE}" || true)
if [ "${WARNINGS}" -gt 0 ]; then
  warn "编译有 ${WARNINGS} 条告警："
  grep -E "^w: " "${LOG_FILE}" | head -10 >&2
fi

# ── 归档 ────────────────────────────────────────────────────────
step "归档为 ${ARCHIVE}"
cp "${BUILT_APK}" "${ARCHIVE}"

if command -v md5 >/dev/null 2>&1; then
  SUM_BUILT=$(md5 -q "${BUILT_APK}")
  SUM_ARCHIVE=$(md5 -q "${ARCHIVE}")
elif command -v md5sum >/dev/null 2>&1; then
  SUM_BUILT=$(md5sum "${BUILT_APK}" | cut -d' ' -f1)
  SUM_ARCHIVE=$(md5sum "${ARCHIVE}" | cut -d' ' -f1)
else
  SUM_BUILT=''
  SUM_ARCHIVE=''
  warn "系统里没有 md5 / md5sum，跳过归档校验"
fi

if [ -n "${SUM_BUILT}" ] && [ "${SUM_BUILT}" != "${SUM_ARCHIVE}" ]; then
  rm -f "${ARCHIVE}"
  die "归档校验失败：${ARCHIVE} 与构建产物不一致，已删除该文件"
fi

# ── 只保留最新一版 ──────────────────────────────────────────────
# 只动符合产物命名规则的文件，不碰根目录里其他任何东西。
pruned=0
for old in OpenMetroUI-v*"-${BUILD_TYPE}.apk"; do
  [ -e "${old}" ] || continue
  [ "${old}" = "${ARCHIVE}" ] && continue
  rm -f "${old}"
  note "已删除 ${old}"
  pruned=$((pruned + 1))
done

# ── 可选安装 ────────────────────────────────────────────────────
if [ "${INSTALL}" = 1 ]; then
  ADB="${ANDROID_HOME:-${HOME}/Library/Android/sdk}/platform-tools/adb"
  if [ ! -x "${ADB}" ]; then
    warn "找不到 adb（${ADB}），跳过安装"
  else
    step "安装并启动 demo"
    "${ADB}" install -r "${ARCHIVE}" >/dev/null || die "adb install 失败"
    "${ADB}" shell am start -n "${MAIN_ACTIVITY}" >/dev/null || die "启动 Activity 失败"
    note "已在设备上启动"
  fi
fi

# ── 结果 ────────────────────────────────────────────────────────
BUILD_LINE=$(grep -E "^BUILD (SUCCESSFUL|FAILED)" "${LOG_FILE}" | tail -1 || true)

printf '\n%s构建完成%s\n' "${C_GREEN}" "${C_OFF}"
note "产物    ${ARCHIVE}（$(du -h "${ARCHIVE}" | cut -f1 | tr -d '[:space:]')）"
note "版本    ${VERSION_NAME}"
if [ "${pruned}" -gt 0 ]; then
  note "旧包    已删除 ${pruned} 个，根目录只留最新一版"
fi
if [ -n "${BUILD_LINE}" ]; then
  note "Gradle  ${BUILD_LINE}"
fi
note "日志    ${LOG_FILE}"
