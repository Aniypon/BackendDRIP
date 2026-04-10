# DZ7: Observability for if-else business logic

Реализовано:
- бизнес-сервис с ветками `if-else`
- в каждой ветке есть:
  - логи
  - трейсы
  - метрики
- Grafana дашборд для метрик приложения и Postgres
- ELK + Jaeger + Prometheus + Grafana + Postgres stack

## Бизнес логика
Endpoint: `GET /api/decision?amount=<int>&vip=<true|false>`

Ветки:
- `amount >= 1000 && vip = true` -> `premium`
- `amount >= 1000` -> `manual_review`
- `amount >= 100` -> `auto_approve`
- иначе -> `reject`

## Что собирается
- **Логи**: JSON-логи приложения + syslog Postgres -> Logstash -> Elasticsearch -> Kibana
- **Трейсы**: Micrometer Tracing / OpenTelemetry -> Jaeger
- **Метрики**: Actuator Prometheus + Postgres Exporter -> Prometheus -> Grafana

## PromQL (из задания)
Использован в графике транзакций Postgres:

```promql
sum(irate(pg_stat_database_xact_commit{instance="$instance", datname=~"$datname"}[5m]))
```

или

```promql
sum(pg_stat_database_xact_commit{instance="$instance", datname=~"$datname"})
```

## Запуск
```bash
docker compose up --build -d
```

## Генерация данных
```bash
curl "http://localhost:8088/api/decision?amount=1500&vip=true"
curl "http://localhost:8088/api/decision?amount=1500&vip=false"
curl "http://localhost:8088/api/decision?amount=500&vip=false"
curl "http://localhost:8088/api/decision?amount=50&vip=false"
```

## UI
- App: http://localhost:8088/api/healthcheck
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001 (admin/admin)
- Kibana: http://localhost:5601
- Jaeger: http://localhost:16686

## Скриншоты
Папки для скриншотов:
- `screenshots/kibana/`
- `screenshots/grafana/`
- `screenshots/jaeger/`

