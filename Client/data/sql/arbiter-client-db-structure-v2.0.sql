-- 
-- Database structure for arbiter client models
-- Version: v2.0
-- Last generated: 2023-05-01
--

--	Auth	----------

-- Represents a local user-session
-- user_id:          Id of the logged-in user
-- created:          Time when this session was opened
-- deprecated_after: Time when this session was closed
CREATE TABLE `session`(
	`id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, 
	`user_id` INT NOT NULL, 
	`created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	`deprecated_after` DATETIME, 
	INDEX s_created_idx (`created`), 
	INDEX s_deprecated_after_idx (`deprecated_after`), 
	CONSTRAINT s_u_user_ref_fk FOREIGN KEY s_u_user_ref_idx (user_id) REFERENCES `user`(`id`) ON DELETE CASCADE
)Engine=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

