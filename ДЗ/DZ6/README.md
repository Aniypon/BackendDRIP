# DZ6: Форма авторизации с JWT

Реализована кастомная форма логина, которая получает JWT вместо JSESSIONID.

## Ключевые моменты
- Приложение построено на `@SpringBootApplication`.
- Security работает в stateless-режиме (`SessionCreationPolicy.STATELESS`).
- Токен выдается endpoint-ом `POST /api/auth/login`.
- Защищенный endpoint: `GET /api/user`.

## Запуск
```bash
docker compose up --build
```

## Проверка вручную
1. Открыть `http://localhost:8086/login.html`
2. Войти: `user / password`
3. Нажать кнопку вызова `/api/user`

## Проверка через curl
```bash
curl -i -X POST http://localhost:8086/api/auth/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user&password=password"
```

В ответе будет JSON с полем `token`, и не будет `Set-Cookie: JSESSIONID`.
