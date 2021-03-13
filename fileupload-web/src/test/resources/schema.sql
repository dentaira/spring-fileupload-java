CREATE TYPE file_type AS ENUM ('FILE', 'DIRECTORY');

CREATE TABLE IF NOT EXISTS file (
    id CHAR(36),
    name TEXT,
    path TEXT,
    type FILE_TYPE,
    content BYTEA,
    size BIGINT
);

CREATE TABLE IF NOT EXISTS user_account (
    id UUID PRIMARY KEY,
    email VARCHAR(254) UNIQUE,
    name VARCHAR(64),
    password VARCHAR(256),
    role VARCHAR(20)
)
