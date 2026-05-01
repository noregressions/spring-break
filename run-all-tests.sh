#!/bin/bash
# run-all-tests.sh
# Run Maven tests across Spring Boot migration test modules, organised by
# the type of breakage they demonstrate.
#
# Usage:
#   ./run-all-tests.sh                        # 3.5.14 baseline — every module should pass
#   ./run-all-tests.sh -v 4.0.6              # 4.0 target  — every module should FAIL
#   ./run-all-tests.sh -q                    # quiet mode  (summary + failures only)
#   ./run-all-tests.sh module1 module2       # run a subset
#   ./run-all-tests.sh -v 4.0.2 -q          # combine flags
#
# Tiers:
#   (1) Won't Build       — source/deps that exist on 3.5 are missing on 4.0
#   (2) Won't Run         — compiles on both versions, throws at runtime on 4.0
#   (3) Different Results — runs on both, but produces different output on 4.0

set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

QUIET=0
BOOT_VERSION=""
ARGS=()

while [ $# -gt 0 ]; do
  case "$1" in
    -q|--quiet) QUIET=1; shift ;;
    -v|--version)
      shift
      BOOT_VERSION="${1:-}"
      [ -z "$BOOT_VERSION" ] && { echo "ERROR: -v requires a version (e.g. -v 4.0.2)" >&2; exit 1; }
      shift
      ;;
    -h|--help)
      sed -n '2,16p' "$0"
      exit 0
      ;;
    *) ARGS+=("$1"); shift ;;
  esac
done

if ! command -v mvn >/dev/null 2>&1; then
  echo "ERROR: mvn not found on PATH. Install Maven and retry." >&2
  exit 127
fi

# ── Tier definitions ──────────────────────────────────────────────────
#
# Tier 1 — Won't Build: real imports / dependencies that vanish on 4.0.
#     On 4.0 the script runs "mvn compile" and EXPECTS failure.
TIER_1=(
  jackson-group-id
  jackson-class-renames
  undertow-removed
  security-removed-apis
  deprecated-classes-removed
  hibernate-session-delete
  hibernate-cascade-removal
  testcontainers-class-relocation
  spring-retry-removed
  hibernate-processor-rename
  listenable-future-removed
  okhttp3-removed
  spring-jcl-removed
  aspectj-observed
  retry-semantics-change
  retryable-transaction-order
  path-matching-engine
  jackson-property-inclusion
  bootstrap-registry-relocated
  testrest-template-removed
  httpheaders-multivaluemap
  elasticsearch-rest5client
  entityscan-relocated
  propertymapping-relocated
  kafka-streams-customizer-removed
  aop-starter-rename
  simpdest-message-matcher-removed
  apacheds-ldap-removed
  spring-security-access-relocated
  propertymapper-alwaysapplyingnonnull
  hibernate-query-setorder-removed
  hibernate-empty-interceptor-removed
  hibernate-where-orderby-removed
  batch-job-builder-string-constructor
  batch-package-moves
  batch-chunkhandler-renamed
  batch-listener-classes
  webjars-locator-core-removed
)

# Tier 2 — Won't Run: compiles on both versions, but throws at runtime on 4.0.
TIER_2=(
  hibernate-query-type-required
  jackson-exception-hierarchy
  hibernate-dialect-removal
  oauth-password-grant-removed
  mockbean-removed
  batch-schema-change
  batch-job-serialisation
  pkce-mandatory
  opensaml4-removed
  resttemplate-autoconfig
  test-slice-relocated
  mockito-test-execution-listener
  javax-annotation-removed
  javax-inject-removed
  actuator-nullable-removed
)

# Tier 3 — Different Results: runs on both, assertions detect different behaviour.
TIER_3=(
  jackson-date-serialisation
  jackson-dates-timestamps
  jackson-locale-format
  hibernate-native-datetime
)

# Portable tier lookup (works on bash 3.x / macOS)
module_tier() {
  local mod="$1"
  for m in "${TIER_1[@]}"; do [ "$m" = "$mod" ] && echo "1" && return; done
  for m in "${TIER_2[@]}"; do [ "$m" = "$mod" ] && echo "2" && return; done
  for m in "${TIER_3[@]}"; do [ "$m" = "$mod" ] && echo "3" && return; done
  echo "?"
}

# Detect whether target version is 4.x
IS_4X=0
if [[ -n "$BOOT_VERSION" && "$BOOT_VERSION" == 4* ]]; then
  IS_4X=1
fi

