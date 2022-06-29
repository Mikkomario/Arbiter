--
-- Inserts the initial Arbiter project data to the DB
-- Intended to follow Citadel data insert
-- Supports versions v1.2 and above
--

-- Inserts the Finnish language (2)
INSERT INTO `language` (id, iso_code) VALUES (2, 'fi');

-- Inserts abbreviation description role (2)
INSERT INTO description_role (id, json_key_singular, json_key_plural) VALUES
    (2, 'abbreviation', 'abbreviations');

-- Inserts unit categories
-- 1 number
-- 2 time (minutes)
-- 3 time period (days)
-- 4 time period (months) - Separate because not directly comparable
INSERT INTO unit_category (id) VALUES (1), (2), (3), (4);

-- Inserts item units
-- Piece (1)
-- Minute (2)
-- Hour (3)
-- Day (4)
-- Month (5)
-- Year (6)
-- Percentage (7)
INSERT INTO item_unit (id, category_id, multiplier) VALUES
    (1, 1, 1.0),
    (2, 2, 1.0),
    (3, 2, 60.0),
    (4, 3, 1.0),
    (5, 4, 1.0),
    (6, 4, 12.0),
    (7, 1, 0.01);
