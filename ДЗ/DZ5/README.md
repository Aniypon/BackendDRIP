# DZ5: Все типы хранилищ + Hazelcast

Spring Boot приложение, которое демонстрирует работу со всеми типами хранилищ из курса.
На старте `StorageRunner` последовательно обращается к каждому хранилищу и логирует результат,
после чего приложение корректно завершается.

## Хранилища

| Тип | Технология | Демонстрация |
|-----|------------|--------------|
| Реляционное | PostgreSQL (JPA/Hibernate) | сохранение и подсчёт `User` |
| Документное | MongoDB | сохранение и подсчёт `Author` |
| Key-value / кэш | Redis | `set`/`get` строкового значения |
| Колоночное (OLAP) | ClickHouse | создание таблицы `events`, вставка, `count()` |
| Объектное | S3 (MinIO) | создание бакета, `putObject`/`getObject` |
| In-memory data grid (доп. задание) | Hazelcast (embedded) | `IMap` put/get |

## Запуск

```bash
docker compose up --build
```

Поднимаются PostgreSQL, MongoDB, Redis, ClickHouse, MinIO и приложение.
Hazelcast работает встроенно (embedded), отдельный контейнер не нужен.

Ожидаемый вывод в логах `dz5-app`:

```
[PostgreSQL/JPA] users count = 2
[MongoDB] authors count = 1
[Redis] greeting = hello from redis
[ClickHouse] events count = 2
[S3/MinIO] object demo/hello.txt = hello from s3
[Hazelcast] map.get(key) = hello from hazelcast
All storage types verified successfully
```

Приложение завершается с кодом 0 после успешной проверки всех хранилищ.

## Тесты

```bash
gradle test
```

`UserRepositoryTest` (`@DataJpaTest` на H2) проверяет сохранение и чтение через Spring Data JPA
без поднятия инфраструктуры.

## Доступы (docker-compose)

| Сервис | Порт (host) | Логин/пароль |
|--------|-------------|--------------|
| PostgreSQL | 5435 | admin / password |
| MongoDB | 27018 | admin / password |
| Redis | 6380 | — |
| ClickHouse (HTTP) | 8124 | admin / password |
| MinIO API / Console | 9092 / 9093 | minioAccessKey / minioSecretKey |
