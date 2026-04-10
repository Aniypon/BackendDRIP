# DZ4: JDBC-примеры, воспроизведённые в JPA

Все JDBC-сценарии из семинара перенесены на JPA/Hibernate + Spring (`EntityManager`,
`TransactionTemplate`, `JdbcTemplate`). На старте `JpaJdbcParityService.runAllExamples()`
последовательно прогоняет каждый сценарий и печатает состояние таблицы `users`.

## Сценарии

**Вставка**
- `insertUser` — одиночная вставка в транзакции
- `insertMultipleUsers` — пакетная вставка
- `insertWithTransaction` — вставка с явной транзакцией
- `insertWithTransactionRollback` — вставка с откатом (`setRollbackOnly`)

**Аномалии изоляции** (демонстрация поведения PostgreSQL)
- `testReadNotCommitted` — чтение во время незакоммиченной транзакции
- `dirtyRead` / `notDirtyRead` — грязное чтение (PostgreSQL не допускает даже при READ UNCOMMITTED)
- `repeatableRead` / `notRepeatableRead` — неповторяемое чтение (READ COMMITTED vs REPEATABLE READ)
- `phantomRead` / `notPhantomRead` — фантомное чтение (READ COMMITTED vs REPEATABLE READ)
- `anomalyExample` / `notAnomalyExample` — write skew (REPEATABLE READ vs SERIALIZABLE с конфликтом сериализации)

**Распространение транзакций**
- `propagationExample(REQUIRED)` — присоединение к существующей транзакции
- `propagationExample(SUPPORTS)` — выполнение без собственной транзакции
- `propagationExample(REQUIRES_NEW)` — отдельная вложенная транзакция

## Запуск
```bash
docker compose up --build app
```
Поднимаются PostgreSQL + Flyway (миграция `users`), затем приложение прогоняет все сценарии
и завершается. Состояние таблицы печатается после каждого блока (`--- <label> ---`, `count=...`).

## Тесты
```bash
gradle test
```
`JpaJdbcParityServiceTest` (на H2, без поднятия PostgreSQL) проверяет happy-path вставки,
пакетную вставку, откат транзакции и очистку таблицы. Тяжёлые многопоточные сценарии изоляции
отключены в тесте флагом `app.run-examples=false`.
