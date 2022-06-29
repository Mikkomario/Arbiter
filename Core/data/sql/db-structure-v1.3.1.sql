--
-- Arbiter Core DB Structure
-- Supports versions v1.1 and above
-- Type: Full
-- Version: v1.3
--

-- Represents different categories a unit can belong to. Units within a category can be compared.
CREATE TABLE `unit_category`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a bank (used with bank addresses)
-- name: (Short) name of this bank
-- bic: BIC-code of this bank
-- creator_id: Id of the user who registered this address
-- created: Time when this Bank was first created
CREATE TABLE `bank`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`name` VARCHAR(64) NOT NULL, 
	`bic` VARCHAR(12) NOT NULL, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX b_name_idx (`name`), 
	INDEX b_bic_idx (`bic`), 
	CONSTRAINT b_u_creator_ref_fk FOREIGN KEY b_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a registered company (or an individual person)
-- y_code: Official registration code of this company (id in the country system)
-- creator_id: Id of the user who registered this company to this system
-- created: Time when this company was registered
CREATE TABLE `company`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`y_code` VARCHAR(10) NOT NULL, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX com_y_code_idx (`y_code`), 
	INDEX com_created_idx (`created`), 
	CONSTRAINT com_u_creator_ref_fk FOREIGN KEY com_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a county within a country
-- name: County name, with that county's or country's primary language
-- creator_id: Id of the user who registered this county
-- created: Time when this County was first created
CREATE TABLE `county`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`name` VARCHAR(64) NOT NULL, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX cou_name_idx (`name`), 
	CONSTRAINT cou_u_creator_ref_fk FOREIGN KEY cou_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a unit in which items can be counted
-- category_id: Id of the category this unit belongs to
-- multiplier: A multiplier that, when applied to this unit, makes it comparable with the other units in the same category
CREATE TABLE `item_unit`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`category_id` INT NOT NULL, 
	`multiplier` DOUBLE NOT NULL, 
	CONSTRAINT iu_uc_category_ref_fk FOREIGN KEY iu_uc_category_ref_idx (category_id) REFERENCES `unit_category`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a postal code, which represents an area within a county
-- number: The number portion of this postal code
-- county_id: Id of the county where this postal code is resides
-- creator_id: Id of the user linked with this PostalCode
-- created: Time when this PostalCode was first created
CREATE TABLE `postal_code`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`number` VARCHAR(5) NOT NULL, 
	`county_id` INT NOT NULL, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX pc_number_idx (`number`), 
	CONSTRAINT pc_cou_county_ref_fk FOREIGN KEY pc_cou_county_ref_idx (county_id) REFERENCES `county`(id) ON DELETE CASCADE, 
	CONSTRAINT pc_u_creator_ref_fk FOREIGN KEY pc_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links UnitCategories with their descriptions
-- unit_category_id: Id of the described UnitCategory
-- description_id: Id of the linked description
CREATE TABLE `unit_category_description`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`unit_category_id` INT NOT NULL, 
	`description_id` INT NOT NULL, 
	CONSTRAINT ucd_uc_unit_category_ref_fk FOREIGN KEY ucd_uc_unit_category_ref_idx (unit_category_id) REFERENCES `unit_category`(id) ON DELETE CASCADE, 
	CONSTRAINT ucd_d_description_ref_fk FOREIGN KEY ucd_d_description_ref_idx (description_id) REFERENCES `description`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Used for listing which bank accounts belong to which company
-- company_id: Id of the company which owns this bank account
-- bank_id: Id of the bank where the company owns an account
-- address: The linked bank account address
-- creator_id: Id of the user linked with this CompanyBankAccount
-- created: Time when this information was registered
-- deprecated_after: Time when this CompanyBankAccount became deprecated. None while this CompanyBankAccount is still valid.
-- is_official: Whether this bank account information was written by the company authorities
CREATE TABLE `company_bank_account`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`company_id` INT NOT NULL, 
	`bank_id` INT NOT NULL, 
	`address` VARCHAR(32) NOT NULL, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	`is_official` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX cba_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT cba_com_company_ref_fk FOREIGN KEY cba_com_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE CASCADE, 
	CONSTRAINT cba_b_bank_ref_fk FOREIGN KEY cba_b_bank_ref_idx (bank_id) REFERENCES `bank`(id) ON DELETE CASCADE, 
	CONSTRAINT cba_u_creator_ref_fk FOREIGN KEY cba_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Connects organizations with their owned companies
