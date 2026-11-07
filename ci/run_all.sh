#!/usr/bin/env bash
# Прогоны семинаров 7 и 8 в GitHub Actions (ubuntu-latest, Chrome под Xvfb).
# Результаты складываются в results/ и коммитятся workflow обратно в репозиторий.
set -u

ROOT="$(pwd)"
OUT="$ROOT/results"
rm -rf "$OUT"
mkdir -p "$OUT"
SUMMARY="$OUT/summary.txt"

{
  echo "Дата прогона (UTC): $(date -u '+%Y-%m-%d %H:%M:%S')"
  echo "Runner: $(lsb_release -ds 2>/dev/null)"
  echo "Java: $(java -version 2>&1 | head -1)"
  echo "Maven: $(mvn -v 2>/dev/null | head -1)"
  echo "Chrome: $(google-chrome --version 2>/dev/null)"
  echo
} > "$SUMMARY"

# run_suite <каталог проекта> <метка> <число прогонов>
run_suite() {
  local dir="$1" label="$2" runs="$3"
  echo "== $label ($dir), прогонов: $runs" >> "$SUMMARY"
  for i in $(seq 1 "$runs"); do
    local log="$OUT/${label}_run${i}.log"
    (cd "$dir" && rm -rf target/surefire-reports && xvfb-run -a mvn -B test > "$log" 2>&1)
    local code=$?
    local line
    line=$(grep -E "Tests run: [0-9]+, Failures: [0-9]+, Errors: [0-9]+, Skipped: [0-9]+$" "$log" | tail -1)
    local status="GREEN"
    [ "$code" -ne 0 ] && status="RED"
    echo "  прогон $i: $status | $line" >> "$SUMMARY"
    # какие тесты упали и с каким исключением
    grep -E "^\[ERROR\]   [A-Za-z]|^\[ERROR\] [a-zA-Z0-9_]+\(.*\)|Exception:" "$log" \
      | grep -vE "^\[ERROR\] (Tests run|Failures|Errors|To see|Re-run|-> \[Help|Failed to execute)" \
      | head -20 | sed 's/^/      /' >> "$SUMMARY"
    mkdir -p "$OUT/${label}_surefire_run${i}"
    cp "$dir"/target/surefire-reports/*.txt "$OUT/${label}_surefire_run${i}/" 2>/dev/null
  done
  echo >> "$SUMMARY"
}

# --- Семинар 7: до и после рефакторинга -----------------------------------
run_suite seminar7-original s7_original 1
run_suite seminar7 s7_refactored 3

# --- Семинар 8: исходный flaky-набор -----------------------------------------
run_suite seminar8-original s8_flaky_original 5

# --- Семинар 8: исправленный набор, 5 прогонов подряд + Allure с историей ---
echo "== s8_fixed (seminar8) + Allure, прогонов: 5" >> "$SUMMARY"
HIST=/tmp/allure-history
rm -rf "$HIST"
for i in 1 2 3 4 5; do
  log="$OUT/s8_fixed_run${i}.log"
  (
    cd seminar8 || exit 1
    rm -rf target/allure-results target/surefire-reports
    mkdir -p target/allure-results
    [ -d "$HIST" ] && cp -r "$HIST" target/allure-results/history
    cat > target/allure-results/executor.json <<EOF
{"name":"GitHub Actions","type":"github","buildName":"Прогон $i из 5","buildOrder":$i,
 "buildUrl":"${GITHUB_SERVER_URL:-}/${GITHUB_REPOSITORY:-}/actions/runs/${GITHUB_RUN_ID:-}"}
EOF
    {
      echo "Browser=$(google-chrome --version 2>/dev/null)"
      echo "Java=$(java -version 2>&1 | head -1 | tr -d '\"')"
      echo "OS=$(lsb_release -ds 2>/dev/null)"
      echo "App=test-app/shop.html (задержки 0-500 мс, перемешивание книг)"
    } > target/allure-results/environment.properties
    xvfb-run -a mvn -B test > "$log" 2>&1
  )
  code=$?
  line=$(grep -E "Tests run: [0-9]+, Failures: [0-9]+, Errors: [0-9]+, Skipped: [0-9]+$" "$log" | tail -1)
  status="GREEN"; [ "$code" -ne 0 ] && status="RED"
  echo "  прогон $i: $status | $line" >> "$SUMMARY"
  mkdir -p "$OUT/s8_fixed_surefire_run${i}"
  cp seminar8/target/surefire-reports/*.txt "$OUT/s8_fixed_surefire_run${i}/" 2>/dev/null
  (cd seminar8 && mvn -B allure:report > "$OUT/s8_fixed_allure_report_run${i}.log" 2>&1)
  rm -rf "$HIST"
  cp -r seminar8/target/site/allure-maven-plugin/history "$HIST" 2>/dev/null
done
cp -r seminar8/target/site/allure-maven-plugin "$OUT/allure-report"
cp -r seminar8/target/allure-results "$OUT/allure-results"
echo >> "$SUMMARY"

# --- Демонстрация скриншота при падении (копия seminar8 с намеренно неверным итогом) ---
echo "== Демонстрация: скриншот при падении в Allure (намеренно неверный ожидаемый итог)" >> "$SUMMARY"
DEMO=/tmp/seminar8-failure-demo
rm -rf "$DEMO"; cp -r seminar8 "$DEMO"
rm -rf "$DEMO/target"
sed -i 's/String.valueOf(2 \* Book.REFACTORING.price())/String.valueOf(3 * Book.REFACTORING.price())/' \
  "$DEMO/src/test/java/ru/qa/course/tests/BookShopRegressionTest.java"
(cd "$DEMO" && xvfb-run -a mvn -B test > "$OUT/s8_failure_demo.log" 2>&1)
line=$(grep -E "Tests run: [0-9]+, Failures: [0-9]+, Errors: [0-9]+, Skipped: [0-9]+$" "$OUT/s8_failure_demo.log" | tail -1)
echo "  $line" >> "$SUMMARY"
(cd "$DEMO" && mvn -B allure:report > /dev/null 2>&1)
cp -r "$DEMO/target/site/allure-maven-plugin" "$OUT/allure-report-failure-demo"

cat "$SUMMARY"
exit 0
