--
-- Type: Update
-- Origin: v1.3.1
-- Version: v1.4
--

--
-- Database structure for arbiter gold models
-- Version: v1.4
-- Last generated: 2023-09-14
--

--	Price	----------

-- Documents a metal's (average) price on a specific date
-- metal_id:             Metal who's price is recorded
-- 		References enumeration Metal
-- 		Possible values are: 1 = gold, 2 = silver
-- currency_id:          The currency in which the price is given
-- 		References enumeration Currency
-- 		Possible values are: 1 = euro, 2 = usd
-- date:                 Date on which the price was used
-- price_per_troy_ounce: Price of the specified metal in the specified currency. Per one troy ounce of metal.
CREATE TABLE `metal_price`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`metal_id` TINYINT NOT NULL,
	`currency_id` TINYINT NOT NULL,
	`date` DATE NOT NULL,
	`price_per_troy_ounce` DOUBLE NOT NULL,
	INDEX mp_combo_1_idx (metal_id, currency_id, `date`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Settings	----------

-- Represents a single (mutable) setting key-value pair used in common configurations
-- key:          Key that represents this setting's target / function
-- value:        Value given for this setting
-- last_updated: Time when this setting was last modified
CREATE TABLE `common_setting`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`key` VARCHAR(16) NOT NULL,
	`value` VARCHAR(32),
	`last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX cs_key_idx (`key`),
	INDEX cs_last_updated_idx (`last_updated`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

