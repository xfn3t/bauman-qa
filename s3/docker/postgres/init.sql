-- Создание дополнительных баз данных для разных окружений
CREATE DATABASE satellite_dev;
CREATE DATABASE satellite_test;
CREATE DATABASE satellite_local;
CREATE DATABASE scheduler_outbox;

-- Создание пользователей для разных окружений
CREATE USER dev_user WITH PASSWORD 'dev_password';
CREATE USER test_user WITH PASSWORD 'test_password';
CREATE USER scheduler_user WITH PASSWORD 'scheduler_pass';

GRANT ALL PRIVILEGES ON DATABASE satellite_dev TO dev_user;
GRANT ALL PRIVILEGES ON DATABASE satellite_test TO test_user;
GRANT ALL PRIVILEGES ON DATABASE scheduler_outbox TO scheduler_user;

\c scheduler_outbox

CREATE SCHEMA IF NOT EXISTS missions AUTHORIZATION scheduler_user;

GRANT ALL ON SCHEMA missions TO scheduler_user;

ALTER ROLE scheduler_user SET search_path TO missions, public;