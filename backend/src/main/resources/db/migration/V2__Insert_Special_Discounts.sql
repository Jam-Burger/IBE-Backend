WITH RECURSIVE dates AS (
    SELECT '2025-03-01'::date AS date
    UNION ALL
    SELECT date + 1
    FROM dates
    WHERE date < '2025-06-30'::date
),
weekend_discounts AS (
    SELECT 
        (random() * 23 + 1)::bigint as property_id,
        date::date as start_date,
        (date + 1)::date as end_date,
        (random() * 15 + 5)::double precision as discount_percentage,
        'Weekend Getaway'::varchar(100) as title,
        'Escape the weekday hustle with our special weekend rates. Perfect for a quick getaway, enjoy our premium amenities and services at discounted rates. Limited time offer for weekend stays.'::varchar(255) as description
    FROM dates
    WHERE EXTRACT(DOW FROM date) IN (5, 6)  -- Saturday and Sunday
    AND random() < 0.6  -- Increased from 0.3 to 0.6 (60% of weekends get discounts)
),
holiday_discounts AS (
    SELECT 
        p.property_id,
        h.start_date::date,
        h.end_date::date,
        (h.base_discount + random() * 5)::double precision as discount_percentage,
        h.title::varchar(100),
        h.long_description::varchar(255) as description
    FROM (
        VALUES
            ('2025-03-21', '2025-03-21', 'Holi Festival', 15, 'Holi Special', 'Celebrate the festival of colors with us! Experience the vibrant spirit of Holi with special room rates and festive decorations. Join us for traditional celebrations and enjoy our special Holi-themed activities.'),
            ('2025-03-25', '2025-03-25', 'Ram Navami', 12, 'Ram Navami Offer', 'Celebrate the auspicious occasion of Ram Navami with our special rates. Experience the divine atmosphere with traditional decorations and special arrangements for devotees.'),
            ('2025-04-13', '2025-04-13', 'Baisakhi', 10, 'Baisakhi Celebration', 'Join us in celebrating the harvest festival of Baisakhi. Experience the rich cultural heritage with special traditional performances and festive decorations.'),
            ('2025-04-14', '2025-04-14', 'Ambedkar Jayanti', 8, 'Ambedkar Jayanti Special', 'Commemorate the birth anniversary of Dr. B.R. Ambedkar with our special rates. Experience the historical significance of this day with special arrangements and cultural programs.'),
            ('2025-04-21', '2025-04-21', 'Ramzan Eid', 20, 'Eid Special', 'Celebrate Eid with joy and happiness at our property. Experience the festive spirit with special Eid decorations, traditional delicacies, and cultural programs.'),
            ('2025-05-01', '2025-05-01', 'Labour Day', 10, 'Labour Day Offer', 'Honor the spirit of labor with our special rates. Enjoy a relaxing stay with our premium services and special arrangements for working professionals.'),
            ('2025-05-09', '2025-05-09', 'Rabindranath Tagore Jayanti', 8, 'Tagore Jayanti Special', 'Celebrate the birth anniversary of Rabindranath Tagore with cultural programs and special rates. Experience the literary and artistic heritage with special arrangements.'),
            ('2025-06-10', '2025-06-10', 'Bakrid', 15, 'Bakrid Celebration', 'Join us in celebrating Bakrid with special rates and festive arrangements. Experience the religious significance with traditional decorations and cultural programs.'),
            ('2025-06-21', '2025-06-21', 'International Yoga Day', 10, 'Yoga Day Special', 'Celebrate International Yoga Day with our special wellness package. Experience rejuvenation with yoga sessions, meditation, and special wellness activities.')
    ) AS h(start_date, end_date, description, base_discount, title, long_description)
    CROSS JOIN generate_series(1, 24) p(property_id)
    WHERE random() < 0.7  -- 70% chance for each property to offer a holiday discount
),
property_discounts AS (
    SELECT 
        property_id,
        start_date::date,
        end_date::date,
        discount_percentage::double precision,
        title::varchar(100),
        long_description::varchar(255) as description
    FROM (
        SELECT 
            p.property_id,
            d.start_date,
            d.end_date,
            d.discount_percentage,
            d.title,
            d.long_description
        FROM generate_series(1, 24) p(property_id)
        CROSS JOIN LATERAL (
            SELECT 
                '2025-03-01'::date + (random() * 121)::integer as start_date,
                '2025-03-01'::date + (random() * 121 + (random() * 3 + 1))::integer as end_date,
                (random() * 20 + 5)::double precision as discount_percentage,
                CASE 
                    WHEN random() < 0.15 THEN 'Book Early & Save'
                    WHEN random() < 0.30 THEN 'Property Anniversary'
                    WHEN random() < 0.45 THEN 'Seasonal Special'
                    WHEN random() < 0.60 THEN 'Exclusive Offer'
                    WHEN random() < 0.75 THEN 'Limited Time Deal'
                    WHEN random() < 0.85 THEN 'Weekday Escape'
                    WHEN random() < 0.95 THEN 'Last Minute Deal'
                    ELSE 'Flash Sale'
                END as title,
                CASE 
                    WHEN random() < 0.15 THEN 'Plan ahead and save more! Book your stay in advance and enjoy our special early bird rates. Perfect for organized travelers who want the best deals.'
                    WHEN random() < 0.30 THEN 'Join us in celebrating our property anniversary with exclusive rates and special amenities. Experience our commitment to excellence with anniversary-themed decorations and activities.'
                    WHEN random() < 0.45 THEN 'Experience the best of the season with our special rates. Enjoy seasonal activities, themed decorations, and special amenities tailored to the current season.'
                    WHEN random() < 0.60 THEN 'Indulge in our exclusive offer with premium services and special rates. Experience luxury and comfort with our carefully curated special amenities and services.'
                    WHEN random() < 0.75 THEN 'Don''t miss out on this limited-time opportunity! Enjoy special rates and exclusive benefits for a limited period. Perfect for spontaneous getaways.'
                    WHEN random() < 0.85 THEN 'Escape the weekend crowds with our special weekday rates. Enjoy a peaceful stay with all our premium services at discounted rates during weekdays.'
                    WHEN random() < 0.95 THEN 'Grab this last-minute deal for an unexpected getaway! Enjoy our premium services at special rates for immediate bookings.'
                    ELSE 'Flash sale alert! Book now and save big on your stay. Limited time offer with exclusive benefits and special amenities.'
                END as long_description
            FROM generate_series(1, (random() * 8 + 3)::integer)  -- Increased from 2-7 to 3-10 discounts per property
        ) d
    ) t
    WHERE start_date < end_date  -- Ensure valid date ranges
    AND start_date <= '2025-06-30'::date  -- Ensure within our date range
    AND end_date <= '2025-06-30'::date
    AND random() < 0.9  -- Increased from 0.85 to 0.9 (90% of generated discounts will be applied)
)
INSERT INTO special_offer (property_id, start_date, end_date, discount_percentage, title, description)
SELECT * FROM holiday_discounts
UNION ALL
SELECT * FROM weekend_discounts
UNION ALL
SELECT * FROM property_discounts;
