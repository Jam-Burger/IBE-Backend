WITH room_types AS (SELECT generate_series(1, 6 * 24) as id),
     base_data AS (SELECT id,
                          CASE
                              WHEN has_rating THEN (random() * 5)::numeric(3, 2)
                              END as rating,
                          CASE
                              WHEN has_rating THEN (random() * 9999 + 1)::bigint
                              END as num_of_reviews,
                          CASE
                              WHEN random() < 0.2 THEN 'Near Central Park'
                              WHEN random() < 0.2 THEN 'Close to Shopping District'
                              WHEN random() < 0.2 THEN 'Next to Fine Dining Restaurants'
                              WHEN random() < 0.3 THEN 'Walking Distance to Beach'
                              ELSE 'Near Business District'
                              END as landmark,
                          CASE
                              WHEN random() < 0.2
                                  THEN 'Experience luxury redefined in our meticulously designed rooms, where every detail has been carefully curated for the discerning traveler. Modern amenities blend seamlessly with elegant decor, creating the perfect retreat for both relaxation and productivity. Floor-to-ceiling windows offer breathtaking views of the cityscape, while premium bedding and soundproof walls ensure a peaceful night''s sleep. The sophisticated ambiance is complemented by smart room controls and mood lighting, allowing you to personalize your space effortlessly.'
                              WHEN random() < 0.2
                                  THEN 'Discover tranquility in our thoughtfully appointed accommodations, designed to exceed the expectations of modern travelers. Each room showcases contemporary design elements paired with premium furnishings, offering a sophisticated haven for both business and leisure guests. The serene color palette and luxurious textures create a calming atmosphere, while state-of-the-art technology ensures seamless connectivity. Expansive windows flood the space with natural light, highlighting the carefully selected artwork and designer furniture.'
                              WHEN random() < 0.3
                                  THEN 'Immerse yourself in comfort and style in our well-appointed rooms, where luxury meets functionality in perfect harmony. These thoughtfully designed spaces feature a blend of modern amenities and elegant touches, creating an environment that caters to your every need. The spacious layout includes dedicated areas for work and relaxation, while premium materials and finishes throughout reflect our commitment to quality. Enjoy the perfect balance of sophistication and comfort, with customizable lighting and climate controls at your fingertips.'
                              WHEN random() < 0.4
                                  THEN 'Welcome to your urban sanctuary, where comfort meets contemporary sophistication in our expertly designed rooms. The carefully planned layout maximizes both space and functionality, while maintaining an inviting atmosphere perfect for relaxation and rejuvenation. Premium furnishings and plush bedding ensure optimal comfort, while modern technology seamlessly integrates into the refined decor. Large windows offer stunning views and abundant natural light, creating an airy and uplifting environment throughout your stay.'
                              ELSE 'Step into a world of refined elegance in our premium accommodations, where luxury and comfort converge to create an unforgettable stay experience. Each room is thoughtfully designed with meticulous attention to detail, featuring high-end amenities and stunning views that set new standards in hospitality. The sophisticated decor is complemented by premium materials and finishes, while cutting-edge technology provides ultimate convenience. Generous living spaces are enhanced by designer furniture and custom lighting, creating the perfect ambiance for both relaxation and entertainment.'
                              END as description,
                          now()   as created_at,
                          now()   as updated_at,
                          0       as version
                   FROM (SELECT id,
                                random() < 0.7 as has_rating -- 70% chance to have both rating and reviews
                         FROM room_types) t)
INSERT
INTO room_type_extension (id, rating, num_of_reviews, landmark, description, created_at, updated_at, version)
SELECT *
FROM base_data;

