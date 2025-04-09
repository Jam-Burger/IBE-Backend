CREATE TABLE pending_email (
    id BIGSERIAL PRIMARY KEY,
    to_email VARCHAR(255) NOT NULL,
    template_name VARCHAR(255) NOT NULL,
    guest_name VARCHAR(255),
    booking_id BIGINT,
    property_id BIGINT,
    tenant_id BIGINT,
    send_after TIMESTAMP NOT NULL,
    sent BOOLEAN DEFAULT FALSE,

    version BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
