-- Таблица пользователей
create table IF NOT EXISTS users (
    user_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar(50) UNIQUE NOT NULL,
    login varchar(50) UNIQUE NOT NULL,
    name varchar(50),
    birth_day DATE CHECK (birth_day <= CURRENT_DATE)
);
-- Таблица рейтингов
create table IF NOT EXISTS  ratings (
    rating_id  bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50) UNIQUE NOT NULL
);

INSERT INTO ratings (name)
SELECT 'G' WHERE NOT EXISTS (SELECT 1 FROM ratings WHERE name = 'G');
INSERT INTO ratings (name)
SELECT 'PG' WHERE NOT EXISTS (SELECT 1 FROM ratings WHERE name = 'PG');
INSERT INTO ratings (name)
SELECT 'PG-13' WHERE NOT EXISTS (SELECT 1 FROM ratings WHERE name = 'PG-13');
INSERT INTO ratings (name)
SELECT 'R' WHERE NOT EXISTS (SELECT 1 FROM ratings WHERE name = 'R');
INSERT INTO ratings (name)
SELECT 'NC-17' WHERE NOT EXISTS (SELECT 1 FROM ratings WHERE name = 'NC-17');

-- Таблица фильмов
create table IF NOT EXISTS  films (
    film_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title varchar(200) NOT NULL,
    description text,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    rating_id SMALLINT REFERENCES ratings (rating_id),
);



-- Таблица лайков фильмов
create table IF NOT EXISTS film_likes (
    user_id bigint REFERENCES users (user_id),
    film_id bigint REFERENCES films (film_id),
    PRIMARY KEY (user_id, film_id)
);

-- Таблица жанров
create table IF NOT EXISTS genre (
    genre_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50) UNIQUE NOT NULL
);
INSERT INTO genre (name)
SELECT 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Комедия');
INSERT INTO genre (name)
SELECT 'Драма' WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Драма');
INSERT INTO genre (name)
SELECT 'Мультфильм' WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Мультфильм');
INSERT INTO genre (name)
SELECT 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Триллер');
INSERT INTO genre (name)
SELECT 'Документальный' WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Документальный');
INSERT INTO genre (name)
SELECT 'Боевик' WHERE NOT EXISTS (SELECT 1 FROM genre WHERE name = 'Боевик');
--Драма.
--Мультфильм.
--Триллер.
--Документальный.
--Боевик.

-- Таблица связи фильмов и жанров
create table IF NOT EXISTS film_genre (
    film_id bigint REFERENCES films (film_id),
    genre_id int REFERENCES genre (genre_id),
    PRIMARY KEY (film_id, genre_id)
);

-- Таблица друзей
create table IF NOT EXISTS friends (
    user_id bigint REFERENCES users (user_id),
    friend_id bigint REFERENCES users (user_id),
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (user_id, friend_id)
);

-- Таблица отзывов
create table IF NOT EXISTS review(
    review_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content text,
    positive BOOLEAN,
    user_id bigint REFERENCES users (user_id),
    film_id bigint REFERENCES films (film_id)
);

-- Таблица оценки отзывов
create table IF NOT EXISTS review_score(
    review_id bigint REFERENCES review (review_id) ON DELETE CASCADE,
    user_id bigint REFERENCES users (user_id),
    isPositive BOOLEAN,
    PRIMARY KEY (review_id, user_id)
);

-- Таблица режиссеров
create table if not EXISTS  director (
    director_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(200) NOT NULL,
)

create table if NOT EXISTS director_film(
    director_id bigint REFERENCES director (director_id),
    film_id bigint REFERENCES films (film_id),
    PRIMARY KEY (director_id, film_id)
)
