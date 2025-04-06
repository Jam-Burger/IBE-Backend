-- Enable the btree_gist extension for the exclusion constraint
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE room_date_lock
(
    room_id    BIGINT, -- room_type_id
    start_date DATE             NOT NULL,
    end_date   DATE             NOT NULL,
    created_at TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version    BIGINT DEFAULT 0 NOT NULL,

    -- Composite primary key
    PRIMARY KEY (room_id, start_date, end_date),

    -- Ensure end_date is after start_date
    CONSTRAINT room_date_lock_date_check CHECK (end_date > start_date),

    -- Prevent date range overlaps for the same room
    CONSTRAINT room_date_lock_no_overlap EXCLUDE USING gist (
        room_id WITH =,
        daterange(start_date, end_date, '[)') WITH &&
        )
);

-- Create index for faster date range queries
CREATE INDEX idx_room_date_lock_dates ON room_date_lock USING gist (room_id, daterange(start_date, end_date, '[)'));

-- Create index for faster expiration cleanup
CREATE INDEX idx_room_date_lock_created_at ON room_date_lock (created_at);

-- Add comment explaining the table
COMMENT ON TABLE room_date_lock IS 'Stores room booking lock periods to prevent double bookings'; 