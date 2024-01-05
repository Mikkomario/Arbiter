-- 
-- Database structure for arbiter accounting models
-- Version: v1.5
-- Last generated: 2024-01-04
--

--	Transaction	----------

-- Represents a transaction party as described on a bank statement
-- name:    Name of this entity, just as it appeared on a bank statement
-- created: Time when this party entry was added to the database
CREATE TABLE `party_entry`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`name` VARCHAR(16) NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	INDEX pe_name_idx (`name`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a category or a type of transaction
-- parent_id:   Id of the parent type of this type. None if this is a root/main category.
-- creator_id:  Reference to the user that created this transaction type
-- created:     Time when this transaction type was added to the database
-- pre_applied: Whether these transaction types should be immediately as income or expense, before targets are applied. 
-- 		E.g. some expenses may be deducted from income instead of considered additional spending. 
-- 		Main input sources should also be pre-applied.
CREATE TABLE `transaction_type`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`parent_id` INT, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`pre_applied` BOOLEAN NOT NULL DEFAULT FALSE, 
	CONSTRAINT tt_tt_parent_ref_fk FOREIGN KEY tt_tt_parent_ref_idx (parent_id) REFERENCES `transaction_type`(`id`) ON DELETE SET NULL, 
	CONSTRAINT tt_u_creator_ref_fk FOREIGN KEY tt_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a transaction from or to a bank account. Only includes data from a bank statement.
-- account_id:           Id of the account on which this transaction was made
-- date:                 Date when this transaction occurred (e.g. date of purchase)
-- record_date:          Date when this transaction was recorded / actuated on the bank account.
-- amount:               The size of the transaction in €. Positive amounts indicate balance added to the account. Negative values indicate withdrawals or purchases.
-- other_party_entry_id: Id of the other party of this transaction, as it appears on this original statement.
-- reference_code:       Reference code mentioned on the transaction. May be empty.
-- creator_id:           Id of the user who added this entry. None if unknown or if not applicable.
-- created:              Time when this transaction was added to the database
-- deprecated_after:     Time when this entry was cancelled / removed. None while valid.
CREATE TABLE `transaction`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`account_id` INT NOT NULL, 
	`date` DATE NOT NULL, 
	`record_date` DATE NOT NULL, 
	`amount` DOUBLE NOT NULL, 
	`other_party_entry_id` INT NOT NULL, 
	`reference_code` VARCHAR(8), 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX t_date_idx (`date`), 
	INDEX t_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT t_cba_account_ref_fk FOREIGN KEY t_cba_account_ref_idx (account_id) REFERENCES `company_bank_account`(`id`) ON DELETE CASCADE, 
	CONSTRAINT t_pe_other_party_entry_ref_fk FOREIGN KEY t_pe_other_party_entry_ref_idx (other_party_entry_id) REFERENCES `party_entry`(`id`) ON DELETE CASCADE, 
	CONSTRAINT t_u_creator_ref_fk FOREIGN KEY t_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links an invoice with the transaction where it was paid
-- invoice_id:     Id of the paid invoice
-- transaction_id: Id of the transaction that paid the linked invoice
-- creator_id:     Id of the user who registered this connection
-- created:        Time when this link was registered
-- manual:         Whether this connection was made manually (true), or whether it was determined by an automated algorithm (false)
CREATE TABLE `invoice_payment`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`invoice_id` INT NOT NULL, 
	`transaction_id` INT NOT NULL, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`manual` BOOLEAN NOT NULL DEFAULT FALSE, 
	CONSTRAINT ip_i_invoice_ref_fk FOREIGN KEY ip_i_invoice_ref_idx (invoice_id) REFERENCES `invoice`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ip_t_transaction_ref_fk FOREIGN KEY ip_t_transaction_ref_idx (transaction_id) REFERENCES `transaction`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ip_u_creator_ref_fk FOREIGN KEY ip_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Lists information that's added on top of a raw transaction record. Typically based on user input.
-- transaction_id:    Id of the described transaction.
-- type_id:           Id of the assigned type of this transaction
-- vat_ratio:         Ratio of VAT applied to this transaction, where 0.5 is 50% and 1.0 is 100%.
-- other_party_alias: An alias given to the other party of this transaction. Empty if no alias has been specified.
-- creator_id:        Id of the user who added this evaluation. None if unknown or if not applicable.
-- created:           Time when this transaction evaluation was added to the database
-- deprecated_after:  Time when this evaluation was replaced or cancelled. None while valid.
-- manual:            Whether this evaluation is manually performed by a human. False if performed by an algorithm.
CREATE TABLE `transaction_evaluation`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`transaction_id` INT NOT NULL, 
	`type_id` INT NOT NULL, 
	`vat_ratio` DOUBLE NOT NULL DEFAULT 0.0, 
	`other_party_alias` VARCHAR(8), 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	`manual` BOOLEAN NOT NULL DEFAULT FALSE, 
	INDEX te_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT te_t_transaction_ref_fk FOREIGN KEY te_t_transaction_ref_idx (transaction_id) REFERENCES `transaction`(`id`) ON DELETE CASCADE, 
	CONSTRAINT te_tt_type_ref_fk FOREIGN KEY te_tt_type_ref_idx (type_id) REFERENCES `transaction_type`(`id`) ON DELETE CASCADE, 
	CONSTRAINT te_u_creator_ref_fk FOREIGN KEY te_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Description	----------

-- Links Transactions with their descriptions
-- transaction_id: Id of the described transaction
-- description_id: Id of the linked description
CREATE TABLE `transaction_description`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`transaction_id` INT NOT NULL, 
	`description_id` INT NOT NULL, 
	CONSTRAINT td_t_transaction_ref_fk FOREIGN KEY td_t_transaction_ref_idx (transaction_id) REFERENCES `transaction`(`id`) ON DELETE CASCADE, 
	CONSTRAINT td_d_description_ref_fk FOREIGN KEY td_d_description_ref_idx (description_id) REFERENCES `description`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links TransactionTypes with their descriptions
-- type_id:        Id of the described transaction type
-- description_id: Id of the linked description
CREATE TABLE `transaction_type_description`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`type_id` INT NOT NULL, 
	`description_id` INT NOT NULL, 
	CONSTRAINT ttd_tt_type_ref_fk FOREIGN KEY ttd_tt_type_ref_idx (type_id) REFERENCES `transaction_type`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ttd_d_description_ref_fk FOREIGN KEY ttd_d_description_ref_idx (description_id) REFERENCES `description`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Target	----------

-- Represents a goal or a target for money-allocation
-- company_id:       Id of the company for which these targets apply
-- capital_ratio:    The targeted (minimum) capital ratio of the income (possibly after certain expenses) [0,1].
-- applied_since:    The first time from which this target is applied
-- applied_until:    Time until which this target was applied. None if applied indefinitely (or until changed).
-- creator_id:       Id of the user who specified these targets
-- created:          Time when this target was specified
-- deprecated_after: Time when this target was cancelled / deprecated. Deprecated targets don't apply, not even in post.
CREATE TABLE `allocation_target`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`company_id` INT NOT NULL, 
	`capital_ratio` DOUBLE NOT NULL, 
	`applied_since` DATETIME NOT NULL, 
	`applied_until` DATETIME, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX at_combo_1_idx (deprecated_after, applied_until, applied_since), 
	CONSTRAINT at_c_company_ref_fk FOREIGN KEY at_c_company_ref_idx (company_id) REFERENCES `company`(`id`) ON DELETE CASCADE, 
	CONSTRAINT at_u_creator_ref_fk FOREIGN KEY at_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an allocation / target for a specific transaction-type
-- parent_id:  Id of the target to which this specific value belongs
-- type_id:    Id of the allocated transaction type
-- ratio:      The targeted ratio of total after-expenses income that should be allocated into this transaction type. 
-- 		If a ratio has been specified for a parent transaction type, this ratio is applied to the parent's portion (E.g. ratio of 1.0 would allocate 100% of the parent's share to this child type). 
-- 		[0,1]
-- is_maximum: Whether this target represents the largest allowed value. False if this represents the minimum target.
CREATE TABLE `type_specific_allocation_target`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`parent_id` INT NOT NULL, 
	`type_id` INT NOT NULL, 
	`ratio` DOUBLE NOT NULL, 
	`is_maximum` BOOLEAN NOT NULL DEFAULT FALSE, 
	CONSTRAINT tsat_at_parent_ref_fk FOREIGN KEY tsat_at_parent_ref_idx (parent_id) REFERENCES `allocation_target`(`id`) ON DELETE CASCADE, 
	CONSTRAINT tsat_tt_type_ref_fk FOREIGN KEY tsat_tt_type_ref_idx (type_id) REFERENCES `transaction_type`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Account	----------

-- Represents a balance (monetary amount) on a bank account at a specific time
-- account_id:       Id of the described bank account
-- balance:          The amount of € on this account in this instance
-- creator_id:       Id of the user who provided this information. None if not known or if not applicable.
-- created:          Time when this value was specified. Also represents the time when this value was accurate.
-- deprecated_after: Time when this statement was cancelled. None while valid.
CREATE TABLE `account_balance`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`account_id` INT NOT NULL, 
	`balance` DOUBLE NOT NULL, 
	`creator_id` INT, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX ab_created_idx (`created`), 
	INDEX ab_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT ab_cba_account_ref_fk FOREIGN KEY ab_cba_account_ref_idx (account_id) REFERENCES `company_bank_account`(`id`) ON DELETE CASCADE, 
	CONSTRAINT ab_u_creator_ref_fk FOREIGN KEY ab_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

