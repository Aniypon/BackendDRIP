SELECT u.id, u.username, u.email, COUNT(o.id) AS total_orders
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.created_at > '2024-01-01'
GROUP BY u.id, u.username, u.email
HAVING COUNT(o.id) > 1
ORDER BY total_orders DESC
LIMIT 10;