-- Insert amenities for each room type
WITH room_types AS (SELECT generate_series(1, 6 * 24) as room_type_id),
     standard_amenities AS (SELECT room_type_id,
                                   unnest(ARRAY [
                                       'Wireless Internet Access',
                                       'Alarm Clock',
                                       'Electronic Door Locks',
                                       'Daily Housekeeping'
                                       ]) as base_amenity
                            FROM room_types),
     comfort_amenities AS (SELECT room_type_id,
                                  unnest(ARRAY [
                                      CASE WHEN random() < 0.5 THEN 'Complimentary Toiletries' END,
                                      CASE WHEN random() < 0.3 THEN 'Coffee/Tea Maker' END,
                                      CASE WHEN random() < 0.4 THEN 'Mini Fridge' END,
                                      CASE WHEN random() < 0.3 THEN 'Blackout Curtains' END,
                                      CASE WHEN random() < 0.3 THEN 'Reading Lights' END,
                                      CASE WHEN random() < 0.2 THEN 'USB Charging Ports' END,
                                      CASE WHEN random() < 0.2 THEN 'Full-Length Mirror' END
                                      ]) as comfort_amenity
                           FROM room_types),
     premium_amenities AS (SELECT room_type_id,
                                  unnest(ARRAY [
                                      CASE WHEN random() < 0.2 THEN 'Mini Bar' END,
                                      CASE WHEN random() < 0.3 THEN 'Smart TV' END,
                                      CASE WHEN random() < 0.35 THEN 'Premium TV Channels' END,
                                      CASE WHEN random() < 0.4 THEN 'Bathrobes' END,
                                      CASE WHEN random() < 0.4 THEN 'Slippers' END,
                                      CASE WHEN random() < 0.2 THEN 'Premium Toiletries' END,
                                      CASE WHEN random() < 0.2 THEN 'Welcome Amenities' END,
                                      CASE WHEN random() < 0.2 THEN 'Nespresso Machine' END
                                      ]) as premium_amenity
                           FROM room_types),
     luxury_amenities AS (SELECT room_type_id,
                                 unnest(ARRAY [
                                     CASE WHEN random() < 0.2 THEN 'Room Service' END,
                                     CASE WHEN random() < 0.15 THEN 'Balcony/Terrace' END,
                                     CASE WHEN random() < 0.12 THEN 'Kitchenette' END,
                                     CASE WHEN random() < 0.1 THEN 'Separate Living Area' END,
                                     CASE WHEN random() < 0.08 THEN 'Jacuzzi/Hot Tub' END,
                                     CASE WHEN random() < 0.06 THEN 'Walk-in Closet' END,
                                     CASE WHEN random() < 0.06 THEN 'Dining Area' END,
                                     CASE WHEN random() < 0.04 THEN 'Private Pool' END,
                                     CASE WHEN random() < 0.04 THEN 'Butler Service' END
                                     ]) as luxury_amenity
                          FROM room_types),
     tech_amenities AS (SELECT room_type_id,
                               unnest(ARRAY [
                                   CASE WHEN random() < 0.3 THEN 'High-Speed WiFi' END,
                                   CASE WHEN random() < 0.3 THEN 'Bluetooth Speaker' END,
                                   CASE WHEN random() < 0.25 THEN 'Smart Room Controls' END,
                                   CASE WHEN random() < 0.15 THEN 'iPad/Tablet' END,
                                   CASE WHEN random() < 0.1 THEN 'Smart Mirror' END,
                                   CASE WHEN random() < 0.05 THEN 'Voice Assistant' END
                                   ]) as tech_amenity
                        FROM room_types),
     wellness_amenities AS (SELECT room_type_id,
                                   unnest(ARRAY [
                                       CASE WHEN random() < 0.25 THEN 'Yoga Mat' END,
                                       CASE WHEN random() < 0.15 THEN 'Fitness Equipment' END,
                                       CASE WHEN random() < 0.12 THEN 'Air Purifier' END,
                                       CASE WHEN random() < 0.08 THEN 'Meditation Corner' END,
                                       CASE WHEN random() < 0.05 THEN 'Aromatherapy Diffuser' END
                                       ]) as wellness_amenity
                            FROM room_types)
INSERT
INTO room_type_amenities (room_type_id, amenity)
SELECT room_type_id, base_amenity
FROM standard_amenities
WHERE base_amenity IS NOT NULL
UNION
SELECT room_type_id, comfort_amenity
FROM comfort_amenities
WHERE comfort_amenity IS NOT NULL
UNION
SELECT room_type_id, premium_amenity
FROM premium_amenities
WHERE premium_amenity IS NOT NULL
UNION
SELECT room_type_id, luxury_amenity
FROM luxury_amenities
WHERE luxury_amenity IS NOT NULL
UNION
SELECT room_type_id, tech_amenity
FROM tech_amenities
WHERE tech_amenity IS NOT NULL
UNION
SELECT room_type_id, wellness_amenity
FROM wellness_amenities
WHERE wellness_amenity IS NOT NULL; 