CREATE TABLE transaction
(
    id               BIGSERIAL PRIMARY KEY,
    transaction_id   VARCHAR(100)                                          NOT NULL UNIQUE,
    amount           DOUBLE PRECISION                                      NOT NULL,
    status           VARCHAR(20)                                           NOT NULL,
    timestamp        TIMESTAMP                                             NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version          BIGINT                      DEFAULT 0                 NOT NULL
);

CREATE INDEX idx_transaction_transaction_id ON transaction (transaction_id);
CREATE INDEX idx_transaction_status ON transaction (status);

ALTER TABLE booking_extension
    ADD CONSTRAINT fk_booking_extension_transaction
        FOREIGN KEY (transaction_id)
            REFERENCES transaction (transaction_id);