-- Represents a bill / an invoice sent by one company to another to request a monetary transfer / payment
-- sender_company_id: Id of the company who sent this invoice (payment recipient)
-- recipient_company_id: Id of the recipient company of this invoice
-- reference_code: A custom reference code used by the sender to identify this invoice
-- created: Time when this Invoice was first created
-- creator_id: Id of the user who created this invoice
-- payment_duration_days: Number of days during which this invoice can be paid before additional consequences
-- product_delivery_date: Date when the sold products were delivered, if applicable
CREATE TABLE invoice(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`sender_company_id` INT NOT NULL, 
	`recipient_company_id` INT NOT NULL, 
	`reference_code` VARCHAR(16) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`creator_id` INT NOT NULL, 
	`payment_duration_days` INT NOT NULL, 
	`product_delivery_date` DATE, 
	INDEX i_created_idx (`created)`, 
	CONSTRAINT i_c_sender_company_ref_fk FOREIGN KEY i_c_sender_company_ref_idx (sender_company_id) REFERENCES `company`(id) ON DELETE CASCADE, 
	CONSTRAINT i_c_recipient_company_ref_fk FOREIGN KEY i_c_recipient_company_ref_idx (recipient_company_id) REFERENCES `company`(id) ON DELETE CASCADE, 
	CONSTRAINT i_u_creator_ref_fk FOREIGN KEY i_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE CASCADE
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

