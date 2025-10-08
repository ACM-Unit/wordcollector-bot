CREATE TABLE IF NOT EXISTS word (
    id SERIAL PRIMARY KEY,
    word_id INTEGER,
    word VARCHAR(255),
    translation VARCHAR(255),
    pupil_materials_id VARCHAR(255),
    date_create VARCHAR(255),
    audio VARCHAR(255),
    file_id VARCHAR(255),
    status VARCHAR(50),
    learned_words INTEGER DEFAULT 0,
    words_to_studied INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    house_points INTEGER DEFAULT 0
);