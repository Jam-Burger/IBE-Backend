ALTER TABLE property_extension
    ADD COLUMN check_in_time  TIME NOT NULL DEFAULT '12:00:00',
    ADD COLUMN check_out_time TIME NOT NULL DEFAULT '10:00:00';

CREATE TABLE shift
(
    id          BIGSERIAL PRIMARY KEY,
    shift_name  VARCHAR(100)                                       NOT NULL,
    start_time  TIME                                               NOT NULL, -- Timezone-agnostic, interpreted via property timezone
    end_time    TIME                                               NOT NULL, -- Timezone-agnostic, interpreted via property timezone
    property_id BIGINT                                             NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version     BIGINT                   DEFAULT 0                 NOT NULL,
    CONSTRAINT fk_shifts_property_id FOREIGN KEY (property_id)
        REFERENCES property_extension (id) ON DELETE CASCADE
);

CREATE TABLE staff
(
    id                 BIGSERIAL PRIMARY KEY,
    staff_name         VARCHAR(100)                                       NOT NULL,
    staff_email        VARCHAR(100)                                       NOT NULL UNIQUE,
    staff_password     VARCHAR(100)                                       NOT NULL,
    phone              VARCHAR(20),
    preferred_shift_id BIGINT,
    property_id        BIGINT                                             NOT NULL,
    created_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version            BIGINT                   DEFAULT 0                 NOT NULL,
    CONSTRAINT fk_staff_preferred_shift_id FOREIGN KEY (preferred_shift_id)
        REFERENCES shift (id) ON DELETE SET NULL,
    CONSTRAINT fk_staff_property_id FOREIGN KEY (property_id)
        REFERENCES property_extension (id) ON DELETE CASCADE
);

CREATE TABLE clean_task_type
(
    id            BIGSERIAL PRIMARY KEY,
    type_name     VARCHAR(100)                                       NOT NULL,
    required_time BIGINT                                             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version       BIGINT                   DEFAULT 0                 NOT NULL
);

CREATE TABLE clean_task
(
    id           BIGSERIAL PRIMARY KEY,
    property_id  BIGINT                                             NOT NULL,
    staff_id     BIGINT                                             NOT NULL,
    start_time   TIME                                               NOT NULL, -- Timezone-agnostic, interpreted via property timezone
    task_type_id BIGINT                                             NOT NULL,
    date         DATE                                               NOT NULL,
    room_id      BIGINT                                             NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version      BIGINT                   DEFAULT 0                 NOT NULL,
    CONSTRAINT fk_clean_tasks_property_id FOREIGN KEY (property_id)
        REFERENCES property_extension (id) ON DELETE CASCADE,
    CONSTRAINT fk_clean_tasks_staff_id FOREIGN KEY (staff_id)
        REFERENCES staff (id) ON DELETE CASCADE,
    CONSTRAINT fk_clean_tasks_task_type_id FOREIGN KEY (task_type_id)
        REFERENCES clean_task_type (id) ON DELETE RESTRICT
);

CREATE TABLE staff_availability
(
    staff_id     BIGSERIAL                                          NOT NULL,
    date         DATE                                               NOT NULL,
    is_available BOOLEAN                                            NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version      BIGINT                   DEFAULT 0                 NOT NULL,
    PRIMARY KEY (staff_id, date),
    CONSTRAINT fk_availability_staff_id FOREIGN KEY (staff_id)
        REFERENCES staff (id) ON DELETE CASCADE
);