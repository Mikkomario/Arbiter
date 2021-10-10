--
-- Inserts the initial project data to the DB
--

-- Inserts the Finnish language (2)
INSERT INTO `language` (id, iso_code) VALUES (2, 'fi');

-- Inserts abbreviation description role (2)
INSERT INTO description_role (id, json_key_singular, json_key_plural) VALUES
    (2, 'abbreviation', 'abbreviations');

-- Inserts item units
-- Piece (1)
-- Hour (2)
INSERT INTO item_unit (id) VALUES (1, 2);