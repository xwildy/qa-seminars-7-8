# Семинары 7–8: рефакторинг автотестов (Java, Selenium, JUnit 5, Allure)

Курс «Тестирование ПО», Зенкин Г.Н., ИДБ-25-06.

| Каталог | Что это |
|---|---|
| `seminar7-original/` | Исходный проект seminar7-start: 4 теста в одном классе |
| `seminar7/` | Семинар 7 — структурный рефакторинг (Page Object Model) |
| `seminar8-original/` | Исходный проект seminar8-flaky: нестабильные тесты |
| `seminar8/` | Семинар 8 — стабильный набор + Allure |
| `ci/run_all.sh` | Скрипт прогонов в GitHub Actions |
| `results/` | Логи прогонов, `summary.txt`, Allure-отчёт (`results/allure-report/index.html`) |

Прогоны выполняются в GitHub Actions (ubuntu-latest, Chrome под Xvfb):
семинар 7 — до и после рефакторинга; семинар 8 — исходный набор 5 раз
и исправленный набор 5 раз подряд с формированием Allure-отчёта.
