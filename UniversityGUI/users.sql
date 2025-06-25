--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','entry','reporting') NOT NULL,
  PRIMARY KEY (`username`)
);

--
-- Dumping data for table `users`
--
-- NOTE: In a real-world application, passwords should be securely hashed.
-- For this example, we are storing them as plain text for simplicity.
--

INSERT INTO `users` (`username`, `password`, `role`) VALUES
('admin', 'admin', 'admin'); 