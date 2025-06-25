--
-- Table structure for table `password_requests`
--

CREATE TABLE `password_requests` (
  `request_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `request_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('pending','approved','denied') NOT NULL DEFAULT 'pending',
  PRIMARY KEY (`request_id`),
  FOREIGN KEY (`username`) REFERENCES `users`(`username`) ON DELETE CASCADE
); 