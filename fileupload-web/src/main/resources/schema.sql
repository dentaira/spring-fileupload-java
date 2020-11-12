-- TODO enumを作成するときにif　existsする方法を見つける
--CREATE TYPE file_type AS ENUM ('FILE', 'DIRECTORY');
CREATE TABLE IF NOT EXISTS FILE (
    id SERIAL,
    name TEXT,
    parent TEXT,
    type FILE_TYPE,
    content BYTEA,
    size BIGINT
);
