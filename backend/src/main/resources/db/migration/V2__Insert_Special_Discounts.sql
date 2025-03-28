WITH RECURSIVE
    dates AS (SELECT '2025-03-01'::date AS date
              UNION ALL
              SELECT date + 1
              FROM dates
              WHERE date < '2025-06-30'::date),
    weekend_discounts AS (SELECT p.property_id,
                                 d.date::date                                                                                                                                                                                               as start_date,
                                 (d.date + (random() * 2)::integer + 1)::date                                                                                                                                                               as end_date, -- 1-3 days weekend offers
                                 (random() * 15 + 5)::double precision                                                                                                                                                                      as discount_percentage,
                                 'Weekend Getaway'::varchar(100)                                                                                                                                                                            as title,
                                 'Escape the weekday hustle with our special weekend rates. Perfect for a quick getaway, enjoy our premium amenities and services at discounted rates. Limited time offer for weekend stays.'::varchar(255) as description
                          FROM generate_series(1, 24) p(property_id)
                                   CROSS JOIN dates d
                          WHERE EXTRACT(DOW FROM d.date) IN (5, 6) -- Saturday and Sunday
                            AND random() < 0.2 -- 20% of weekends get discounts
    ),
    holiday_discounts AS (SELECT p.property_id,
                                 h.start_date::date,
                                 h.end_date::date,
                                 (h.base_discount + random() * 5)::double precision as discount_percentage,
                                 h.title::varchar(100),
                                 h.long_description::varchar(255)                   as description
                          FROM (VALUES ('2025-03-21', '2025-03-21', 'Holi Festival', 15, 'Holi Special',
                                        'Celebrate the festival of colors with us! Experience the vibrant spirit of Holi with special room rates and festive decorations. Join us for traditional celebrations and enjoy our special Holi-themed activities.'),
                                       ('2025-03-25', '2025-03-25', 'Ram Navami', 12, 'Ram Navami Offer',
                                        'Celebrate the auspicious occasion of Ram Navami with our special rates. Experience the divine atmosphere with traditional decorations and special arrangements for devotees.'),
                                       ('2025-04-13', '2025-04-13', 'Baisakhi', 10, 'Baisakhi Celebration',
                                        'Join us in celebrating the harvest festival of Baisakhi. Experience the rich cultural heritage with special traditional performances and festive decorations.'),
                                       ('2025-04-14', '2025-04-14', 'Ambedkar Jayanti', 8, 'Ambedkar Jayanti Special',
                                        'Commemorate the birth anniversary of Dr. B.R. Ambedkar with our special rates. Experience the historical significance of this day with special arrangements and cultural programs.'),
                                       ('2025-04-21', '2025-04-21', 'Ramzan Eid', 20, 'Eid Special',
                                        'Celebrate Eid with joy and happiness at our property. Experience the festive spirit with special Eid decorations, traditional delicacies, and cultural programs.'),
                                       ('2025-05-01', '2025-05-01', 'Labour Day', 10, 'Labour Day Offer',
                                        'Honor the spirit of labor with our special rates. Enjoy a relaxing stay with our premium services and special arrangements for working professionals.'),
                                       ('2025-05-09', '2025-05-09', 'Rabindranath Tagore Jayanti', 8,
                                        'Tagore Jayanti Special',
                                        'Celebrate the birth anniversary of Rabindranath Tagore with cultural programs and special rates. Experience the literary and artistic heritage with special arrangements.'),
                                       ('2025-06-10', '2025-06-10', 'Bakrid', 15, 'Bakrid Celebration',
                                        'Join us in celebrating Bakrid with special rates and festive arrangements. Experience the religious significance with traditional decorations and cultural programs.'),
                                       ('2025-06-21', '2025-06-21', 'International Yoga Day', 10, 'Yoga Day Special',
                                        'Celebrate International Yoga Day with our special wellness package. Experience rejuvenation with yoga sessions, meditation, and special wellness activities.')) AS h(start_date,
                                                                                                                                                                                                              end_date,
                                                                                                                                                                                                              description,
                                                                                                                                                                                                              base_discount,
                                                                                                                                                                                                              title,
                                                                                                                                                                                                              long_description)
                                   CROSS JOIN generate_series(1, 24) p(property_id)
                          WHERE random() < 0.7 -- 70% chance for each property to offer a holiday discount
    ),
    property_discounts AS (SELECT p.property_id,
                                  start_date,
                                  start_date + (random() * 4)::integer + 1 as end_date,
                                  (random() * 20 + 5)::double precision    as discount_percentage,
                                  o.title,
                                  o.description
                           FROM generate_series(1, 24) p(property_id)
                                    CROSS JOIN (VALUES ('Book Early & Save',
                                                        'Plan ahead and save more! Book your stay in advance and enjoy our special early bird rates. Perfect for organized travelers who want the best deals.'),
                                                       ('Property Anniversary',
                                                        'Join us in celebrating our property anniversary with exclusive rates and special amenities. Experience our commitment to excellence with anniversary-themed decorations and activities.'),
                                                       ('Seasonal Special',
                                                        'Experience the best of the season with our special rates. Enjoy seasonal activities, themed decorations, and special amenities tailored to the current season.'),
                                                       ('Exclusive Offer',
                                                        'Indulge in our exclusive offer with premium services and special rates. Experience luxury and comfort with our carefully curated special amenities and services.'),
                                                       ('Limited Time Deal',
                                                        'Don''t miss out on this limited-time opportunity! Enjoy special rates and exclusive benefits for a limited period. Perfect for spontaneous getaways.'),
                                                       ('Weekday Escape',
                                                        'Escape the weekend crowds with our special weekday rates. Enjoy a peaceful stay with all our premium services at discounted rates during weekdays.'),
                                                       ('Last Minute Deal',
                                                        'Grab this last-minute deal for an unexpected getaway! Enjoy our premium services at special rates for immediate bookings.'),
                                                       ('Flash Sale',
                                                        'Flash sale alert! Book now and save big on your stay. Limited time offer with exclusive benefits and special amenities.')) AS o(title, description)
                                    CROSS JOIN LATERAL (
                               SELECT '2025-03-01'::date + (random() * 121)::integer as start_date
                               ) dates
                           WHERE random() < 0.5 -- 50% chance for each property to have each type of offer
    )
