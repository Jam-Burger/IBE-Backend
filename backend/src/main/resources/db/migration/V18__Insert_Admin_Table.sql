-- Insert specific admins for property_id = 9
INSERT INTO admin (admin_name, admin_email, admin_password, phone, property_id)
VALUES ('Jay Malaviya', 'jay40793@gmail.com', '$2a$10$MJ7T37xl2LrZFahiKTTvwOf96AMwTn9u2ap8MyBFD5kmfllMn4oW.',
        '1234567890', 9),
       ('Mirza Atif', 'themirza001@gmail.com', '$2a$10$MJ7T37xl2LrZFahiKTTvwOf96AMwTn9u2ap8MyBFD5kmfllMn4oW.',
        '0987654321', 9);

-- Insert random admins for property_id 1 to 24 excluding 9
DO
$$
    DECLARE
        i          INT;
        j          INT;
        num_admins INT;
    BEGIN
        FOR i IN 1..24
            LOOP
                IF i = 9 THEN
                    CONTINUE;
                END IF;

                -- Random number of admins per property (1 to 3)
                num_admins := floor(random() * 3 + 1);

                FOR j IN 1..num_admins
                    LOOP
                        INSERT INTO admin (admin_name, admin_email, admin_password, phone, property_id)
                        VALUES ('Admin_' || i || '_' || j,
                                'admin_' || i || '_' || j || '@example.com',
                                'pass_' || substr(md5(random()::text), 1, 10),
                                '9' || lpad((floor(random() * 1000000000)::int)::text, 9, '0'),
                                i);
                    END LOOP;
            END LOOP;
    END
$$;