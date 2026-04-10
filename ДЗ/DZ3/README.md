# DZ3: JPA + Hibernate + Spring Data JPA

Реализовано:
- таблица `users` описана JPA-сущностью `User`
- подключение к БД через Hibernate (`hibernate.cfg.xml`)
- выполнены запросы на выборку через Hibernate
- подключены Spring + Spring Data JPA
- выполнены запросы на выборку через Spring JPA

## Запуск
```bash
docker compose up --build app
```

## Что проверить в логах app
- `Hibernate SELECT result:`
- `Spring JPA SELECT result:`


