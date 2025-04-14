-- INSERT INTO clean_task_type (type_name, required_time)
-- VALUES ('Daily Cleaning', 30),
--        ('Check-out Cleaning', 120);
--
-- -- 2. Insert shift data for existing property_extension IDs (1 to 3)
-- INSERT INTO shift (shift_name, start_time, end_time, property_id)
-- VALUES ('Morning', '08:00:00', '12:00:00', 9),
--        ('Afternoon', '12:00:00', '16:00:00', 9),
--        ('Evening', '16:00:00', '20:00:00', 9);
--
-- -- 3. Insert staff data linked to above shifts
-- INSERT INTO staff (staff_name, staff_email, staff_password, phone, is_permanent_staff, preferred_shift_id)
-- VALUES ('Alice', 'alice@example.com', 'password1', '111-111-1111', TRUE, 1),
--        ('Bob', 'bob@example.com', 'password2', '222-222-2222', TRUE, 2),
--        ('Charlie', 'charlie@example.com', 'password3', '333-333-3333', FALSE, 1),
--        ('Daisy', 'daisy@example.com', 'password4', '444-444-4444', TRUE, 3);
--
-- -- 4. Insert staff availability for today
-- INSERT INTO staff_availability (staff_id, date, is_available)
-- VALUES (1, CURRENT_DATE, TRUE),
--        (2, CURRENT_DATE, TRUE),
--        (3, CURRENT_DATE, FALSE),
--        (4, CURRENT_DATE, TRUE);

INSERT INTO clean_task_type (id, type_name, required_time, created_at, updated_at, version)
VALUES
    (1, 'Standard Cleaning', 30, NOW(), NOW(), 1),
    (2, 'Deep Cleaning', 60, NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;
-- 2. Insert shift data for existing property_extension IDs (1 to 3)
INSERT INTO shift (id, shift_name, start_time, end_time, property_id, created_at, updated_at, version)
VALUES
    (1, 'Morning', '08:00:00', '12:00:00', 1, NOW(), NOW(), 1),
    (2, 'Afternoon', '12:00:00', '16:00:00', 2, NOW(), NOW(), 1),
    (3, 'Evening', '16:00:00', '20:00:00', 3, NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;
-- 3. Insert staff data linked to above shifts
INSERT INTO staff (id, staff_name, staff_email, staff_password, phone, is_permanent_staff, preferred_shift_id, created_at, updated_at, version)
VALUES
    (1, 'Alice', 'alice@example.com', 'password1', '111-111-1111', TRUE, 1, NOW(), NOW(), 1),
    (2, 'Bob', 'bob@example.com', 'password2', '222-222-2222', TRUE, 2, NOW(), NOW(), 1),
    (3, 'Charlie', 'charlie@example.com', 'password3', '333-333-3333', FALSE, 1, NOW(), NOW(), 1),
    (4, 'Daisy', 'daisy@example.com', 'password4', '444-444-4444', TRUE, 3, NOW(), NOW(), 1)
ON CONFLICT (id) DO NOTHING;
-- 4. Insert staff availability for today
INSERT INTO staff_availability (staff_id, date, is_available, created_at, updated_at, version)
VALUES
    (1, CURRENT_DATE, TRUE, NOW(), NOW(), 1),
    (2, CURRENT_DATE, TRUE, NOW(), NOW(), 1),
    (3, CURRENT_DATE, FALSE, NOW(), NOW(), 1),
    (4, CURRENT_DATE, TRUE, NOW(), NOW(), 1)
ON CONFLICT DO NOTHING;