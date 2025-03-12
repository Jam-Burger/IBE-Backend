CREATE TABLE student
(
    id    UUID    NOT NULL,
    name  VARCHAR(255),
    email VARCHAR(255),
    age   INTEGER NOT NULL,
    CONSTRAINT pk_student PRIMARY KEY (id)
);