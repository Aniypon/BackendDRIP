CREATE TABLE users (
    id BIGINT,
    name VARCHAR(50) NOT NULL,
    about VARCHAR(100),
)
ENGINE = MergeTree()
PRIMARY KEY id;
