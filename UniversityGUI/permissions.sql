--
-- Table structure for table `permissions`
--

CREATE TABLE `permissions` (
  `username` varchar(50) NOT NULL,
  `table_name` varchar(64) NOT NULL,
  PRIMARY KEY (`username`, `table_name`),
  FOREIGN KEY (`username`) REFERENCES `users`(`username`) ON DELETE CASCADE
); 