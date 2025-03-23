-- Special Discount Initialization Data

-- Generate all discounts using CTEs
WITH
-- Holidays definition
holidays AS (SELECT holiday_date, holiday_name
             FROM (VALUES ('2025-03-25'::date, 'Holi'),
                          ('2025-03-29'::date, 'Good Friday'),
                          ('2025-04-09'::date, 'Ugadi'),
                          ('2025-04-14'::date, 'Tamil New Year/Baisakhi'),
                          ('2025-04-21'::date, 'Ram Navami'),
                          ('2025-05-01'::date, 'Labor Day/Maharashtra Day'),
                          ('2025-05-12'::date, 'Mother''s Day'),
                          ('2025-05-23'::date, 'Buddha Purnima'),
                          ('2025-06-14'::date, 'Flag Day'),
                          ('2025-06-16'::date, 'Father''s Day'),
                          ('2025-06-19'::date, 'Juneteenth')) AS h(holiday_date, holiday_name)),
-- Generate holiday discounts
holiday_discounts AS (SELECT ROW_NUMBER() OVER ()                      as id,
                             p.property_id,
                             h.holiday_date                            as discount_date,
                             ROUND((15 + (RANDOM() * 20))::numeric, 2) as discount_percentage,
                             h.holiday_name                            as description
                      FROM holidays h
                               CROSS JOIN (SELECT generate_series(1, 24) as property_id) p
                      WHERE RANDOM() < 0.4),
-- Generate weekend dates
weekend_dates AS (SELECT date_series::date as discount_date
                  FROM generate_series(
                               '2025-03-01'::timestamp,
                               '2025-06-30'::timestamp,
                               '1 day'::interval
                       ) as date_series
                  WHERE EXTRACT(DOW FROM date_series) IN (5, 6)),
-- Generate weekend discounts
weekend_discounts AS (SELECT (SELECT COALESCE(MAX(id), 0) FROM holiday_discounts) + ROW_NUMBER() OVER () as id,
                             p.property_id,
                             w.discount_date,
                             ROUND((10 + (RANDOM() * 10))::numeric, 2)                                   as discount_percentage,
                             'Weekend Discount'                                                          as description
                      FROM weekend_dates w
                               CROSS JOIN (SELECT generate_series(1, 24) as property_id) p
                      WHERE RANDOM() < 0.2
                        AND NOT EXISTS (SELECT 1
                                        FROM holiday_discounts hd
                                        WHERE hd.discount_date = w.discount_date
                                          AND hd.property_id = p.property_id)),
-- Generate weekday dates
weekday_dates AS (SELECT date_series::date as discount_date
                  FROM generate_series(
                               '2025-03-01'::timestamp,
                               '2025-06-30'::timestamp,
                               '1 day'::interval
                       ) as date_series
                  WHERE EXTRACT(DOW FROM date_series) IN (1, 2, 3, 4)),
-- Generate off-peak weekday discounts
weekday_discounts AS (SELECT (SELECT COALESCE(MAX(id), 0) FROM weekend_discounts) + ROW_NUMBER() OVER () as id,
                             p.property_id,
                             w.discount_date,
                             ROUND((5 + (RANDOM() * 10))::numeric, 2)                                    as discount_percentage,
                             'Off-peak Discount'                                                         as description
                      FROM weekday_dates w
                               CROSS JOIN (SELECT generate_series(1, 24) as property_id) p
                      WHERE RANDOM() < 0.1
                        AND NOT EXISTS (SELECT 1
                                        FROM holiday_discounts hd
                                        WHERE hd.discount_date = w.discount_date
                                          AND hd.property_id = p.property_id)
                        AND NOT EXISTS (SELECT 1
                                        FROM weekend_discounts wd
                                        WHERE wd.discount_date = w.discount_date
                                          AND wd.property_id = p.property_id))
-- Combine all discounts and insert into table
INSERT
INTO special_discount (id, property_id, discount_date, discount_percentage, description)
SELECT id, property_id, discount_date, discount_percentage, description
FROM (SELECT *
      FROM holiday_discounts
      UNION ALL
      SELECT *
      FROM weekend_discounts
      UNION ALL
      SELECT *
      FROM weekday_discounts) combined_discounts;