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

INSERT INTO users(id, name, joined_on ) VALUES
    ( 1, 'Jane Q. Public', '2021-09-25 12:00:00' ),
    ( 2, 'Oliver Overdue', '2019-01-01 12:00:00' ),
    ( 3, 'Ryan Reader', '2005-07-04 12:00:00' );

INSERT INTO books(id, title, author, description, year) VALUES
    ( 1, 'Anna Karenina', 'Leo Tolstoy', 'Anna Karenina tells of the doomed love affair between the sensuous and rebellious Anna and the dashing officer, Count Vronsky.', 1878),
    ( 2, 'Madame Bovary', 'Gustave Flaubert', 'The eponymous character lives beyond her means in order to escape the banalities and emptiness of provincial life.', 1856),
    ( 3, 'War and Peace', 'Leo Tolstoy', 'Epic in scale, War and Peace delineates in graphic detail events leading up to Napoleon''s invasion of Russia, and the impact of the Napoleonic era on Tsarist society', 1865),
    ( 4, 'The Great Gatsby', 'F. Scott Fitzgerald', 'Following the shock and chaos of World War I, American society enjoyed unprecedented levels of prosperity during the "roaring 20s."', 1925),
    ( 5, 'Lolita', 'Vladimir Nabokov', 'The protagonist and unreliable narrator, middle aged Humbert Humbert, becomes obsessed and sexually involved with a twelve-year-old girl named Dolores Haze.', 1955),
    ( 6, 'Middlemarch', 'George Eliot', 'Set in Middlemarch, a fictional English Midland town, in 1829 to 1832, it follows distinct, intersecting stories with many characters.', 1871),
    ( 7, 'The Adventures of Huckleberry Finn', 'Mark Twain', 'Revered by all of the town''s children and dreaded by all of its mothers, Huckleberry Finn is indisputably the most appealing child-hero in American literature.', 1884),
    ( 8, 'In Search of Lost Time', 'Marcel Proust', 'In Search of Lost Time follows the narrator''s recollections of childhood and experiences into adulthood in the late 19th century and early 20th century high society France, while reflecting on the loss of time and lack of meaning in the world.', 1927),
    ( 9, 'Hamlet', 'William Shakespeare', 'Set in Denmark, the play depicts Prince Hamlet and his revenge against his uncle, Claudius, who has murdered Hamlet''s father in order to seize his throne and marry Hamlet''s mother.', 1601),
    ( 10, 'Moby Dick', 'Herman Melville', 'The saga of Captain Ahab and his monomaniacal pursuit of the white whale.', 1851);

-- Oliver has 1 book checked out and very overdue
UPDATE books
    SET borrowed_by = 2, borrowed_on = parsedatetime('2020-09-31 12:00:00', 'yyyy-MM-dd hh:mm:ss')
    WHERE id = 8;

-- Ryan has 3 books checked out, none overdue
UPDATE books
    SET borrowed_by = 3, borrowed_on = CURRENT_TIMESTAMP()
    WHERE id in (2, 4, 6);