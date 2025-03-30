-- Update room type descriptions to make them shorter by removing 1-2 sentences
UPDATE room_type_extension
SET description = CASE
    -- For description pattern 1
                      WHEN description LIKE 'Experience luxury redefined%' THEN
                          'Experience luxury redefined in our meticulously designed rooms, where every detail has been carefully curated for the discerning traveler. Modern amenities blend seamlessly with elegant decor, creating the perfect retreat for both relaxation and productivity. Floor-to-ceiling windows offer breathtaking views of the cityscape.'

    -- For description pattern 2
                      WHEN description LIKE 'Discover tranquility in our%' THEN
                          'Discover tranquility in our thoughtfully appointed accommodations, designed to exceed the expectations of modern travelers. Each room showcases contemporary design elements paired with premium furnishings, offering a sophisticated haven for both business and leisure guests.'

    -- For description pattern 3
                      WHEN description LIKE 'Immerse yourself in comfort%' THEN
                          'Immerse yourself in comfort and style in our well-appointed rooms, where luxury meets functionality in perfect harmony. These thoughtfully designed spaces feature a blend of modern amenities and elegant touches, creating an environment that caters to your every need.'

    -- For description pattern 4
                      WHEN description LIKE 'Welcome to your urban sanctuary%' THEN
                          'Welcome to your urban sanctuary, where comfort meets contemporary sophistication in our expertly designed rooms. The carefully planned layout maximizes both space and functionality, while maintaining an inviting atmosphere perfect for relaxation and rejuvenation.'

    -- For description pattern 5
                      WHEN description LIKE 'Step into a world of refined%' THEN
                          'Step into a world of refined elegance in our premium accommodations, where luxury and comfort converge to create an unforgettable stay experience. Each room is thoughtfully designed with meticulous attention to detail, featuring high-end amenities and stunning views.'

    -- Default case (keep original)
                      ELSE description
    END
WHERE description IS NOT NULL;

-- Update ratings to be between 2 and 5 where not null
UPDATE room_type_extension
SET rating = random() * 3 + 2
WHERE rating IS NOT NULL;