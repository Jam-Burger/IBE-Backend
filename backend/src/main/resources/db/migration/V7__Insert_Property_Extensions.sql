-- Insert property extensions for properties 1-24
INSERT INTO property_extension (id, contact_number, availability, country, surcharge, fees, tax, terms_and_conditions,
                                created_at, updated_at, version)
WITH RECURSIVE
    shuffled_numbers AS (SELECT generate_series as id, random() as r
                         FROM generate_series(1, 24)
                         ORDER BY r),
    country_info AS (SELECT row_number() OVER () as rn, *
                     FROM (VALUES ('IN', '+91 96-XXXX-XXXX', 'Mon-Fri 9a-6p IST', 18.0), -- India GST
                                  ('JP', '+81 X-XXXX-XXXX', 'Mon-Sat 9a-6p JST', 10.0),  -- Japan Consumption Tax
                                  ('KR', '+82 2-XXXX-XXXX', 'Mon-Fri 9a-6p KST', 10.0),  -- South Korea VAT
                                  ('SG', '+65 XXXX-XXXX', 'Mon-Sat 9a-6p SGT', 8.0),     -- Singapore GST
                                  ('AU', '+61 2-XXXX-XXXX', 'Mon-Fri 9a-5p AEST', 10.0), -- Australia GST
                                  ('AE', '+971 4-XXX-XXXX', 'Sun-Thu 9a-6p GST', 5.0),   -- UAE VAT
                                  ('GB', '+44 20-XXXX-XXXX', 'Mon-Fri 9a-5p GMT', 20.0), -- UK VAT
                                  ('US', '+1 XXX-XXX-XXXX', 'Mon-Fri 8a-5p EST', 8.5) -- US Sales Tax (average)
                          ) AS t (country_code, phone_pattern, availability_hours, tax_rate))
SELECT sn.id,
       -- Generate random contact numbers based on selected country's pattern
       regexp_replace(
               ci.phone_pattern,
               'X',
               (random() * 10)::integer::text,
               'g'
       )                                     as contact_number,
       ci.availability_hours                 as availability,
       ci.country_code                       as country,
       -- Random surcharge between 2% and 6%
       round((random() * 4 + 2)::numeric, 2) as surcharge,
       -- Random fees between $1 and $5
       round((random() * 4 + 1)::numeric, 2) as fees,
       -- Tax rate based on country
       ci.tax_rate                           as tax,
       -- Temporary terms and conditions link
       'https://example.com/terms'           as terms_and_conditions,
       CURRENT_TIMESTAMP                     as created_at,
       CURRENT_TIMESTAMP                     as updated_at,
       0                                     as version
FROM shuffled_numbers sn
         CROSS JOIN LATERAL (
    SELECT *
    FROM country_info
    WHERE rn = 1 + mod(sn.id - 1, 8)
    ) ci;