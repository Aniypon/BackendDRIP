CREATE TABLE IF NOT EXISTS app_data (
    id SERIAL PRIMARY KEY,
    value TEXT NOT NULL
);

INSERT INTO app_data (value) VALUES
('hello from db1'),
('second row db1');
