--
-- Arbiter DB Structure Update
-- Type: Changes
-- From: v1.1
-- To: v1.2
--

-- Adds new unit, percentage (7)
INSERT INTO item_unit (id, category_id, multiplier) VALUES (7, 1, 0.01);