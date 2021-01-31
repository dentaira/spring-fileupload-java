\c fileupload docker

CREATE TYPE file_type AS ENUM ('FILE', 'DIRECTORY');

CREATE TABLE IF NOT EXISTS file (
    id CHAR(36),
    name TEXT,
    path TEXT,
    type FILE_TYPE,
    content BYTEA,
    size BIGINT
);
