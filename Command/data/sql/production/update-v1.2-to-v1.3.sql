--
-- Arbiter DB structure update
-- Based on Citadel changes
-- Version: v1.3
-- Origin: v1.2
-- Type: Update
--

-- Language familiarities were changed. Also the order index column length was changed.

ALTER TABLE `language_familiarity`
    CHANGE `order_index` `order_index` TINYINT NOT NULL;

UPDATE `user_language` SET `familiarity_id` = 2 WHERE `familiarity_id` = 3;
UPDATE `user_language` SET `familiarity_id` = 3 WHERE `familiarity_id` IN (4, 5);
UPDATE `user_language` SET `familiarity_id` = 4 WHERE `familiarity_id` = 6;

DELETE FROM `language_familiarity` WHERE id IN (5, 6);