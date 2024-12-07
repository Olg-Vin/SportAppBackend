-- Создание таблиц
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50),
    title VARCHAR(100) NOT NULL,
    description TEXT,
    calories INT,
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Импорт данных в таблицу Users
COPY Users(
    "name",
    "email",
    "password",
    "role",
    "created_at",
    "updated_at"
)
FROM '/var/lib/postgresql/data/users.csv' DELIMITER ',' CSV HEADER;

-- Импорт данных в таблицу Events
COPY Events(
    "user_id",
    "start_time",
    "end_time",
    "status",
    "title",
    "description",
    "calories",
    "category",
    "created_at",
    "updated_at"
)
FROM '/var/lib/postgresql/data/events.csv' DELIMITER ',' CSV HEADER;