# DZ2: Two PostgreSQL + pgAdmin + Flyway + Hikari

## Что поднимается
- `postgres-db1` в сети `db1_network` (порт `5433` на хосте)
- `postgres-db2` в сети `db2_network` (порт `5434` на хосте)
- `pgadmin` подключен к обеим сетям (порт `5051` на хосте)
- `flyway-db1` и `flyway-db2` (по одному сервису на БД)
- `app` (Java + Hikari), выполняет запросы к обеим БД

## Запуск
```bash
docker compose up --build
```

## PgAdmin
- URL: http://127.0.0.1:5051
- Login: `admin@admin.com`
- Password: `password`


