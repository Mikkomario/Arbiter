--
-- Arbiter database structure
-- Version: v1.4
-- Type: Full
--

-- STRUCTURE    ----------------------------

-- Trove DB Structure

-- Creates a table that records database version updates
CREATE TABLE database_version
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    version VARCHAR(16) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX dv_version_idx (version),
    INDEX dv_creation_idx (created)

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;



--
-- Database structure for Citadel models
-- Version: v2.1
-- Last generated: 2022-02-27
--

--	Language	----------

-- Represents a language
-- iso_code: 2 letter ISO-standard code for this language
-- created:  Time when this language was first created
CREATE TABLE `language`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`iso_code` VARCHAR(2) NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	INDEX l_iso_code_idx (`iso_code`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a language skill level
-- order_index: Index used for ordering between language familiarities, where lower values mean higher familiarity
-- created:     Time when this language familiarity was first created
CREATE TABLE `language_familiarity`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`order_index` TINYINT NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	INDEX lf_order_index_idx (`order_index`)
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	User	----------

-- Represents a software user
-- created: Time when this user was first created
CREATE TABLE `user`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links user with their language familiarity levels
-- user_id:        Id of the user who's being described
-- language_id:    Id of the language known to the user
-- familiarity_id: Id of the user's familiarity level in the referenced language
-- created:        Time when this user language link was first created
CREATE TABLE `user_language`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`user_id` INT NOT NULL,
	`language_id` INT NOT NULL,
	`familiarity_id` INT NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT ul_u_user_ref_fk FOREIGN KEY ul_u_user_ref_idx (user_id) REFERENCES `user`(`id`) ON DELETE CASCADE,
	CONSTRAINT ul_l_language_ref_fk FOREIGN KEY ul_l_language_ref_idx (language_id) REFERENCES `language`(`id`) ON DELETE CASCADE,
	CONSTRAINT ul_lf_familiarity_ref_fk FOREIGN KEY ul_lf_familiarity_ref_idx (familiarity_id) REFERENCES `language_familiarity`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Versioned user-specific settings
-- user_id:          Id of the described user
-- name:             Name used by this user
-- email:            Email address of this user
-- created:          Time when this user settings was first created
-- deprecated_after: Time when these settings were replaced with a more recent version (if applicable)
CREATE TABLE `user_settings`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`user_id` INT NOT NULL,
	`name` VARCHAR(64) NOT NULL,
	`email` VARCHAR(128),
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deprecated_after` DATETIME,
	INDEX us_name_idx (`name`),
	INDEX us_email_idx (`email`),
	INDEX us_created_idx (`created`),
	INDEX us_deprecated_after_idx (`deprecated_after`),
	CONSTRAINT us_u_user_ref_fk FOREIGN KEY us_u_user_ref_idx (user_id) REFERENCES `user`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Organization	----------

-- Represents an organization or a user group
-- creator_id: Id of the user who created this organization (if still known)
-- created:    Time when this organization was first created
CREATE TABLE `organization`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT o_u_creator_ref_fk FOREIGN KEY o_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a type of task a user can perform (within an organization)
-- created: Time when this task was first created
CREATE TABLE `task`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- An enumeration for different roles a user may have within an organization
-- created: Time when this user role was first created
CREATE TABLE `user_role`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an invitation to join an organization
-- organization_id:  Id of the organization which the recipient is invited to join
-- starting_role_id: The role the recipient will have in the organization initially if they join
-- expires:          Time when this invitation expires / becomes invalid
-- recipient_id:     Id of the invited user, if known
-- recipient_email:  Email address of the invited user / the email address where this invitation is sent to
-- message:          Message written by the sender to accompany this invitation
-- sender_id:        Id of the user who sent this invitation, if still known
-- created:          Time when this invitation was created / sent
CREATE TABLE `invitation`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`organization_id` INT NOT NULL,
	`starting_role_id` INT NOT NULL,
	`expires` DATETIME NOT NULL,
	`recipient_id` INT,
	`recipient_email` VARCHAR(128),
	`message` VARCHAR(480),
	`sender_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	INDEX i_created_idx (`created`),
	INDEX i_combo_1_idx (expires, recipient_email),
	CONSTRAINT i_o_organization_ref_fk FOREIGN KEY i_o_organization_ref_idx (organization_id) REFERENCES `organization`(`id`) ON DELETE CASCADE,
	CONSTRAINT i_ur_starting_role_ref_fk FOREIGN KEY i_ur_starting_role_ref_idx (starting_role_id) REFERENCES `user_role`(`id`) ON DELETE CASCADE,
	CONSTRAINT i_u_recipient_ref_fk FOREIGN KEY i_u_recipient_ref_idx (recipient_id) REFERENCES `user`(`id`) ON DELETE SET NULL,
	CONSTRAINT i_u_sender_ref_fk FOREIGN KEY i_u_sender_ref_idx (sender_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Lists organization members, including membership history
-- organization_id: Id of the organization the referenced user is/was a member of
-- user_id:         Id of the user who is/was a member of the referenced organization
-- creator_id:      Id of the user who created/started this membership
-- started:         Time when this membership started
-- ended:           Time when this membership ended (if applicable)
CREATE TABLE `membership`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`organization_id` INT NOT NULL,
	`user_id` INT NOT NULL,
	`creator_id` INT,
	`started` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`ended` DATETIME,
	INDEX m_started_idx (`started`),
	INDEX m_ended_idx (`ended`),
	CONSTRAINT m_o_organization_ref_fk FOREIGN KEY m_o_organization_ref_idx (organization_id) REFERENCES `organization`(`id`) ON DELETE CASCADE,
	CONSTRAINT m_u_user_ref_fk FOREIGN KEY m_u_user_ref_idx (user_id) REFERENCES `user`(`id`) ON DELETE CASCADE,
	CONSTRAINT m_u_creator_ref_fk FOREIGN KEY m_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a request to delete an organization. There exists a time period between the request and its completion, during which other users may cancel the deletion.
-- organization_id: Id of the organization whose deletion was requested
-- actualization:   Time when this deletion is/was scheduled to actualize
-- creator_id:      Id of the user who requested organization deletion
-- created:         Time when this deletion was requested
CREATE TABLE `organization_deletion`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`organization_id` INT NOT NULL,
	`actualization` DATETIME NOT NULL,
	`creator_id` INT NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	INDEX orgdel_actualization_idx (`actualization`),
	INDEX orgdel_created_idx (`created`),
	CONSTRAINT orgdel_o_organization_ref_fk FOREIGN KEY orgdel_o_organization_ref_idx (organization_id) REFERENCES `organization`(`id`) ON DELETE CASCADE,
	CONSTRAINT orgdel_u_creator_ref_fk FOREIGN KEY orgdel_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Used for listing / linking, which tasks different organization membership roles allow
-- role_id: Id of the organization user role that has authorization to perform the referenced task
-- task_id: Id of the task the user's with referenced user role are allowed to perform
-- created: Time when this user role right was first created
CREATE TABLE `user_role_right`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`role_id` INT NOT NULL,
	`task_id` INT NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT urr_ur_role_ref_fk FOREIGN KEY urr_ur_role_ref_idx (role_id) REFERENCES `user_role`(`id`) ON DELETE CASCADE,
	CONSTRAINT urr_t_task_ref_fk FOREIGN KEY urr_t_task_ref_idx (task_id) REFERENCES `task`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a response (yes|no) to an invitation to join an organization
-- invitation_id: Id of the invitation this response is for
-- message:       Attached written response
-- creator_id:    Id of the user who responded to the invitation, if still known
-- created:       Time when this invitation response was first created
-- accepted:      Whether the invitation was accepted (true) or rejected (false)
-- blocked:       Whether future invitations were blocked
CREATE TABLE `invitation_response`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`invitation_id` INT NOT NULL,
	`message` VARCHAR(480),
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`accepted` BOOLEAN NOT NULL DEFAULT FALSE,
	`blocked` BOOLEAN NOT NULL DEFAULT FALSE,
	INDEX ir_created_idx (`created`),
	CONSTRAINT ir_i_invitation_ref_fk FOREIGN KEY ir_i_invitation_ref_idx (invitation_id) REFERENCES `invitation`(`id`) ON DELETE CASCADE,
	CONSTRAINT ir_u_creator_ref_fk FOREIGN KEY ir_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links an organization membership to the roles that member has within that organization
-- membership_id:    Id of the membership / member that has the referenced role
-- role_id:          Id of role the referenced member has
-- creator_id:       Id of the user who added this role to the membership, if known
-- created:          Time when this role was added for the organization member
-- deprecated_after: Time when this member role link became deprecated. None while this member role link is still valid.
CREATE TABLE `member_role`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`membership_id` INT NOT NULL,
	`role_id` INT NOT NULL,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deprecated_after` DATETIME,
	INDEX mr_deprecated_after_idx (`deprecated_after`),
	CONSTRAINT mr_m_membership_ref_fk FOREIGN KEY mr_m_membership_ref_idx (membership_id) REFERENCES `membership`(`id`) ON DELETE CASCADE,
	CONSTRAINT mr_ur_role_ref_fk FOREIGN KEY mr_ur_role_ref_idx (role_id) REFERENCES `user_role`(`id`) ON DELETE CASCADE,
	CONSTRAINT mr_u_creator_ref_fk FOREIGN KEY mr_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Records a cancellation for a pending organization deletion request
-- deletion_id: Id of the cancelled deletion
-- creator_id:  Id of the user who cancelled the referenced organization deletion, if still known
-- created:     Time when this organization deletion cancellation was first created
CREATE TABLE `organization_deletion_cancellation`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`deletion_id` INT NOT NULL,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	INDEX odc_created_idx (`created`),
	CONSTRAINT odc_orgdel_deletion_ref_fk FOREIGN KEY odc_orgdel_deletion_ref_idx (deletion_id) REFERENCES `organization_deletion`(`id`) ON DELETE CASCADE,
	CONSTRAINT odc_u_creator_ref_fk FOREIGN KEY odc_u_creator_ref_idx (creator_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


--	Description	----------

-- An enumeration for different roles or purposes a description can serve
-- json_key_singular: Key used in json documents for a singular value (string) of this description role
-- json_key_plural:   Key used in json documents for multiple values (array) of this description role
-- created:           Time when this description role was first created
CREATE TABLE `description_role`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`json_key_singular` VARCHAR(32) NOT NULL,
	`json_key_plural` VARCHAR(32) NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents some description of some item in some language
-- role_id:          Id of the role of this description
-- language_id:      Id of the language this description is written in
-- text:             This description as text / written description
-- author_id:        Id of the user who wrote this description (if known and applicable)
-- created:          Time when this description was written
-- deprecated_after: Time when this description was removed or replaced with a new version
CREATE TABLE `description`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`role_id` INT NOT NULL,
	`language_id` INT NOT NULL,
	`text` VARCHAR(64) NOT NULL,
	`author_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deprecated_after` DATETIME,
	INDEX d_created_idx (`created`),
	INDEX d_deprecated_after_idx (`deprecated_after`),
	CONSTRAINT d_dr_role_ref_fk FOREIGN KEY d_dr_role_ref_idx (role_id) REFERENCES `description_role`(`id`) ON DELETE CASCADE,
	CONSTRAINT d_l_language_ref_fk FOREIGN KEY d_l_language_ref_idx (language_id) REFERENCES `language`(`id`) ON DELETE CASCADE,
	CONSTRAINT d_u_author_ref_fk FOREIGN KEY d_u_author_ref_idx (author_id) REFERENCES `user`(`id`) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links DescriptionRoles with their descriptions
-- role_id:        Id of the described description role
-- description_id: Id of the linked description
CREATE TABLE `description_role_description`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`role_id` INT NOT NULL,
	`description_id` INT NOT NULL,
	CONSTRAINT drd_dr_role_ref_fk FOREIGN KEY drd_dr_role_ref_idx (role_id) REFERENCES `description_role`(`id`) ON DELETE CASCADE,
	CONSTRAINT drd_d_description_ref_fk FOREIGN KEY drd_d_description_ref_idx (description_id) REFERENCES `description`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links Languages with their descriptions
-- language_id:    Id of the described language
-- description_id: Id of the linked description
CREATE TABLE `language_description`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`language_id` INT NOT NULL,
	`description_id` INT NOT NULL,
	CONSTRAINT ld_l_language_ref_fk FOREIGN KEY ld_l_language_ref_idx (language_id) REFERENCES `language`(`id`) ON DELETE CASCADE,
	CONSTRAINT ld_d_description_ref_fk FOREIGN KEY ld_d_description_ref_idx (description_id) REFERENCES `description`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links LanguageFamiliarities with their descriptions
-- familiarity_id: Id of the described language familiarity
-- description_id: Id of the linked description
CREATE TABLE `language_familiarity_description`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`familiarity_id` INT NOT NULL,
	`description_id` INT NOT NULL,
	CONSTRAINT lfd_lf_familiarity_ref_fk FOREIGN KEY lfd_lf_familiarity_ref_idx (familiarity_id) REFERENCES `language_familiarity`(`id`) ON DELETE CASCADE,
	CONSTRAINT lfd_d_description_ref_fk FOREIGN KEY lfd_d_description_ref_idx (description_id) REFERENCES `description`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links Organizations with their descriptions
-- organization_id: Id of the described organization
-- description_id:  Id of the linked description
CREATE TABLE `organization_description`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`organization_id` INT NOT NULL,
	`description_id` INT NOT NULL,
	CONSTRAINT orgdes_o_organization_ref_fk FOREIGN KEY orgdes_o_organization_ref_idx (organization_id) REFERENCES `organization`(`id`) ON DELETE CASCADE,
	CONSTRAINT orgdes_d_description_ref_fk FOREIGN KEY orgdes_d_description_ref_idx (description_id) REFERENCES `description`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links Tasks with their descriptions
-- task_id:        Id of the described task
-- description_id: Id of the linked description
CREATE TABLE `task_description`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`task_id` INT NOT NULL,
	`description_id` INT NOT NULL,
	CONSTRAINT td_t_task_ref_fk FOREIGN KEY td_t_task_ref_idx (task_id) REFERENCES `task`(`id`) ON DELETE CASCADE,
	CONSTRAINT td_d_description_ref_fk FOREIGN KEY td_d_description_ref_idx (description_id) REFERENCES `description`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links UserRoles with their descriptions
-- role_id:        Id of the described user role
-- description_id: Id of the linked description
CREATE TABLE `user_role_description`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`role_id` INT NOT NULL,
	`description_id` INT NOT NULL,
	CONSTRAINT urd_ur_role_ref_fk FOREIGN KEY urd_ur_role_ref_idx (role_id) REFERENCES `user_role`(`id`) ON DELETE CASCADE,
	CONSTRAINT urd_d_description_ref_fk FOREIGN KEY urd_d_description_ref_idx (description_id) REFERENCES `description`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;




--
-- Arbiter Core DB Structure
-- Supports versions v1.1 and above
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
	CONSTRAINT cdt_u_creator_ref_fk FOREIGN KEY cdt_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
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


--
-- Arbiter Command DB Structure
-- Supports versions v1.1 and above
--

-- Lists times when different description files were last read
-- path: Path to the read file
-- created: Time when this file was read
CREATE TABLE `description_import`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`path` VARCHAR(255) NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Stores information about invoice form locations
-- owner_id: Id of the user who uses this form
-- language_id: Id of the language this form uses
-- company_id: Id of the company for which this form is used (if used for a specific company)
-- path: Path to the form file in the local file system
CREATE TABLE `invoice_form`(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`owner_id` INT NOT NULL,
	`language_id` INT NOT NULL,
	`company_id` INT,
	`path` VARCHAR(255) NOT NULL,
	CONSTRAINT if_u_owner_ref_fk FOREIGN KEY if_u_owner_ref_idx (owner_id) REFERENCES `user`(id) ON DELETE CASCADE,
	CONSTRAINT if_l_language_ref_fk FOREIGN KEY if_l_language_ref_idx (language_id) REFERENCES `language`(id) ON DELETE CASCADE,
	CONSTRAINT if_c_company_ref_fk FOREIGN KEY if_c_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


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


-- DATA  ---------------------------------

--
-- Initial database inserts for Citadel
-- Version: v2.1
-- Last generated: 2022-02-24
--

--	Description	----------

-- Inserts 1 description role
INSERT INTO `description_role` (`id`, `json_key_plural`, `json_key_singular`) VALUES
	(1, 'names', 'name');


--	Language	----------

-- Inserts 1 language
INSERT INTO `language` (`id`, `iso_code`) VALUES
	(1, 'en');

-- Inserts 4 language familiarities
INSERT INTO `language_familiarity` (`id`, `order_index`) VALUES
	(1, 1),
	(2, 5),
	(3, 10),
	(4, 15);


--	Organization	----------

-- Inserts 6 tasks
INSERT INTO `task` (`id`) VALUES
	(1),
	(2),
	(3),
	(4),
	(5),
	(6);

-- Inserts 2 user roles
INSERT INTO `user_role` (`id`) VALUES
	(1),
	(2);

-- Inserts 11 user role rights
INSERT INTO `user_role_right` (`role_id`, `task_id`) VALUES
	(1, 1),
	(1, 2),
	(1, 3),
	(1, 4),
	(1, 5),
	(1, 6),
	(2, 2),
	(2, 3),
	(2, 4),
	(2, 5),
	(2, 6);


--
-- Inserts the initial Arbiter project data to the DB
-- Intended to follow Citadel data insert
-- Supports versions v0.2 and above
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
-- Percent (7)
INSERT INTO item_unit (id, category_id, multiplier) VALUES
    (1, 1, 1.0),
    (2, 2, 1.0),
    (3, 2, 60.0),
    (4, 3, 1.0),
    (5, 4, 1.0),
    (6, 4, 12.0),
    (7, 1, 0.01);
