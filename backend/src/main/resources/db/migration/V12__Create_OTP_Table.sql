CREATE TABLE otp
(
    id                BIGSERIAL PRIMARY KEY,
    email             VARCHAR(255)                                          NOT NULL UNIQUE,
    otp_number        VARCHAR(255)                                          NOT NULL,
    expiration_time   TIMESTAMP                                             NOT NULL,
    verified          BOOLEAN                     DEFAULT FALSE             NOT NULL,
    attempt_remaining INT,
    timestamp         TIMESTAMP                                             NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version           BIGINT                      DEFAULT 0                 NOT NULL
);
