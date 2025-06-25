--
-- Add requires_password_reset column to users table
--

ALTER TABLE `users`
ADD COLUMN `requires_password_reset` BOOLEAN NOT NULL DEFAULT FALSE
AFTER `role`; 