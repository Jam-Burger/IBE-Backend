CREATE TABLE reviews
(
    id         BIGSERIAL PRIMARY KEY,
    booking_id BIGINT                                                NOT NULL UNIQUE REFERENCES booking_extension (booking_id),
    rating     INTEGER                                               NOT NULL,
    comment    TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version    BIGINT                      DEFAULT 0                 NOT NULL
);
