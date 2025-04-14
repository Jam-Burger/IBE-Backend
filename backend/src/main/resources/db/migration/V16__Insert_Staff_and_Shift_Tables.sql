-- Migration script to populate shift table
-- Creates 3 shifts (Morning, Afternoon, Evening) for each property
DO
$$
    BEGIN
        FOR prop_id IN 1..24
            LOOP
                INSERT INTO public.shift (shift_name, start_time, end_time, property_id)
                VALUES ('Morning', '08:00:00', '12:00:00', prop_id),
                       ('Afternoon', '12:00:00', '16:00:00', prop_id),
                       ('Evening', '16:00:00', '20:00:00', prop_id);
            END LOOP;
    END
$$;

-- Migration script to populate staff table
-- Creates 10 permanent staff and 20 temporary staff for each property
DO
$$
    DECLARE
        staff_counter INT := 1;
        shift_id      INT;
        shift_count   INT;
        random_offset INT;
    BEGIN
        FOR prop_id IN 1..24
            LOOP
                -- Count how many shifts exist for this property
                SELECT COUNT(*) INTO shift_count FROM public.shift WHERE property_id = prop_id;

                -- Add 10 permanent staff for each property
                FOR i IN 1..10
                    LOOP
                        -- Select a random shift for this property as preferred shift
                        random_offset := floor(random() * shift_count);
                        SELECT id
                        INTO shift_id
                        FROM public.shift
                        WHERE property_id = prop_id
                        OFFSET random_offset LIMIT 1;

                        INSERT INTO public.staff (staff_name,
                                                  staff_email,
                                                  staff_password,
                                                  phone,
                                                  is_permanent_staff,
                                                  preferred_shift_id)
                        VALUES ('Permanent Staff ' || staff_counter,
                                'perm' || staff_counter || '@example.com',
                                '$2a$10$MJ7T37xl2LrZFahiKTTvwOf96AMwTn9u2ap8MyBFD5kmfllMn4oW.',
                                '555-' || LPAD(staff_counter::TEXT, 4, '0'),
                                TRUE,
                                shift_id);
                        staff_counter := staff_counter + 1;
                    END LOOP;

                -- Add 20 temporary staff for each property
                FOR i IN 1..20
                    LOOP
                        -- Select a random shift for this property as preferred shift
                        random_offset := floor(random() * shift_count);
                        SELECT id
                        INTO shift_id
                        FROM public.shift
                        WHERE property_id = prop_id
                        OFFSET random_offset LIMIT 1;

                        INSERT INTO public.staff (staff_name,
                                                  staff_email,
                                                  staff_password,
                                                  phone,
                                                  is_permanent_staff,
                                                  preferred_shift_id)
                        VALUES ('Temp Staff ' || staff_counter,
                                'temp' || staff_counter || '@example.com',
                                'password' || staff_counter,
                                '555-' || LPAD(staff_counter::TEXT, 4, '0'),
                                FALSE,
                                shift_id);
                        staff_counter := staff_counter + 1;
                    END LOOP;
            END LOOP;
    END
$$;

-- Existing clean_task_type data
INSERT INTO public.clean_task_type (type_name, required_time)
VALUES ('Check-out Cleaning', 120),
       ('Check-in Cleaning', 30),
       ('Daily Cleaning', 30);

-- Generate random availability for each staff member for the next month
DO
$$
    DECLARE
        staff_record RECORD;
        current_date DATE := CURRENT_DATE;
        end_date     DATE := CURRENT_DATE + INTERVAL '1 month';
        loop_date    DATE;
        is_available BOOLEAN;
    BEGIN
        -- Loop through all staff members
        FOR staff_record IN SELECT id, is_permanent_staff FROM public.staff
            LOOP
                -- For each staff member, generate availability for each day in the next month
                loop_date := current_date;

                WHILE loop_date < end_date
                    LOOP
                        -- Generate random availability (approximately 80% available, 20% unavailable)
                        is_available := ((not staff_record.is_permanent_staff) or random() < 0.8);

                        -- Insert availability record
                        INSERT INTO public.staff_availability (staff_id, date, is_available)
                        VALUES (staff_record.id, loop_date, is_available);

                        -- Move to next day
                        loop_date := loop_date + INTERVAL '1 day';
                    END LOOP;
            END LOOP;
    END
$$;