CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO users (name, email) VALUES
('Ivan Petrov', 'ivan.petrov@example.com'),
('Olga Smirnova', 'olga.smirnova@example.com')
ON CONFLICT (email) DO NOTHING;
