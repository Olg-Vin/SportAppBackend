-- Создание таблиц
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'user',
    profile_image VARCHAR(255),
    last_login TIMESTAMP,
    is_email_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) CHECK (status IN ('ожидается', 'выполняется', 'выполнено')),
    title VARCHAR(100) NOT NULL,
    description TEXT,
    calories INT CHECK (calories IS NOT NULL),
    category VARCHAR(50) CHECK (category IN ('спорт', 'еда', 'работа', 'сон')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Импорт данных из CSV
COPY users(name, email, password, role, profile_image, last_login, is_email_verified, created_at, updated_at)
FROM '/docker-entrypoint-initdb.d/users.csv'
DELIMITER ',' CSV HEADER;

COPY events(user_id, start_time, end_time, status, title, description, calories, category, created_at, updated_at)
FROM '/docker-entrypoint-initdb.d/events.csv'
DELIMITER ',' CSV HEADER;