-- organization_id: Id of the owner organization
-- company_id: Id of the owned company
-- creator_id: Id of the user who created this link
-- created: Time when this OrganizationCompany was first created
CREATE TABLE `organization_company`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`organization_id` INT NOT NULL, 
	`company_id` INT NOT NULL, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	CONSTRAINT oc_o_organization_ref_fk FOREIGN KEY oc_o_organization_ref_idx (organization_id) REFERENCES `organization`(id) ON DELETE CASCADE, 
	CONSTRAINT oc_com_company_ref_fk FOREIGN KEY oc_com_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE CASCADE, 
	CONSTRAINT oc_u_creator_ref_fk FOREIGN KEY oc_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links ItemUnits with their descriptions
-- unit_id: Id of the described ItemUnit
-- description_id: Id of the linked description
CREATE TABLE `item_unit_description`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`unit_id` INT NOT NULL, 
	`description_id` INT NOT NULL, 
	CONSTRAINT iud_iu_unit_ref_fk FOREIGN KEY iud_iu_unit_ref_idx (unit_id) REFERENCES `item_unit`(id) ON DELETE CASCADE, 
	CONSTRAINT iud_d_description_ref_fk FOREIGN KEY iud_d_description_ref_idx (description_id) REFERENCES `description`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a specific street address
-- postal_code_id: Id of the postal_code linked with this StreetAddress
-- street_name: Name of the street -portion of this address
-- building_number: Number of the targeted building within the specified street
-- stair: Number or letter of the targeted stair within that building, if applicable
-- room_number: Number of the targeted room within that stair / building, if applicable
-- creator_id: Id of the user who registered this address
-- created: Time when this StreetAddress was first created
CREATE TABLE `street_address`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`postal_code_id` INT NOT NULL, 
	`street_name` VARCHAR(64) NOT NULL, 
	`building_number` VARCHAR(10) NOT NULL, 
	`stair` VARCHAR(10), 
	`room_number` VARCHAR(10), 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX sa_combo_1_idx (postal_code_id, street_name, building_number, stair, room_number), 
	CONSTRAINT sa_pc_postal_code_ref_fk FOREIGN KEY sa_pc_postal_code_ref_idx (postal_code_id) REFERENCES `postal_code`(id) ON DELETE CASCADE, 
	CONSTRAINT sa_u_creator_ref_fk FOREIGN KEY sa_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a type of product sold by an individual company
-- company_id: Id of the company that owns this product type
-- unit_id: Id representing the units in which this product or service is sold
-- default_unit_price: Default € price per single unit of this product
-- tax_modifier: A modifier that is applied to this product's price to get the applied tax
-- creator_id: Id of the user linked with this CompanyProduct
-- created: Time when this product was registered
-- discontinued_after: Time when this product was discontinued (no longer sold)
CREATE TABLE `company_product`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`company_id` INT NOT NULL, 
	`unit_id` INT NOT NULL, 
	`default_unit_price` DOUBLE, 
	`tax_modifier` DOUBLE NOT NULL, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`discontinued_after` DATETIME, 
	INDEX cp_created_idx (`created`), 
	INDEX cp_combo_1_idx (discontinued_after, created), 
	CONSTRAINT cp_com_company_ref_fk FOREIGN KEY cp_com_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE CASCADE, 
	CONSTRAINT cp_iu_unit_ref_fk FOREIGN KEY cp_iu_unit_ref_idx (unit_id) REFERENCES `item_unit`(id) ON DELETE CASCADE, 
	CONSTRAINT cp_u_creator_ref_fk FOREIGN KEY cp_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links CompanyProducts with their descriptions
-- product_id: Id of the described CompanyProduct
-- description_id: Id of the linked description
CREATE TABLE `company_product_description`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`product_id` INT NOT NULL, 
	`description_id` INT NOT NULL, 
	CONSTRAINT cpd_cp_product_ref_fk FOREIGN KEY cpd_cp_product_ref_idx (product_id) REFERENCES `company_product`(id) ON DELETE CASCADE, 
	CONSTRAINT cpd_d_description_ref_fk FOREIGN KEY cpd_d_description_ref_idx (description_id) REFERENCES `description`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Contains company information which may change and on which there may be varying views
-- company_id: Id of the company which this describes
-- name: Name of this company
-- address_id: Street address of this company's headquarters or operation
-- tax_code: Tax-related identifier code for this company
-- creator_id: Id of the user who wrote this description
-- created: Time when this CompanyDetails was first created
-- deprecated_after: Time when this CompanyDetails became deprecated. None while this CompanyDetails is still valid.
-- is_official: Whether this information is by the company which is being described, having a more authority
CREATE TABLE `company_details`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`company_id` INT NOT NULL, 
	`name` VARCHAR(64) NOT NULL, 
	`address_id` INT NOT NULL, 
	`tax_code` VARCHAR(16), 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	`is_official` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX cd_name_idx (`name`), 
	INDEX cd_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT cd_com_company_ref_fk FOREIGN KEY cd_com_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE CASCADE, 
	CONSTRAINT cd_sa_address_ref_fk FOREIGN KEY cd_sa_address_ref_idx (address_id) REFERENCES `street_address`(id) ON DELETE CASCADE, 
	CONSTRAINT cd_u_creator_ref_fk FOREIGN KEY cd_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a bill / an invoice sent by one company to another to request a monetary transfer / payment
