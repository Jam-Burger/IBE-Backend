CREATE TABLE otp (
    otp_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    otp_number VARCHAR(255) NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    attempt_remaining INT
);
