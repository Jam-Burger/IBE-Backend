-- Create special_discount table
CREATE TABLE special_discount
(
    id                  BIGSERIAL PRIMARY KEY,
    property_id         BIGINT           NOT NULL,
    discount_date       DATE             NOT NULL,
    discount_percentage DOUBLE PRECISION NOT NULL,
    description         VARCHAR(255)
);

-- Create index for special_discount queries
CREATE INDEX idx_special_discount_property_date ON special_discount (property_id, discount_date);

-- Create room_type_extension table
CREATE TABLE room_type_extension
(
    room_type_id BIGINT PRIMARY KEY
);

-- Create room_type_image table for storing image URLs
CREATE TABLE room_type_image
(
    room_type_id BIGINT,
    image_url    VARCHAR(1024),
    CONSTRAINT fk_room_type_image FOREIGN KEY (room_type_id) REFERENCES room_type_extension (room_type_id)
); 