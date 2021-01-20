#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER docker WITH PASSWORD '$POSTGRES_PASSWORD';
    CREATE DATABASE fileupload;
    GRANT ALL PRIVILEGES ON DATABASE fileupload TO docker;
EOSQL
