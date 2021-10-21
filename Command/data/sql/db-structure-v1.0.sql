--
-- Arbiter database structure
-- Version: v1.0
-- Type: Full
--

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
-- DB Structure for Utopia Citadel features
-- Intended to be inserted after database creation
-- Supports versions v1.2 and above
--

-- Various languages
CREATE TABLE `language`
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    iso_code VARCHAR(2) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX l_iso_code_idx (iso_code)

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- 1 = English
INSERT INTO `language` (id, iso_code) VALUES (1, 'en');

-- Language familiarity levels (how good a user can be using a language)
-- Order index is from most preferred to least preferred (Eg. index 1 is more preferred than index 2)
CREATE TABLE language_familiarity
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_index INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- 1 = Primary language
-- 2 = Fluent and preferred
-- 3 = Fluent
-- 4 = OK
-- 5 = OK, less preferred
-- 6 = Knows a little (better than nothing)
INSERT INTO language_familiarity (id, order_index) VALUES
    (1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6);

-- Describes individual users
CREATE TABLE `user`
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Versioned settings for users
CREATE TABLE user_settings
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(128),
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX us_user_name_idx (name),
    INDEX us_email_idx (email),
    INDEX us_deprecation_idx (deprecated_after),

    CONSTRAINT us_u_described_user_fk FOREIGN KEY us_u_described_user_idx (user_id)
        REFERENCES `user`(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links user's known languages to the user
CREATE TABLE user_language
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    language_id INT NOT NULL,
    familiarity_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ul_u_described_user_fk FOREIGN KEY ul_u_described_user_idx (user_id)
        REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT ul_l_known_language_fk FOREIGN KEY ul_l_known_language_idx (language_id)
        REFERENCES `language`(id) ON DELETE CASCADE,
    CONSTRAINT ul_lf_language_proficiency_fk FOREIGN KEY ul_lf_language_proficiency_idx (familiarity_id)
        REFERENCES language_familiarity(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Enumeration for various description roles (label, use documentation etc.)
CREATE TABLE description_role
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    json_key_singular VARCHAR(32) NOT NULL,
    json_key_plural VARCHAR(32) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- 1 = Name
INSERT INTO description_role (id, json_key_singular, json_key_plural) VALUES (1, 'name', 'names');

-- Descriptions describe various things.
-- Descriptions can be overwritten and are written in a specific language.
-- Usually there is only up to one description available for each item, per language.
CREATE TABLE description
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    role_id INT NOT NULL,
    language_id INT NOT NULL,
    `text` VARCHAR(255) NOT NULL,
    author_id INT,
    created TIMESTAMP NOT NULl DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT d_dr_description_purpose_fk FOREIGN KEY d_dr_description_purpose_idx (role_id)
        REFERENCES description_role(id) ON DELETE CASCADE,
    CONSTRAINT d_l_used_language_fk FOREIGN KEY d_l_used_language_idx (language_id)
        REFERENCES `language`(id) ON DELETE CASCADE,
    CONSTRAINT d_u_description_writer_fk FOREIGN KEY d_u_description_writer_idx (author_id)
        REFERENCES `user`(id) ON DELETE SET NULL

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links descriptions with description roles
CREATE TABLE description_role_description
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    role_id INT NOT NULL,
    description_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX drd_deprecation_idx (deprecated_after),

    CONSTRAINT drd_dr_described_role_fk FOREIGN KEY drd_dr_described_role_idx (role_id)
        REFERENCES description_role(id) ON DELETE CASCADE,
    CONSTRAINT drd_d_description_for_role_fk FOREIGN KEY drd_d_description_for_role_idx (description_id)
        REFERENCES description(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links descriptions with languages
CREATE TABLE language_description
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    language_id INT NOT NULL,
    description_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX ld_deprecation_idx (deprecated_after),

    CONSTRAINT ld_l_described_language_fk FOREIGN KEY ld_l_described_language_idx (language_id)
        REFERENCES `language`(id) ON DELETE CASCADE,
    CONSTRAINT ld_d_description_for_language_fk FOREIGN KEY ld_d_description_for_language_idx (description_id)
        REFERENCES description(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links descriptions with language familiarity levels
CREATE TABLE language_familiarity_description
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    familiarity_id INT NOT NULL,
    description_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX lfd_deprecation_idx (deprecated_after),

    CONSTRAINT lfd_lf_described_familiarity_fk FOREIGN KEY lfd_lf_described_familiarity_idx (familiarity_id)
        REFERENCES language_familiarity(id) ON DELETE CASCADE,
    CONSTRAINT lfd_d_familiarity_description_fk FOREIGN KEY lfd_d_familiarity_description_idx (description_id)
        REFERENCES description(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Organizations represent user groups (Eg. company)
CREATE TABLE organization
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator_id INT,

    CONSTRAINT o_u_organization_founder_fk FOREIGN KEY o_u_organization_founder_idx (creator_id)
        REFERENCES `user`(id) ON DELETE SET NULL

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Names & descriptions for organizations
CREATE TABLE organization_description
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    organization_id INT NOT NULL,
    description_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX od_creation_idx (created),
    INDEX od_deprecation_idx (deprecated_after),

    CONSTRAINT od_o_described_organization_fk FOREIGN KEY od_o_described_organization_idx (organization_id)
        REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT od_d_organization_description_fk FOREIGN KEY od_d_organization_description_idx (description_id)
        REFERENCES description(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represent various tasks or features organization members can perform
CREATE TABLE task
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- 1 = Delete organization
-- 2 = Change user roles (to similar or lower)
-- 3 = Invite new users to organization (with similar or lower role)
-- 4 = Edit organization description (including name)
-- 5 = Remove users (of lower role) from the organization
-- 6 = Cancel organization deletion
INSERT INTO task (id) VALUES (1), (2), (3), (4), (5), (6);

-- Names and descriptions of various tasks
CREATE TABLE task_description
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    task_id INT NOT NULL,
    description_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX td_deprecation_idx (deprecated_after),

    CONSTRAINT td_t_described_task_fk FOREIGN KEY td_t_described_task_idx (task_id)
        REFERENCES task(id) ON DELETE CASCADE,
    CONSTRAINT td_d_task_description_fk FOREIGN KEY td_d_task_description_idx (description_id)
        REFERENCES description(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- An enumeration for various roles within an organization. One user may have multiple roles within an organization.
CREATE TABLE organization_user_role
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- 1 = Owner (all rights)
-- 2 = Admin/Steward (all rights except owner-specific rights)
-- 3 = Manager (rights to modify users)
-- 4 = Developer (rights to create & edit resources and to publish)
-- 5 = Publisher (Read access to data + publish rights)
-- 5 = Reader (Read only access to data)
INSERT INTO organization_user_role (id) VALUES (1), (2);

-- Links to descriptions of user roles
CREATE TABLE user_role_description
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    role_id INT NOT NULL,
    description_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX urd_deprecation_idx (deprecated_after),

    CONSTRAINT urd_our_described_role_fk FOREIGN KEY urd_our_described_role_idx (role_id)
        REFERENCES organization_user_role(id) ON DELETE CASCADE,
    CONSTRAINT urd_d_role_description_fk FOREIGN KEY urd_d_role_description_idx (description_id)
        REFERENCES description(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links user roles to one or more tasks the users in that role are allowed to perform
CREATE TABLE user_role_right
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    role_id INT NOT NULL,
    task_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT urr_our_owner_role_fk FOREIGN KEY urr_our_owner_role_idx (role_id)
        REFERENCES organization_user_role(id) ON DELETE CASCADE,
    CONSTRAINT urr_t_right_fk FOREIGN KEY urr_t_right_idx (task_id)
        REFERENCES task(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

INSERT INTO user_role_right (role_id, task_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
    (2, 2), (2, 3), (2, 4), (2, 5);

-- Contains links between users and organizations (many-to-many)
-- One user may belong to multiple organizations and one organization may contain multiple users
CREATE TABLE organization_membership
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    organization_id INT NOT NULL,
    user_id INT NOT NULL,
    started TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended DATETIME,
    creator_id INT,

    INDEX om_starting_idx (started),
    INDEX om_ending_idx (ended),

    CONSTRAINT om_o_parent_organization_fk FOREIGN KEY om_o_parent_organization_idx (organization_id)
        REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT om_u_member_fk FOREIGN KEY om_u_member_idx (user_id)
        REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT om_u_membership_adder_fk FOREIGN KEY om_u_membership_adder_idx (creator_id)
        REFERENCES `user`(id) ON DELETE SET NULL

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links organization members with their roles in the organizations
CREATE TABLE organization_member_role
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    membership_id INT NOT NULL,
    role_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,
    creator_id INT,

    INDEX omr_ending_idx (deprecated_after),

    CONSTRAINT omr_described_membership_fk FOREIGN KEY omr_described_membership_idx (membership_id)
        REFERENCES organization_membership(id) ON DELETE CASCADE,
    CONSTRAINT omr_our_member_role_fk FOREIGN KEY omr_our_member_role_idx (role_id)
        REFERENCES organization_user_role(id) ON DELETE CASCADE,
    CONSTRAINT omr_u_role_adder_fk FOREIGN KEY omr_u_role_adder_idx (creator_id)
        REFERENCES `user`(id) ON DELETE SET NULL

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


-- Contains invitations for joining an organization
CREATE TABLE organization_invitation
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    organization_id INT NOT NULL,
    recipient_id INT,
    recipient_email VARCHAR(128),
    starting_role_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_in DATETIME NOT NULL,
    creator_id INT,

    INDEX oi_active_invitations_idx (expires_in, recipient_email),

    CONSTRAINT oi_o_target_organization_fk FOREIGN KEY oi_o_target_organization_idx (organization_id)
        REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT oi_u_invited_user_fk FOREIGN KEY oi_u_invited_user_idx (recipient_id)
        REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT oi_our_initial_role_fk FOREIGN KEY oi_our_initial_role_idx (starting_role_id)
        REFERENCES organization_user_role(id) ON DELETE CASCADE,
    CONSTRAINT oi_u_inviter_fk FOREIGN KEY oi_u_inviter_idx (creator_id)
        REFERENCES `user`(id) ON DELETE SET NULL

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Registered responses (yes|no) to organization invitations
CREATE TABLE invitation_response
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    invitation_id INT NOT NULL,
    was_accepted BOOLEAN NOT NULL DEFAULT FALSE,
    was_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator_id INT NOT NULL,

    CONSTRAINT ir_oi_opened_invitation_fk FOREIGN KEY ir_oi_opened_invitation_idx (invitation_id)
        REFERENCES organization_invitation(id) ON DELETE CASCADE,
    CONSTRAINT ir_u_recipient_fk FOREIGN KEY ir_u_recipient_idx (creator_id)
        REFERENCES `user`(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


-- Requested deletions for an organization
-- There is a period of time during which organization owners may cancel the deletion
CREATE TABLE organization_deletion
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    organization_id INT NOT NULL,
    creator_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualization DATETIME NOT NULL,

    INDEX od_actualization_idx (actualization),

    CONSTRAINT od_o_deletion_target_fk FOREIGN KEY od_o_deletion_target_idx (organization_id)
        REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT od_u_deletion_proposer_fk FOREIGN KEY od_u_deletion_proposer_idx (creator_id)
        REFERENCES `user`(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

CREATE TABLE organization_deletion_cancellation
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    deletion_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator_id INT,

    INDEX odc_cancel_time_idx (created),

    CONSTRAINT odc_od_cancelled_deletion_fk FOREIGN KEY odc_od_cancelled_deletion_idx (deletion_id)
        REFERENCES organization_deletion(id) ON DELETE CASCADE,
    CONSTRAINT odc_u_cancel_author_fk FOREIGN KEY odc_u_cancel_author_idx (creator_id)
        REFERENCES `user`(id) ON DELETE SET NULL

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


-- Devices the users use to log in and use this service
CREATE TABLE client_device
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator_id INT,

    CONSTRAINT cd_u_first_device_user_fk FOREIGN KEY cd_u_first_device_user_idx (creator_id)
        REFERENCES `user`(id) ON DELETE SET NULL

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Names and descriptions of client devices
CREATE TABLE client_device_description
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    device_id INT NOT NULL,
    description_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX cdd_timeline_idx (deprecated_after, created),

    CONSTRAINT cdd_cd_described_device_fk FOREIGN KEY cdd_cd_described_device_idx (device_id)
        REFERENCES client_device(id) ON DELETE CASCADE,
    CONSTRAINT cdd_d_device_description_fk FOREIGN KEY cdd_d_device_description_idx (description_id)
        REFERENCES description(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links users with the devices they have used
CREATE TABLE client_device_user
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    device_id INT NOT NULL,
    user_id INT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deprecated_after DATETIME,

    INDEX cdu_timeline_idx (deprecated_after, created),

    CONSTRAINT cdu_cd_used_client_device_fk FOREIGN KEY cdu_cd_used_client_device_idx (device_id)
        REFERENCES client_device(id) ON DELETE CASCADE,
    CONSTRAINT cdu_u_device_user_fk FOREIGN KEY cdu_u_device_user_idx (user_id)
        REFERENCES `user`(id) ON DELETE CASCADE

)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Arbiter DB structure

-- Represents a county within a country
-- name: County name, with that county's or country's primary language
-- creator_id: Id of the user who registered this county
-- created: Time when this County was first created
CREATE TABLE county(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`name` VARCHAR(64) NOT NULL,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	INDEX c_name_idx (`name`),
	CONSTRAINT c_u_creator_ref_fk FOREIGN KEY c_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a postal code, which represents an area within a county
-- number: The number portion of this postal code
-- county_id: Id of the county where this postal code is resides
-- creator_id: Id of the user linked with this PostalCode
-- created: Time when this PostalCode was first created
CREATE TABLE postal_code(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`number` VARCHAR(5) NOT NULL,
	`county_id` INT NOT NULL,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	INDEX pc_number_idx (`number`),
	CONSTRAINT pc_c_county_ref_fk FOREIGN KEY pc_c_county_ref_idx (county_id) REFERENCES `county`(id) ON DELETE CASCADE,
	CONSTRAINT pc_u_creator_ref_fk FOREIGN KEY pc_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a specific street address
-- postal_code_id: Id of the postal_code linked with this StreetAddress
-- street_name: Name of the street -portion of this address
-- building_number: Number of the targeted building within the specified street
-- stair: Number or letter of the targeted stair within that building, if applicable
-- room_number: Number of the targeted room within that stair / building, if applicable
-- creator_id: Id of the user who registered this address
-- created: Time when this StreetAddress was first created
CREATE TABLE street_address(
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

-- Represents a registered company (or an individual person)
-- y_code: Official registration code of this company (id in the country system)
-- creator_id: Id of the user who registered this company to this system
-- created: Time when this company was registered
CREATE TABLE company(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`y_code` VARCHAR(10) NOT NULL,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	INDEX c_y_code_idx (`y_code`),
	INDEX cmp_created_idx (`created`),
	CONSTRAINT cmp_u_starter_ref_fk FOREIGN KEY cmp_u_starter_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
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
CREATE TABLE company_details(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`company_id` INT NOT NULL,
	`name` VARCHAR(64) NOT NULL,
	`address_id` INT NOT NULL,
	`tax_code` VARCHAR(16),
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deprecated_after` DATETIME,
	`is_official` BOOLEAN NOT NULL,
	INDEX cd_name_idx (`name`),
	INDEX cd_deprecated_after_idx (`deprecated_after`),
	CONSTRAINT cd_c_company_ref_fk FOREIGN KEY cd_c_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE CASCADE,
	CONSTRAINT cd_sa_address_ref_fk FOREIGN KEY cd_sa_address_ref_idx (address_id) REFERENCES `street_address`(id) ON DELETE CASCADE,
	CONSTRAINT cd_u_creator_ref_fk FOREIGN KEY cd_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Connects organizations with their owned companies
-- organization_id: Id of the owner organization
-- company_id: Id of the owned company
-- creator_id: Id of the user who created this link
-- created: Time when this OrganizationCompany was first created
CREATE TABLE organization_company(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`organization_id` INT NOT NULL,
	`company_id` INT NOT NULL,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT oc_o_organization_ref_fk FOREIGN KEY oc_o_organization_ref_idx (organization_id) REFERENCES `organization`(id) ON DELETE CASCADE,
	CONSTRAINT oc_c_company_ref_fk FOREIGN KEY oc_c_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE CASCADE,
	CONSTRAINT oc_u_creator_ref_fk FOREIGN KEY oc_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a bank (used with bank addresses)
-- name: (Short) name of this bank
-- bic: BIC-code of this bank
-- creator_id: Id of the user who registered this address
-- created: Time when this Bank was first created
CREATE TABLE bank(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`name` VARCHAR(64) NOT NULL,
	`bic` VARCHAR(12) NOT NULL,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	INDEX b_name_idx (`name`),
	INDEX b_bic_idx (`bic`),
	CONSTRAINT b_u_creator_ref_fk FOREIGN KEY b_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Used for listing which bank accounts belong to which company
-- company_id: Id of the company which owns this bank account
-- bank_id: Id of the bank where the company owns an account
-- address: The linked bank account address
-- creator_id: Id of the user linked with this CompanyBankAccount
-- created: Time when this information was registered
-- deprecated_after: Time when this CompanyBankAccount became deprecated. None while this CompanyBankAccount is still valid.
-- is_official: Whether this bank account information was written by the company authorities
CREATE TABLE company_bank_account(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`company_id` INT NOT NULL,
	`bank_id` INT NOT NULL,
	`address` VARCHAR(32) NOT NULL,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deprecated_after` DATETIME,
	`is_official` BOOLEAN NOT NULL,
	INDEX cba_deprecated_after_idx (`deprecated_after`),
	CONSTRAINT cba_c_company_ref_fk FOREIGN KEY cba_c_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE CASCADE,
	CONSTRAINT cba_b_bank_ref_fk FOREIGN KEY cba_b_bank_ref_idx (bank_id) REFERENCES `bank`(id) ON DELETE CASCADE,
	CONSTRAINT cba_u_creator_ref_fk FOREIGN KEY cba_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents different categories a unit can belong to. Units within a category can be compared.
CREATE TABLE unit_category(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links UnitCategories with their descriptions
-- unit_category_id: Id of the described UnitCategory
-- description_id: Id of the linked description
-- created: Time when this description was added
-- deprecated_after: Time when this description was replaced or removed
CREATE TABLE unit_category_description(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`unit_category_id` INT NOT NULL,
	`description_id` INT NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deprecated_after` DATETIME,
	INDEX ucd_created_idx (`created`),
	INDEX ucd_deprecated_after_idx (`deprecated_after`),
	CONSTRAINT ucd_uc_unit_category_ref_fk FOREIGN KEY ucd_uc_unit_category_ref_idx (unit_category_id) REFERENCES `unit_category`(id) ON DELETE CASCADE,
	CONSTRAINT ucd_d_description_ref_fk FOREIGN KEY ucd_d_description_ref_idx (description_id) REFERENCES `description`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a unit in which items can be counted
-- category_id: Id of the category this unit belongs to
-- multiplier: A multiplier that, when applied to this unit, makes it comparable with the other units in the same category
CREATE TABLE item_unit(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`category_id` INT NOT NULL,
	`multiplier` DOUBLE NOT NULL,
	CONSTRAINT iu_uc_category_ref_fk FOREIGN KEY iu_uc_category_ref_idx (category_id) REFERENCES `unit_category`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links ItemUnits with their descriptions
-- unit_id: Id of the described ItemUnit
-- description_id: Id of the linked description
-- created: Time when this description was added
-- deprecated_after: Time when this description was replaced or removed
CREATE TABLE item_unit_description(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`unit_id` INT NOT NULL,
	`description_id` INT NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deprecated_after` DATETIME,
	INDEX iud_created_idx (`created`),
	INDEX iud_deprecated_after_idx (`deprecated_after`),
	CONSTRAINT iud_iu_unit_ref_fk FOREIGN KEY iud_iu_unit_ref_idx (unit_id) REFERENCES `item_unit`(id) ON DELETE CASCADE,
	CONSTRAINT iud_d_description_ref_fk FOREIGN KEY iud_d_description_ref_idx (description_id) REFERENCES `description`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;


-- Represents a type of product sold by an individual company
-- company_id: Id of the company that owns this product type
-- unit_id: Id representing the units in which this product or service is sold
-- default_unit_price: Default € price per single unit of this product
-- tax_modifier: A modifier that is applied to this product's price to get the applied tax
-- creator_id: Id of the user linked with this CompanyProduct
-- created: Time when this product was registered
-- discontinued_after: Time when this product was discontinued (no longer sold)
CREATE TABLE company_product(
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
	CONSTRAINT cp_c_company_ref_fk FOREIGN KEY cp_c_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE CASCADE,
	CONSTRAINT cp_iu_unit_ref_fk FOREIGN KEY cp_iu_unit_ref_idx (unit_id) REFERENCES `item_unit`(id) ON DELETE CASCADE,
	CONSTRAINT cp_u_creator_ref_fk FOREIGN KEY cp_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Links CompanyProducts with their descriptions
-- product_id: Id of the described CompanyProduct
-- description_id: Id of the linked description
-- created: Time when this description was added
-- deprecated_after: Time when this description was replaced or removed
CREATE TABLE company_product_description(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`product_id` INT NOT NULL,
	`description_id` INT NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deprecated_after` DATETIME,
	INDEX cpd_created_idx (`created`),
	INDEX cpd_deprecated_after_idx (`deprecated_after`),
	CONSTRAINT cpd_cp_product_ref_fk FOREIGN KEY cpd_cp_product_ref_idx (product_id) REFERENCES `company_product`(id) ON DELETE CASCADE,
	CONSTRAINT cpd_d_description_ref_fk FOREIGN KEY cpd_d_description_ref_idx (description_id) REFERENCES `description`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents a bill / an invoice sent by one company to another to request a monetary transfer / payment
-- sender_company_details_id: Id of the details of the company who sent this invoice (payment recipient)
-- recipient_company_details_id: Id of the details of the recipient company used in this invoice
-- sender_bank_account_id: Id of the bank account the invoice sender wants the recipient to transfer money to
-- language_id: Id of the language used in this invoice
-- reference_code: A custom reference code used by the sender to identify this invoice
-- payment_duration_days: Number of days during which this invoice can be paid before additional consequences
-- product_delivery_date: Date when the sold products were delivered, if applicable
-- creator_id: Id of the user who created this invoice
-- created: Time when this Invoice was first created
-- cancelled_after: Time when this Invoice became deprecated. None while this Invoice is still valid.
CREATE TABLE invoice(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`sender_company_details_id` INT NOT NULL,
	`recipient_company_details_id` INT NOT NULL,
	`sender_bank_account_id` INT NOT NULL,
	`language_id` INT NOT NULL,
	`reference_code` VARCHAR(16) NOT NULL,
	`payment_duration_days` INT NOT NULL,
	`product_delivery_date` DATE,
	`creator_id` INT,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`cancelled_after` DATETIME,
	INDEX i_reference_code_idx (`reference_code`),
	INDEX i_created_idx (`created`),
	INDEX i_cancelled_after_idx (`cancelled_after`),
	CONSTRAINT i_cd_sender_company_details_ref_fk FOREIGN KEY i_cd_sender_company_details_ref_idx (sender_company_details_id) REFERENCES `company_details`(id) ON DELETE CASCADE,
	CONSTRAINT i_cd_recipient_company_details_ref_fk FOREIGN KEY i_cd_recipient_company_details_ref_idx (recipient_company_details_id) REFERENCES `company_details`(id) ON DELETE CASCADE,
	CONSTRAINT i_cba_sender_bank_account_ref_fk FOREIGN KEY i_cba_sender_bank_account_ref_idx (sender_bank_account_id) REFERENCES `company_bank_account`(id) ON DELETE CASCADE,
	CONSTRAINT i_l_language_ref_fk FOREIGN KEY i_l_language_ref_idx (language_id) REFERENCES `language`(id) ON DELETE CASCADE,
	CONSTRAINT i_u_creator_ref_fk FOREIGN KEY i_u_creator_ref_idx (creator_id) REFERENCES `user`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Represents an individual sold item or service
-- invoice_id: Id of the invoice on which this item appears
-- product_id: Id of the type of product this item represents / is
-- description: Name or description of this item (in the same language the invoice is given in)
-- price_per_unit: Euro (€) price per each sold unit of this item, without taxes applied
-- units_sold: Amount of items sold in the product's unit
CREATE TABLE invoice_item(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`invoice_id` INT NOT NULL,
	`product_id` INT NOT NULL,
	`description` VARCHAR(255) NOT NULL,
	`price_per_unit` DOUBLE NOT NULL,
	`units_sold` DOUBLE NOT NULL,
	CONSTRAINT ii_i_invoice_ref_fk FOREIGN KEY ii_i_invoice_ref_idx (invoice_id) REFERENCES `invoice`(id) ON DELETE CASCADE,
	CONSTRAINT ii_cp_product_ref_fk FOREIGN KEY ii_cp_product_ref_idx (product_id) REFERENCES `company_product`(id) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Arbiter DB Inserts

--
-- Inserts the initial project data to the DB
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
INSERT INTO item_unit (id, category_id, multiplier) VALUES
    (1, 1, 1.0),
    (2, 2, 1.0),
    (3, 2, 60.0),
    (4, 3, 1.0),
    (5, 4, 1.0),
    (6, 4, 12.0);

-- Command module DB

-- Stores information about invoice form locations
-- owner_id: Id of the user who uses this form
-- language_id: Id of the language this form uses
-- company_id: Id of the company for which this form is used (if used for a specific company)
-- path: Path to the form file in the local file system
CREATE TABLE invoice_form(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`owner_id` INT NOT NULL,
	`language_id` INT NOT NULL,
	`company_id` INT,
	`path` VARCHAR(255) NOT NULL,
	CONSTRAINT if_u_owner_ref_fk FOREIGN KEY if_u_owner_ref_idx (owner_id) REFERENCES `user`(id) ON DELETE CASCADE,
	CONSTRAINT if_l_language_ref_fk FOREIGN KEY if_l_language_ref_idx (language_id) REFERENCES `language`(id) ON DELETE CASCADE,
	CONSTRAINT if_c_company_ref_fk FOREIGN KEY if_c_company_ref_idx (company_id) REFERENCES `company`(id) ON DELETE SET NULL
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

-- Lists times when different description files were last read
-- path: Path to the read file
-- created: Time when this file was read
CREATE TABLE description_import(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`path` VARCHAR(255) NOT NULL,
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
