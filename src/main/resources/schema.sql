DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INTEGER NOT NULL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    joined_on TIMESTAMP NOT NULL
);

CREATE TABLE books (
    id INTEGER NOT NULL PRIMARY KEY,
    title VARCHAR(64) NOT NULL,
    author VARCHAR(64) NOT NULL,
    description VARCHAR(256) NOT NULL,
    year INTEGER NOT NULL,

    borrowed_by INTEGER,
    borrowed_on TIMESTAMP,

    FOREIGN KEY (borrowed_by) REFERENCES users(id)
);