# Module list — explicit args override the defaults
if [ ${#ARGS[@]} -gt 0 ]; then
  MODULES=("${ARGS[@]}")
else
  MODULES=("${TIER_1[@]}" "${TIER_2[@]}" "${TIER_3[@]}")
fi

if [ ${#MODULES[@]} -eq 0 ]; then
  echo "No modules to run." >&2
  exit 1
fi

# ── ANSI colours ──────────────────────────────────────────────────────
if [ -t 1 ]; then
  GREEN=$'\033[32m'; RED=$'\033[31m'; YELLOW=$'\033[33m'; CYAN=$'\033[36m'
  BOLD=$'\033[1m'; DIM=$'\033[2m'; RESET=$'\033[0m'
else
  GREEN=""; RED=""; YELLOW=""; CYAN=""; BOLD=""; DIM=""; RESET=""
fi

LOG_DIR="$SCRIPT_DIR/.test-logs"
mkdir -p "$LOG_DIR"

VERSION_FLAG=""
[ -n "$BOOT_VERSION" ] && VERSION_FLAG="-Dspring-boot.version=$BOOT_VERSION"

PASSED=()
FAILED=()
START_TS=$(date +%s)

VERSION_LABEL="${BOOT_VERSION:-3.5.14 (default)}"

# ── Banner ────────────────────────────────────────────────────────────
printf "\n%s━━━ Spring Boot 3.5 → 4.0 Migration Test Suite ━━━%s\n" "$BOLD" "$RESET"
printf "  Spring Boot : %s\n" "$VERSION_LABEL"
printf "  Modules     : %d\n\n" "${#MODULES[@]}"
printf "  %s(1)%s Won't Build      %s(2)%s Won't Run      %s(3)%s Different Results\n" \
  "$CYAN" "$RESET" "$CYAN" "$RESET" "$CYAN" "$RESET"

# ── Run each module ──────────────────────────────────────────────────
current_tier=""

for module in "${MODULES[@]}"; do
  if [ ! -f "$module/pom.xml" ]; then
    printf "  %s[SKIP]%s %s (no pom.xml)\n" "$YELLOW" "$RESET" "$module"
    continue
  fi

  tier="$(module_tier "$module")"

  # Print a tier header when the tier changes
  if [ "$tier" != "$current_tier" ]; then
    current_tier="$tier"
    case "$tier" in
      1) printf "\n%sTier 1 — Won't Build%s\n" "$BOLD" "$RESET" ;;
      2) printf "\n%sTier 2 — Won't Run%s\n" "$BOLD" "$RESET" ;;
      3) printf "\n%sTier 3 — Different Results%s\n" "$BOLD" "$RESET" ;;
      *) printf "\n%sUncategorized%s\n" "$BOLD" "$RESET" ;;
    esac
  fi

  # Tier 1 on 4.x → compile-only (tests won't even compile)
  # BUT some Tier 1 modules might actually fail at test execution (runtime)
  # even if they are Tier 1 because they use reflection or are Tier 2 style.
  # We force Tier 1 to compile only on 4.x to catch Won't Build breaks.
  if [ "$tier" = "1" ] && [ "$IS_4X" -eq 1 ]; then
    if false; then
      # (placeholder — no Tier 1 runtime-only special cases currently)
      GOAL="clean test"
    else
      GOAL="clean compile test-compile"
    fi
  else
    GOAL="clean test"
  fi

  MVN_CMD="mvn -B $GOAL $VERSION_FLAG"

  printf "  [....] %s" "$module"
  log="$LOG_DIR/${module}.log"

  if [ "$QUIET" -eq 1 ]; then
    ( cd "$module" && eval $MVN_CMD ) >"$log" 2>&1
    rc=$?
  else
    ( cd "$module" && eval $MVN_CMD ) 2>&1 | tee "$log" >/dev/null
    rc=${PIPESTATUS[0]}
  fi

  if [ $rc -eq 0 ]; then
    printf "\r  %s[PASS]%s %s\n" "$GREEN" "$RESET" "$module"
    PASSED+=("$module")
  else
    printf "\r  %s[FAIL]%s %s  %s→ .test-logs/%s.log%s\n" "$RED" "$RESET" "$module" "$DIM" "$module" "$RESET"
    FAILED+=("$module")
  fi
done

END_TS=$(date +%s)
ELAPSED=$(( END_TS - START_TS ))

# ── Summary ──────────────────────────────────────────────────────────
printf "\n%s━━━ Summary ━━━%s  [Boot %s]  %ds\n" "$BOLD" "$RESET" "$VERSION_LABEL" "$ELAPSED"
printf "  %sPassed:%s %d    %sFailed:%s %d    Total: %d\n" \
  "$GREEN" "$RESET" "${#PASSED[@]}" \
  "$RED" "$RESET" "${#FAILED[@]}" \
  "$(( ${#PASSED[@]} + ${#FAILED[@]} ))"

if [ ${#FAILED[@]} -gt 0 ]; then
  printf "\n  Failed:\n"
  for m in "${FAILED[@]}"; do
    printf "    %s✗%s (%s) %s\n" "$RED" "$RESET" "$(module_tier "$m")" "$m"
  done
fi

# On 4.x, passing is the surprise — flag it
if [ "$IS_4X" -eq 1 ] && [ ${#PASSED[@]} -gt 0 ]; then
  printf "\n  %s⚠  Unexpected passes on 4.x — these tests may not be catching the break:%s\n" "$YELLOW" "$RESET"
  for m in "${PASSED[@]}"; do
    printf "    %s?%s (%s) %s\n" "$YELLOW" "$RESET" "$(module_tier "$m")" "$m"
  done
fi

printf "\n  Logs: .test-logs/\n\n"

# Exit code:
#   3.x → 0 if all pass, 1 if any fail
#   4.x → 0 if all fail (expected), 1 if any pass unexpectedly
if [ "$IS_4X" -eq 1 ]; then
  [ ${#PASSED[@]} -eq 0 ] && exit 0 || exit 1
else
  [ ${#FAILED[@]} -eq 0 ] && exit 0 || exit 1
fi