INSERT
INTO special_offer (property_id, start_date, end_date, discount_percentage, title, description)
SELECT *
FROM holiday_discounts
UNION ALL
SELECT *
FROM weekend_discounts
UNION ALL
SELECT property_id, start_date, end_date, discount_percentage, title, description
FROM property_discounts
WHERE start_date < end_date
  AND start_date <= '2025-06-30'::date
  AND end_date <= '2025-06-30'::date;

-- Additional special offers with promo codes
WITH additional_offers AS (SELECT p.property_id,
                                  o.start_date::date,
                                  o.end_date::date,
                                  o.discount::double precision as discount_percentage,
                                  o.title::varchar(100),
                                  o.description::text,
                                  o.promo_code::varchar(255)
                           FROM generate_series(1, 24) p(property_id)
                                    CROSS JOIN (VALUES ('2025-03-01', '2025-03-31', 25, 'Spring Break Special',
                                                        'Welcome spring with our exclusive Spring Break package! Enjoy premium amenities, special welcome drinks, and access to our seasonal activities. Perfect for families and students looking for a memorable spring break.',
                                                        'SPRING25'),
                                                       ('2025-03-15', '2025-04-15', 30, 'Extended Stay Discount',
                                                        'Book a stay of 7 nights or more and unlock premium savings! Includes complimentary breakfast, late checkout, and special access to our executive lounge.',
                                                        'EXTEND30'),
                                                       ('2025-04-01', '2025-04-30', 20, 'April Adventure Package',
                                                        'Embark on an adventure this April! Package includes guided local tours, adventure sport activities, and special dining experiences.',
                                                        'APRIL20'),
                                                       ('2025-04-15', '2025-05-15', 35, 'Family Fun Package',
                                                        'Create lasting memories with our family package! Includes kids'' activities, family dining discounts, and complimentary access to our recreational facilities.',
                                                        'FAMILY35'),
                                                       ('2025-05-01', '2025-05-31', 22, 'May Wellness Retreat',
                                                        'Rejuvenate your mind and body with our wellness package. Includes spa credits, yoga sessions, and healthy meal options.',
                                                        'WELLNESS22'),
                                                       ('2025-05-15', '2025-06-15', 28, 'Business Traveler Special',
                                                        'Special rates for business travelers! Includes high-speed internet, business center access, and complimentary pressing service.',
                                                        'BIZ28'),
                                                       ('2025-06-01', '2025-06-30', 33, 'Summer Kickoff Deal',
                                                        'Start your summer with amazing savings! Enjoy pool access, summer cocktails, and special evening entertainment.',
                                                        'SUMMER33'),
                                                       ('2025-03-01', '2025-06-30', 15, 'AAA Member Discount',
                                                        'Exclusive discount for AAA members. Show your membership card at check-in to avail this special rate.',
                                                        'AAA15'),
                                                       ('2025-03-01', '2025-06-30', 18, 'Senior Citizen Special',
                                                        'Special rates for guests aged 60 and above. Valid ID required at check-in.',
                                                        'SENIOR18'),
                                                       ('2025-03-01', '2025-06-30', 20, 'Military & Veterans Discount',
                                                        'As a token of our gratitude, enjoy special rates for military personnel and veterans.',
                                                        'MILITARY20')) AS o(start_date, end_date, discount, title, description, promo_code)
                           WHERE random() < 0.7 -- 70% chance for each property to have each offer
)
INSERT
INTO special_offer (property_id, start_date, end_date, discount_percentage, title, description, promo_code)
SELECT *
FROM additional_offers;
