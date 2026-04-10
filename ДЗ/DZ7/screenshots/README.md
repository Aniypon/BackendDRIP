# Какие скриншоты приложить

Сохраните PNG/JPG в соответствующие папки.

## Kibana (`screenshots/kibana/`)
- Discover: логи сервиса `dz7-app` с полями `traceId`/`spanId`.
- Discover: логи Postgres (`service=postgresql`).

## Grafana (`screenshots/grafana/`)
- Дашборд `DZ7 Observability`, график `Decision Branch RPS`.
- Дашборд `DZ7 Observability`, график `Postgres Transactions Commit` с query:
  - `sum(irate(pg_stat_database_xact_commit{instance="$instance", datname=~"$datname"}[5m]))`
  - или `sum(pg_stat_database_xact_commit{instance="$instance", datname=~"$datname"})`

## Jaeger (`screenshots/jaeger/`)
- Трейс `decision.evaluate`.
- Вложенные спаны `decision.branch.premium/manual_review/auto_approve/reject`.
