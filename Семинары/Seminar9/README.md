# CI

```bash
./ci.sh
```

# CURL

## User Service (port 8081)

### Create user
```bash
curl -X POST http://localhost:8081/api/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe"
  }'
```

### Get user by ID
```bash
curl -X GET http://localhost:8081/api/users/2
```

## Order Service (port 8080)

### Create order
```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "ORD-002",
    "userId": 1
  }'
```

### Get order by ID (with user info)
```bash
curl -X GET http://localhost:8082/api/orders/
```
