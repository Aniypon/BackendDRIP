# План

```bash
curl -kv http://localhost:8080/public
```
```bash
curl -kv http://localhost:8080/csrf
```
```bash
curl -kv 'http://localhost:8080/login' \
  -b 'XSRF-TOKEN=abf1dc97-9e80-423a-ab3a-3360b1a29e51; Path=/' \
  --data-raw 'username=user&password=password&_csrf=gYKC9Ip1LCkTmdERmvDw-rVUyJHzoNCJIxCl87jabds7Ve1d4ODkxe4WFR4-oLQpqt3EyIY15fCRk7GkECOTw9rrDOkCMNhs'
```
```bash
curl -kv http://localhost:8080/user \
  -b 'JSESSIONID=1E3D84CA86C31AC841B3D52ACCD089B2; Path=/; HttpOnly'
```


```bash
curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "user", "password": "password"}'
```
```bash
curl -kv http://localhost:8080/user -H  'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImV4cCI6MTc3MjUyNzg2M30.Z19fONSxNOXWcwlBrwIW7QcNt3CD1Mm16jVQVcd_eK4%'
```

```bash
curl -X POST "http://localhost:8081/realms/myrealm/protocol/openid-connect/token" \
  -d "grant_type=password" \
  -d "client_id=myclient" \
  -d "username=test" \
  -d "password=pwd" \
  -d "scope=openid"
```
```bash
curl -kv http://localhost:8080/user -H  'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ1OXFtbmJvZUlPUzBHU1pJOGFTX2Zhci1GWjBraXpFWWVNRzY2VkdGQmhFIn0.eyJleHAiOjE3NzE2NjAwMTYsImlhdCI6MTc3MTY1OTcxNiwianRpIjoib25ydHJvOjJhMTYyM2YyLWMxODItY2Y1Ny05YzdhLWMyZjMxNzljMzY5YyIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MS9yZWFsbXMvbXlyZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJkNjQ2OGZhMC1lZGMxLTQ5ZjgtYWQ2YS0xMjdiYWYxNjBjYTkiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJteWNsaWVudCIsInNpZCI6IkFLdFZLNVpuV1pYNW5OZ2hXbE1nWnJPUyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtbXlyZWFsbSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6InRlc3QgdGVzdCIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3QiLCJnaXZlbl9uYW1lIjoidGVzdCIsImZhbWlseV9uYW1lIjoidGVzdCIsImVtYWlsIjoidGVzdEBnbWFpbC5jb20ifQ.HYasxxwyJwet1BrtujbWGmzdjKIpkEAAVDM5wXMt6e7UZdH1jiNlAFxj3_tiuSOvrSR8EqtNG9EkadtPfpTZ5eNc-yRqn0Q_BGnN51kYfva5x92CjzJfjDuIc6FjfGJnfagkngM0oYK8sIzpkSBTUUcN5NeJ7Oa5ViESBow2uvVQIjlr5B50JD4OLPZ1zZiKxLhCLNKv9LGhLZ5P0_vRMzq7_DeUT8MrdFVXdyFx0BtfXpCcGEZz9ZVBcUVSdum4y6a724bZKNFmoPCqWD62C6dSMG939n5swjm4hdm-ScTweiHU0pShhtsWX0-VPpSi_SJ9Kcw40QTj4cJLAWA6PA'
```