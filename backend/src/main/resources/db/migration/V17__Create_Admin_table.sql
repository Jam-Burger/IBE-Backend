CREATE TABLE IF NOT EXISTS admin
(
    id             BIGSERIAL PRIMARY KEY,
    admin_name     VARCHAR(100) NOT NULL,
    admin_email    VARCHAR(100) NOT NULL UNIQUE,
    admin_password VARCHAR(100) NOT NULL,
    phone          VARCHAR(20),
    property_id    BIGINT       NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version        BIGINT    DEFAULT 0,

    CONSTRAINT fk_admin_property FOREIGN KEY (property_id)
        REFERENCES property_extension (id)
);