-- Represents a bill / an invoice sent by one company to another to request a monetary transfer / payment
-- sender_company_details_id:    Id of the details of the company who sent this invoice (payment recipient)
-- recipient_company_details_id: Id of the details of the recipient company used in this invoice
-- sender_bank_account_id:       Id of the bank account the invoice sender wants the recipient to transfer money to
-- language_id:                  Id of the language used in this invoice
-- reference_code:               A custom reference code used by the sender to identify this invoice
-- payment_duration_days:        Number of days during which this invoice can be paid before additional consequences
-- product_delivery_begin:       The first date when the products were delivered, if applicable
-- product_delivery_end:         The last date when the invoiced products were delivered, if applicable
-- creator_id:                   Id of the user who created this invoice
-- created:                      Time when this invoice was first created
-- cancelled_after:              Time when this invoice became deprecated. None while this invoice is still valid.
CREATE TABLE `invoice`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`sender_company_details_id` INT NOT NULL,
	`recipient_company_details_id` INT NOT NULL,
	`sender_bank_account_id` INT NOT NULL,
	`language_id` INT NOT NULL,
	`reference_code` VARCHAR(16) NOT NULL,
	`payment_duration_days` INT NOT NULL DEFAULT 0,
	`product_delivery_begin` DATE,
	`product_delivery_end` DATE,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`cancelled_after` DATETIME,
	INDEX i_reference_code_idx (`reference_code`),
	INDEX i_created_idx (`created`),
	INDEX i_cancelled_after_idx (`cancelled_after`),
	CONSTRAINT i_cd_sender_company_details_ref_fk FOREIGN KEY i_cd_sender_company_details_ref_idx (sender_company_details_id) REFERENCES `company_details`(`id`) ON DELETE CASCADE,
	CONSTRAINT i_cd_recipient_company_details_ref_fk FOREIGN KEY i_cd_recipient_company_details_ref_idx (recipient_company_details_id) REFERENCES `company_details`(`id`) ON DELETE CASCADE,
	CONSTRAINT i_cba_sender_bank_account_ref_fk FOREIGN KEY i_cba_sender_bank_account_ref_idx (sender_bank_account_id) REFERENCES `company_bank_account`(`id`) ON DELETE CASCADE,
	CONSTRAINT i_l_language_ref_fk FOREIGN KEY i_l_language_ref_idx (language_id) REFERENCES `language`(`id`) ON DELETE CASCADE,
	CONSTRAINT i_u_creator_ref_fk FOREIGN KEY i_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a payment event concerning an invoice you have sent
-- invoice_id: Id of the invoice that was paid
-- date: Date when this payment was received
-- received_amount: Received amount in €, including taxes
-- remarks: Free remarks concerning this payment
-- created: Time when this payment was registered
CREATE TABLE `invoice_payment`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`invoice_id` INT NOT NULL, 
	`date` DATE NOT NULL, 
	`received_amount` DOUBLE NOT NULL, 
	`remarks` VARCHAR(1028), 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX ip_date_idx (`date`), 
	CONSTRAINT ip_i_invoice_ref_fk FOREIGN KEY ip_i_invoice_ref_idx (invoice_id) REFERENCES `invoice`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an individual sold item or service
-- invoice_id: Id of the invoice on which this item appears
-- product_id: Id of the type of product this item represents / is
-- description: Name or description of this item (in the same language the invoice is given in)
-- price_per_unit: Euro (€) price per each sold unit of this item, without taxes applied
-- units_sold: Amount of items sold in the product's unit
CREATE TABLE `invoice_item`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`invoice_id` INT NOT NULL, 
	`product_id` INT NOT NULL, 
	`description` VARCHAR(255) NOT NULL, 
	`price_per_unit` DOUBLE NOT NULL, 
	`units_sold` DOUBLE NOT NULL, 
	CONSTRAINT ii_i_invoice_ref_fk FOREIGN KEY ii_i_invoice_ref_idx (invoice_id) REFERENCES `invoice`(id) ON DELETE CASCADE, 
	CONSTRAINT ii_cp_product_ref_fk FOREIGN KEY ii_cp_product_ref_idx (product_id) REFERENCES `company_product`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

