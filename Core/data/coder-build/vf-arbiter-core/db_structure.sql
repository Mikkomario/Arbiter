-- Represents a county within a country
-- name: County name, with that county's or country's primary language
CREATE TABLE county(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`name` INT NOT NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a postal code, which represents an area within a county
-- number: The number portion of this postal code
-- county_id: Id of the county where this postal code is resides
CREATE TABLE postal_code(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`number` VARCHAR(5) NOT NULL, 
	`county_id` INT NOT NULL, 
	CONSTRAINT pc_c_county_ref_fk FOREIGN KEY pc_c_county_ref_idx (county_id) REFERENCES `county`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a specific street address
-- postal_code_id: Id of the postal_code linked with this StreetAddress
-- street_name: Name of the street -portion of this address
-- building_number: Number of the targeted building within the specified street
-- stair: Number or letter of the targeted stair within that building, if applicable
-- room_number: Number of the targeted room within that stair / building, if applicable
CREATE TABLE street_address(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`postal_code_id` INT NOT NULL, 
	`street_name` VARCHAR(64) NOT NULL, 
	`building_number` VARCHAR(10) NOT NULL, 
	`stair` VARCHAR(10), 
	`room_number` VARCHAR(10), 
	CONSTRAINT sa_pc_postal_code_ref_fk FOREIGN KEY sa_pc_postal_code_ref_idx (postal_code_id) REFERENCES `postal_code`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a registered company (or an individual person)
-- y_code: Official registration code of this company (id in the country system)
-- name: Name of this company
-- address_id: Street address of this company's headquarters or operation
-- tax_code: Tax-related identifier code for this company
CREATE TABLE company(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`y_code` VARCHAR(10) NOT NULL, 
	`name` VARCHAR(64) NOT NULL, 
	`address_id` INT NOT NULL, 
	`tax_code` VARCHAR(16), 
	CONSTRAINT c_sa_address_ref_fk FOREIGN KEY c_sa_address_ref_idx (address_id) REFERENCES `street_address`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Connects organizations with their owned companies
-- organization_id: Id of the owner organization
-- company_id: Id of the owned company
CREATE TABLE organization_company(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`organization_id` INT NOT NULL, 
	`company_id` INT NOT NULL, 
	CONSTRAINT oc_o_organization_ref_fk FOREIGN KEY oc_o_organization_ref_idx (organization_id) REFERENCES `organization`(id) ON DELETE CASCADE, 
	CONSTRAINT oc_c_company_ref_fk FOREIGN KEY oc_c_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a bank (used with bank addresses)
-- name: (Short) name of this bank
-- bic: BIC-code of this bank
CREATE TABLE bank(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`name` VARCHAR(64) NOT NULL, 
	`bic` VARCHAR(12) NOT NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Used for listing which bank addresses belong to which company
-- company_id: Id of the company which owns this bank account address
-- bank_id: Id of the bank where the company owns an account
-- address: The linked bank account address
-- created: Time when this information was registered
-- is_default: Whether this is the preferred / primary bank address used by this company
CREATE TABLE company_bank_address(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`company_id` INT NOT NULL, 
	`bank_id` INT NOT NULL, 
	`address` VARCHAR(32) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`is_default` BOOLEAN NOT NULL, 
	INDEX cba_created_idx (`created)`, 
	CONSTRAINT cba_c_company_ref_fk FOREIGN KEY cba_c_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE CASCADE, 
	CONSTRAINT cba_b_bank_ref_fk FOREIGN KEY cba_b_bank_ref_idx (bank_id) REFERENCES `bank`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a unit in which items can be counted
CREATE TABLE item_unit(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links ItemUnits with their descriptions
-- item_unit_id: Id of the described ItemUnit
-- description_id: Id of the linked description
-- created: Time when this description was added
-- deprecated_after: Time when this description was replaced or removed
CREATE TABLE item_unit_description(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`item_unit_id` INT NOT NULL, 
	`description_id` INT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX iud_created_idx (`created)`, 
	INDEX iud_deprecated_after_idx (`deprecated_after)`
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a bill / an invoice sent by one company to another to request a monetary transfer / payment
-- sender_company_id: Id of the company who sent this invoice (payment recipient)
-- recipient_company_id: Id of the recipient company of this invoice
-- reference_code: A custom reference code used by the sender to identify this invoice
-- created: Time when this Invoice was first created
-- payment_duration_days: Number of days during which this invoice can be paid before additional consequences
-- product_delivery_date: Date when the sold products were delivered, if applicable
CREATE TABLE invoice(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`sender_company_id` INT NOT NULL, 
	`recipient_company_id` INT NOT NULL, 
	`reference_code` VARCHAR(16) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`payment_duration_days` INT NOT NULL, 
	`product_delivery_date` DATE, 
	INDEX i_created_idx (`created)`, 
	CONSTRAINT i_c_sender_company_ref_fk FOREIGN KEY i_c_sender_company_ref_idx (sender_company_id) REFERENCES `company`(id) ON DELETE CASCADE, 
	CONSTRAINT i_c_recipient_company_ref_fk FOREIGN KEY i_c_recipient_company_ref_idx (recipient_company_id) REFERENCES `company`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an individual sold item or service
-- invoice_id: Id of the invoice on which this item appears
-- description: Name or description of this item
-- amount: Amount of items sold within the specified unit
-- unit_id: Unit in which these items are sold
-- price_per_unit: Euro (€) price per each sold unit of this item, without taxes applied
-- tax_modifier: A modifier that is applied to this item's price to get the applied tax
CREATE TABLE invoice_item(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`invoice_id` INT NOT NULL, 
	`description` VARCHAR(255) NOT NULL, 
	`amount` DOUBLE NOT NULL, 
	`unit_id` INT NOT NULL, 
	`price_per_unit` DOUBLE NOT NULL, 
	`tax_modifier` DOUBLE NOT NULL, 
	CONSTRAINT ii_i_invoice_ref_fk FOREIGN KEY ii_i_invoice_ref_idx (invoice_id) REFERENCES `invoice`(id) ON DELETE CASCADE, 
	CONSTRAINT ii_iu_unit_ref_fk FOREIGN KEY ii_iu_unit_ref_idx (unit_id) REFERENCES `item_unit`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

