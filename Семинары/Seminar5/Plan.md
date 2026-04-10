# План

- Lock
  - [Optimistic](https://www.baeldung.com/jpa-optimistic-locking)
  - [Pessimistic](https://www.baeldung.com/jpa-pessimistic-locking)

## OLAP vs OLTP

### OLTP (Online Transaction Processing)

Postgres: 
- ACID
- Скорость записи/чтения
- Row based
- Размер таблицы: Максимум 32 ТБ
- Размер одной строки: Максимум 1.6 ТБ 
- Размер одного поля: До 1 ГБ.

OLAP (Online Analytical Processing)

ClickHouse:
- Column based
- Batch insert (ограничения на мелкие вставки)
- Без ограничений на размер таблиц

- [Driver](https://repo1.maven.org/maven2/com/clickhouse/clickhouse-jdbc/0.9.0/clickhouse-jdbc-0.9.0-all.jar)
- [Engine](https://clickhouse.com/docs/engines/table-engines#mergetree)


- Redis
- MongoDB
- S3

- [Hazelcast](https://www.baeldung.com/java-hazelcast)
  - https://habr.com/ru/companies/reksoft/articles/645997/
