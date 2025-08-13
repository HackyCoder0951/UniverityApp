-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Aug 13, 2025 at 06:18 PM
-- Server version: 10.4.22-MariaDB
-- PHP Version: 7.4.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `UniversityDB`
--

-- --------------------------------------------------------

--
-- Table structure for table `advisor`
--

CREATE TABLE `advisor` (
  `sID` varchar(10) NOT NULL,
  `iID` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `advisor`
--

INSERT INTO `advisor` (`sID`, `iID`) VALUES
('S101', 'CH204'),
('S101', 'EN505'),
('S101', 'M790'),
('S101', 'PH101'),
('S101', 'PH102'),
('S123', 'P123'),
('S202', 'CH202'),
('S303', 'BI303'),
('S404', 'HI404'),
('S456', 'E456'),
('S505', 'EN505'),
('S606', 'MG606'),
('S707', 'SP707'),
('S789', 'M789');

-- --------------------------------------------------------

--
-- Table structure for table `classroom`
--

CREATE TABLE `classroom` (
  `building` varchar(20) NOT NULL,
  `room_number` int(11) NOT NULL,
  `capacity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `classroom`
--

INSERT INTO `classroom` (`building`, `room_number`, `capacity`) VALUES
('Admin Building', 102, 25),
('Arts Building', 211, 35),
('Cafeteria Building', 104, 100),
('Computer Center', 201, 50),
('Engineering Building', 303, 40),
('Hostel Building', 103, 50),
('Library Building', 110, 20),
('Main Building', 101, 50),
('Science Building', 202, 30),
('Sports Complex', 105, 100);

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `course_id` varchar(10) NOT NULL,
  `title` varchar(50) DEFAULT NULL,
  `dept_name` varchar(50) DEFAULT NULL,
  `credits` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`course_id`, `title`, `dept_name`, `credits`) VALUES
('BIO101', 'Introduction to Biology', 'Biology', 3),
('BIO102', 'Introduction to Micro Biology', 'Biology', 3),
('CHEM101', 'General Chemistry', 'Chemistry', 4),
('CHEM102', 'Organic Chemistry', 'Chemistry', 4),
('CS101', 'Introduction to Programming', 'Computer Science', 3),
('CS102', 'Introduction to IT', 'Computer Science', 3),
('EE202', 'Circuit Analysis', 'Electrical Engineering', 4),
('EE203', 'Circuit Design', 'Electrical Engineering', 4),
('ENG101', 'English Composition', 'English', 3),
('HIST201', 'Modern World History', 'History', 3),
('MATH301', 'Calculus III', 'Mathematics', 3),
('MATH302', 'Calculus IV', 'Mathematics', 3),
('MGT301', 'Principles of Management', 'Management', 3),
('MGT302', 'MIS-Concepts', 'Management', 3),
('PHYS201', 'Mechanics', 'Physics', 3),
('PHYS202', 'Dynamics', 'Physics', 3),
('SPORT101', 'Physical Fitness', 'Sports', 2),
('SPORT102', 'Medical Fitness', 'Sports', 2);

--
-- Triggers `course`
--
DELIMITER $$
CREATE TRIGGER `recalculate_tot_cred_after_course_update` AFTER UPDATE ON `course` FOR EACH ROW BEGIN
    UPDATE student
    SET tot_cred = (
        SELECT COALESCE(SUM(c.credits), 0)
        FROM takes t
        JOIN course c ON t.course_id = c.course_id
        WHERE t.ID = (
            SELECT ID FROM takes WHERE course_id = NEW.course_id LIMIT 1
        )
    )
    WHERE ID IN (
        SELECT ID FROM takes WHERE course_id = NEW.course_id
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `department`
--

CREATE TABLE `department` (
  `dept_name` varchar(50) NOT NULL,
  `building` varchar(20) DEFAULT NULL,
  `budget` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `department`
--

INSERT INTO `department` (`dept_name`, `building`, `budget`) VALUES
('Biology', 'Science Building', 250000.00),
('Chemistry', 'Science Building', 350000.00),
('Computer Science', 'Main Building', 350000.00),
('Electrical Engineering', 'Engineering Building', 800000.00),
('English', 'Arts Building', 150000.00),
('History', 'Arts Building', 200000.00),
('Management', 'Admin Building', 250000.00),
('Mathematics', 'Main Building', 500000.00),
('Physics', 'Science Building', 400000.00),
('Sports', 'Sports Complex', 100000.00);

-- --------------------------------------------------------

--
-- Table structure for table `grade_points`
--

CREATE TABLE `grade_points` (
  `id` int(11) NOT NULL,
  `grade` varchar(50) NOT NULL,
  `points` decimal(4,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `grade_points`
--

INSERT INTO `grade_points` (`id`, `grade`, `points`) VALUES
(1, 'A+', 4.00),
(2, 'A-', 3.70),
(3, 'B+', 3.30),
(4, 'B-', 3.00),
(5, 'C+', 2.75);

-- --------------------------------------------------------

--
-- Table structure for table `instructor`
--

CREATE TABLE `instructor` (
  `ID` varchar(10) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `dept_name` varchar(50) DEFAULT NULL,
  `salary` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `instructor`
--

INSERT INTO `instructor` (`ID`, `name`, `dept_name`, `salary`) VALUES
('BI303', 'Prof. Biology', 'Biology', 45000.00),
('BI304', 'Prof. Singh', 'Biology', 45500.00),
('CH202', 'Prof. Chemistry', 'Chemistry', 50000.00),
('CH204', 'Prof. Varma', 'Chemistry', 58000.00),
('E456', 'Prof. Engineer', 'Electrical Engineering', 75000.00),
('E457', 'Prof. Kunal', 'Electrical Engineering', 80500.00),
('EN505', 'Prof. English', 'English', 35000.00),
('HI404', 'Prof. History', 'History', 40000.00),
('M789', 'Prof. Math', 'Mathematics', 60000.00),
('M790', 'Prof. Joshi', 'Mathematics', 66000.00),
('MG606', 'Prof. Management', 'Management', 50000.00),
('MG607', 'Prof. Vicky', 'Management', 59000.00),
('P123', 'Prof. Patel', 'Computer Science', 80000.00),
('P124', 'Prof. Sharma', 'Computer Science', 80000.00),
('PH101', 'Prof. Physics', 'Physics', 55000.00),
('PH102', 'Prof. Kirti', 'Physics', 55000.00),
('SP707', 'Coach Sports', 'Sports', 30000.00),
('SP708', 'Medical Examinar', 'Sports', 35000.00);

-- --------------------------------------------------------

--
-- Table structure for table `login_history`
--

CREATE TABLE `login_history` (
  `id` int(11) NOT NULL,
  `uid` varchar(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `login_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `logout_time` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `login_history`
--

INSERT INTO `login_history` (`id`, `uid`, `username`, `login_time`, `logout_time`, `is_active`) VALUES
(1, '1', 'admin', '2025-06-27 12:12:35', '2025-06-27 12:12:43', 0),
(2, '3', 'jignesh', '2025-06-27 12:12:48', '2025-06-27 12:13:03', 0),
(3, '2', 'good', '2025-06-27 12:13:13', '2025-06-27 12:16:18', 0),
(4, '1', 'admin', '2025-06-27 12:16:21', '2025-06-27 12:18:18', 0),
(5, '1', 'admin', '2025-06-27 12:18:21', '2025-06-27 12:20:55', 0),
(6, '1', 'admin', '2025-06-27 12:21:07', '2025-06-27 12:21:10', 0),
(7, '1', 'admin', '2025-06-27 12:24:44', '2025-06-27 12:25:03', 0),
(8, '3', 'jignesh', '2025-06-27 12:25:10', '2025-07-03 10:43:07', 0),
(9, '1', 'admin', '2025-06-27 12:27:54', '2025-06-27 12:28:09', 0),
(10, '2', 'good', '2025-06-27 12:28:15', '2025-06-27 12:28:32', 0),
(11, '3', 'jignesh', '2025-06-27 12:28:42', '2025-07-03 10:43:07', 0),
(12, '1', 'admin', '2025-06-27 12:33:24', '2025-06-27 12:41:56', 0),
(13, '1', 'admin', '2025-06-27 12:42:51', '2025-06-27 13:05:58', 0),
(14, '1', 'admin', '2025-06-27 12:52:36', '2025-06-27 13:05:58', 0),
(15, '1', 'admin', '2025-06-27 12:55:55', '2025-06-27 13:05:58', 0),
(16, '1', 'admin', '2025-06-27 13:05:51', '2025-06-27 13:05:58', 0),
(17, '1', 'admin', '2025-06-27 13:24:41', '2025-06-27 14:17:24', 0),
(18, '1', 'admin', '2025-06-27 13:30:35', '2025-06-27 14:17:24', 0),
(19, '1', 'admin', '2025-06-27 13:31:08', '2025-06-27 14:17:24', 0),
(20, '1', 'admin', '2025-06-27 13:31:43', '2025-06-27 14:17:24', 0),
(21, '1', 'admin', '2025-06-27 13:32:38', '2025-06-27 14:17:24', 0),
(22, '1', 'admin', '2025-06-27 13:43:10', '2025-06-27 14:17:24', 0),
(23, '1', 'admin', '2025-06-27 13:44:41', '2025-06-27 14:17:24', 0),
(24, '1', 'admin', '2025-06-27 13:46:25', '2025-06-27 14:17:24', 0),
(25, '1', 'admin', '2025-06-27 13:58:47', '2025-06-27 14:17:24', 0),
(26, '1', 'admin', '2025-06-27 14:02:40', '2025-06-27 14:17:24', 0),
(27, '1', 'admin', '2025-06-27 14:13:09', '2025-06-27 14:17:24', 0),
(28, '1', 'admin', '2025-06-27 14:14:25', '2025-06-27 14:17:24', 0),
(29, 'S123', 'S123', '2025-06-27 14:19:34', '2025-06-27 14:19:40', 0),
(30, '1', 'admin', '2025-06-27 14:19:45', '2025-06-27 14:20:22', 0),
(31, '1', 'admin', '2025-06-27 14:22:10', '2025-06-27 14:28:50', 0),
(32, '1', 'admin', '2025-06-27 14:26:42', '2025-06-27 14:28:50', 0),
(33, '1', 'admin', '2025-06-27 14:28:39', '2025-06-27 14:28:50', 0),
(34, 'S123', 'S123', '2025-06-27 14:28:56', '2025-06-27 14:28:59', 0),
(35, 'S123', 'S123', '2025-06-27 14:30:25', '2025-06-27 14:32:19', 0),
(36, 'S123', 'S123', '2025-06-27 14:32:15', '2025-06-27 14:32:19', 0),
(37, '1', 'admin', '2025-06-27 14:32:26', '2025-06-27 14:32:46', 0),
(38, 'S123', 'S123', '2025-06-27 14:34:38', '2025-06-27 14:34:41', 0),
(39, 'S123', 'S123', '2025-06-27 14:45:54', '2025-06-27 14:46:21', 0),
(40, '2', 'good', '2025-06-27 14:46:32', '2025-06-27 14:46:35', 0),
(41, '1', 'admin', '2025-06-27 14:47:02', '2025-06-27 14:47:13', 0),
(42, '1', 'admin', '2025-06-27 17:58:31', '2025-06-27 17:59:15', 0),
(43, '1', 'admin', '2025-07-03 10:39:01', '2025-07-03 10:40:29', 0),
(44, '2', 'good', '2025-07-03 10:40:58', '2025-07-03 10:41:05', 0),
(45, '1', 'admin', '2025-07-03 10:41:15', '2025-07-03 10:41:31', 0),
(46, '3', 'jignesh', '2025-07-03 10:41:51', '2025-07-03 10:43:07', 0),
(47, '1', 'admin', '2025-07-03 11:13:38', '2025-07-03 11:14:28', 0),
(48, '3', 'jignesh', '2025-07-03 11:14:34', '2025-07-03 11:15:45', 0),
(49, '2', 'good', '2025-07-03 11:15:52', '2025-07-03 11:15:56', 0),
(50, 'S123', 'S123', '2025-07-03 11:16:11', '2025-07-03 11:16:15', 0);

-- --------------------------------------------------------

--
-- Table structure for table `marks`
--

CREATE TABLE `marks` (
  `mark_id` int(11) NOT NULL,
  `student_id` varchar(10) NOT NULL,
  `course_id` varchar(10) NOT NULL,
  `sec_id` int(11) NOT NULL,
  `semester` varchar(10) NOT NULL,
  `year` int(11) NOT NULL,
  `instructor_id` varchar(10) NOT NULL,
  `marks` decimal(5,2) NOT NULL,
  `letter_grade` int(11) NOT NULL,
  `gpa` decimal(3,2) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `marks`
--

INSERT INTO `marks` (`mark_id`, `student_id`, `course_id`, `sec_id`, `semester`, `year`, `instructor_id`, `marks`, `letter_grade`, `gpa`, `created_at`, `updated_at`) VALUES
(1, 'S101', 'CHEM101', 1, 'Fall', 2023, 'CH202', 95.00, 1, 4.00, '2025-06-26 18:49:24', '2025-06-26 19:00:48'),
(2, 'S101', 'CHEM102', 1, 'Spring', 2024, 'CH202', 92.00, 1, 4.00, '2025-06-26 18:49:24', '2025-06-26 19:00:51'),
(3, 'S101', 'CS101', 3, 'Fall', 2023, 'P123', 75.00, 5, 2.75, '2025-06-26 18:49:24', '2025-06-26 19:01:08'),
(4, 'S101', 'ENG101', 6, 'Fall', 2023, 'EN505', 98.00, 1, 4.00, '2025-06-26 18:49:24', '2025-06-26 19:01:10'),
(5, 'S101', 'MATH302', 1, 'Spring', 2024, 'M789', 85.00, 3, 3.30, '2025-06-26 18:49:24', '2025-06-26 19:01:34'),
(6, 'S123', 'BIO101', 1, 'Fall', 2023, 'BI303', 97.00, 1, 4.00, '2025-06-26 18:49:24', '2025-06-26 19:01:14'),
(7, 'S303', 'PHYS201', 1, 'Fall', 2023, 'PH101', 96.00, 1, 4.00, '2025-06-26 18:49:24', '2025-06-26 19:01:16'),
(8, 'S404', 'HIST201', 7, 'Fall', 2023, 'HI404', 80.00, 4, 3.00, '2025-06-26 18:49:24', '2025-06-26 19:02:06'),
(9, 'S404', 'PHYS202', 1, 'Spring', 2024, 'PH102', 99.00, 1, 4.00, '2025-06-26 18:49:24', '2025-06-26 19:01:18'),
(10, 'S456', 'EE202', 2, 'Spring', 2024, 'E456', 88.00, 3, 3.30, '2025-06-26 18:49:24', '2025-06-26 19:01:43'),
(11, 'S505', 'ENG101', 6, 'Fall', 2023, 'EN505', 97.00, 1, 4.00, '2025-06-26 18:49:24', '2025-06-26 19:01:22'),
(12, 'S606', 'MGT301', 5, 'Fall', 2023, 'MG606', 87.00, 3, 3.30, '2025-06-26 18:49:24', '2025-06-26 19:01:45'),
(13, 'S707', 'SPORT101', 4, 'Fall', 2023, 'SP707', 85.00, 3, 3.30, '2025-06-26 18:49:24', '2025-06-26 19:01:46'),
(14, 'S789', 'MATH301', 1, 'Fall', 2023, 'M789', 91.00, 2, 3.70, '2025-06-26 18:49:24', '2025-06-26 19:01:55');

-- --------------------------------------------------------

--
-- Table structure for table `password_requests`
--

CREATE TABLE `password_requests` (
  `request_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `request_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` enum('pending','approved','denied') NOT NULL DEFAULT 'pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `password_requests`
--

INSERT INTO `password_requests` (`request_id`, `username`, `request_date`, `status`) VALUES
(1, 'good', '2025-06-25 08:03:37', 'approved'),
(2, 'good', '2025-06-25 08:14:50', 'approved'),
(3, 'good', '2025-06-25 08:21:34', 'approved'),
(4, 'good', '2025-06-27 04:23:05', 'denied'),
(5, 'good', '2025-06-27 12:16:17', 'pending'),
(6, 'good', '2025-07-03 10:41:03', 'pending');

-- --------------------------------------------------------

--
-- Table structure for table `permissions`
--

CREATE TABLE `permissions` (
  `username` varchar(50) NOT NULL,
  `table_name` varchar(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `permissions`
--

INSERT INTO `permissions` (`username`, `table_name`) VALUES
('good', 'student'),
('jignesh', 'marks'),
('jignesh', 'result'),
('jignesh', 'student');

-- --------------------------------------------------------

--
-- Table structure for table `prereq`
--

CREATE TABLE `prereq` (
  `course_id` varchar(10) NOT NULL,
  `prereq_id` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `prereq`
--

INSERT INTO `prereq` (`course_id`, `prereq_id`) VALUES
('BIO101', 'BIO100'),
('BIO102', 'BIO110'),
('CHEM101', 'CHEM100'),
('CHEM102', 'CHEM110'),
('CS101', 'CS201'),
('CS102', 'CS202'),
('EE202', 'CS101'),
('EE203', 'EE201'),
('ENG101', 'ENG100'),
('HIST201', 'HIST101'),
('MATH301', 'MATH201'),
('MATH302', 'MATH301'),
('MGT301', 'MGT201'),
('MGT302', 'MGT301'),
('PHYS201', 'PHYS101'),
('PHYS202', 'PHYS102'),
('SPORT101', 'SPORT100'),
('SPORT102', 'SPORT101');

-- --------------------------------------------------------

--
-- Table structure for table `result`
--

CREATE TABLE `result` (
  `result_id` int(11) NOT NULL,
  `student_id` varchar(20) NOT NULL,
  `semester` varchar(10) NOT NULL,
  `year` int(11) NOT NULL,
  `sgpa` decimal(3,2) DEFAULT NULL,
  `cgpa` decimal(3,2) DEFAULT NULL,
  `total_credits` int(11) DEFAULT NULL,
  `result_type` enum('semester','annual','final') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `result`
--

INSERT INTO `result` (`result_id`, `student_id`, `semester`, `year`, `sgpa`, `cgpa`, `total_credits`, `result_type`, `created_at`, `updated_at`) VALUES
(1, 'S101', 'Fall', 2023, 3.61, 3.61, 15, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06'),
(2, 'S101', 'Spring', 2024, 3.65, 3.63, 12, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06'),
(3, 'S123', 'Fall', 2023, 4.00, 4.00, 3, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06'),
(4, 'S303', 'Fall', 2023, 4.00, 4.00, 3, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06'),
(5, 'S404', 'Fall', 2023, 3.00, 3.00, 3, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06'),
(6, 'S404', 'Spring', 2024, 4.00, 3.50, 3, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06'),
(7, 'S456', 'Spring', 2024, 3.30, 3.30, 4, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06'),
(8, 'S505', 'Fall', 2023, 4.00, 4.00, 3, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06'),
(9, 'S606', 'Fall', 2023, 3.30, 3.30, 3, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06'),
(10, 'S707', 'Fall', 2023, 3.30, 3.30, 2, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06'),
(11, 'S789', 'Fall', 2023, 3.70, 3.70, 3, 'semester', '2025-06-26 18:49:06', '2025-06-26 18:49:06');

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id`, `name`) VALUES
(1, 'admin'),
(2, 'entry'),
(3, 'reporting'),
(4, 'student');

-- --------------------------------------------------------

--
-- Table structure for table `section`
--

CREATE TABLE `section` (
  `course_id` varchar(10) NOT NULL,
  `sec_id` int(11) NOT NULL,
  `semester` varchar(20) NOT NULL,
  `year` int(11) NOT NULL,
  `building` varchar(20) DEFAULT NULL,
  `room_number` int(11) DEFAULT NULL,
  `time_slot_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `section`
--

INSERT INTO `section` (`course_id`, `sec_id`, `semester`, `year`, `building`, `room_number`, `time_slot_id`) VALUES
('BIO101', 1, 'Fall', 2023, 'Science Building', 202, 6),
('BIO102', 1, 'Spring', 2024, 'Science Building', 202, 6),
('CHEM101', 1, 'Fall', 2023, 'Science Building', 202, 5),
('CHEM102', 1, 'Spring', 2024, 'Science Building', 202, 5),
('CS101', 3, 'Fall', 2023, 'Main Building', 101, 1),
('CS102', 3, 'Spring', 2024, 'Main Building', 101, 11),
('EE202', 2, 'Spring', 2024, 'Engineering Building', 303, 2),
('EE203', 2, 'Spring', 2024, 'Engineering Building', 303, 2),
('ENG101', 6, 'Fall', 2023, 'Arts Building', 211, 8),
('HIST201', 7, 'Fall', 2023, 'Arts Building', 211, 7),
('MATH301', 1, 'Fall', 2023, 'Main Building', 101, 3),
('MATH302', 1, 'Spring', 2024, 'Main Building', 101, 3),
('MGT301', 5, 'Fall', 2023, 'Admin Building', 102, 9),
('MGT302', 5, 'Spring', 2024, 'Admin Building', 102, 9),
('PHYS201', 1, 'Fall', 2023, 'Science Building', 202, 4),
('PHYS202', 1, 'Spring', 2024, 'Science Building', 202, 12),
('SPORT101', 4, 'Fall', 2023, 'Sports Complex', 105, 10),
('SPORT102', 4, 'Spring', 2024, 'Sports Complex', 105, 10);

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `ID` varchar(10) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `dept_name` varchar(50) NOT NULL,
  `tot_cred` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`ID`, `name`, `dept_name`, `tot_cred`) VALUES
('S101', 'David', 'Physics', 17),
('S120', 'Jignesh', 'English', 15),
('S123', 'Alice', 'Computer Science', 3),
('S202', 'Emily', 'Chemistry', 7),
('S303', 'Frank', 'Biology', 6),
('S404', 'Grace', 'History', 6),
('S456', 'Bob', 'Electrical Engineering', 7),
('S505', 'Henry', 'English', 3),
('S606', 'Ivy', 'Management', 3),
('S707', 'Jack', 'Sports', 2),
('S789', 'Charlie', 'Mathematics', 3);

-- --------------------------------------------------------

--
-- Stand-in structure for view `student_gpa_annual`
-- (See below for the actual view)
--
CREATE TABLE `student_gpa_annual` (
`student_id` varchar(10)
,`GPA` decimal(37,2)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `student_gpa_sem`
-- (See below for the actual view)
--
CREATE TABLE `student_gpa_sem` (
`student_id` varchar(10)
,`semester` varchar(20)
,`year` int(11)
,`SGPA` decimal(37,2)
);

-- --------------------------------------------------------

--
-- Table structure for table `takes`
--

CREATE TABLE `takes` (
  `ID` varchar(10) NOT NULL,
  `course_id` varchar(10) NOT NULL,
  `sec_id` int(11) NOT NULL,
  `semester` varchar(20) NOT NULL,
  `year` int(11) NOT NULL,
  `grade` varchar(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `takes`
--

INSERT INTO `takes` (`ID`, `course_id`, `sec_id`, `semester`, `year`, `grade`) VALUES
('S101', 'CHEM101', 1, 'Fall', 2023, 'A+'),
('S101', 'CHEM102', 1, 'Spring', 2024, 'A+'),
('S101', 'CS101', 3, 'Fall', 2023, 'C+'),
('S101', 'ENG101', 6, 'Fall', 2023, 'A+'),
('S101', 'MATH302', 1, 'Spring', 2024, 'B+'),
('S123', 'BIO101', 1, 'Fall', 2023, 'A+'),
('S303', 'PHYS201', 1, 'Fall', 2023, 'A+'),
('S404', 'HIST201', 7, 'Fall', 2023, 'B-'),
('S404', 'PHYS202', 1, 'Spring', 2024, 'A+'),
('S456', 'EE202', 2, 'Spring', 2024, 'B+'),
('S505', 'ENG101', 6, 'Fall', 2023, 'A+'),
('S606', 'MGT301', 5, 'Fall', 2023, 'B+'),
('S707', 'SPORT101', 4, 'Fall', 2023, 'B+'),
('S789', 'MATH301', 1, 'Fall', 2023, 'A-');

--
-- Triggers `takes`
--
DELIMITER $$
CREATE TRIGGER `update_tot_cred` AFTER INSERT ON `takes` FOR EACH ROW BEGIN
    UPDATE student s
    SET tot_cred = (
        SELECT COALESCE(SUM(c.credits), 0)
        FROM takes t
        JOIN course c ON t.course_id = c.course_id
        WHERE t.id = s.id
    )
    WHERE s.id = NEW.id;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `update_tot_cred_after_delete` AFTER DELETE ON `takes` FOR EACH ROW BEGIN
    UPDATE student s
    SET tot_cred = (
        SELECT COALESCE(SUM(c.credits), 0)
        FROM takes t
        JOIN course c ON t.course_id = c.course_id
        WHERE t.id = OLD.id
    )
    WHERE s.id = OLD.id;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `update_tot_cred_after_update` AFTER UPDATE ON `takes` FOR EACH ROW BEGIN
    UPDATE student s
    SET tot_cred = (
        SELECT COALESCE(SUM(c.credits), 0)
        FROM takes t
        JOIN course c ON t.course_id = c.course_id
        WHERE t.id = s.id
    )
    WHERE s.id = NEW.id;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `teaches`
--

CREATE TABLE `teaches` (
  `ID` varchar(10) NOT NULL,
  `course_id` varchar(10) NOT NULL,
  `sec_id` int(11) NOT NULL,
  `semester` varchar(20) NOT NULL,
  `year` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `teaches`
--

INSERT INTO `teaches` (`ID`, `course_id`, `sec_id`, `semester`, `year`) VALUES
('BI303', 'BIO101', 1, 'Fall', 2023),
('BI303', 'BIO102', 1, 'Spring', 2024),
('CH202', 'CHEM101', 1, 'Fall', 2023),
('CH202', 'CHEM102', 1, 'Spring', 2024),
('E456', 'EE202', 2, 'Spring', 2024),
('E456', 'EE203', 2, 'Spring', 2024),
('EN505', 'ENG101', 6, 'Fall', 2023),
('HI404', 'HIST201', 7, 'Fall', 2023),
('M789', 'MATH301', 1, 'Fall', 2023),
('M789', 'MATH302', 1, 'Spring', 2024),
('MG606', 'MGT301', 5, 'Fall', 2023),
('MG606', 'MGT302', 5, 'Spring', 2024),
('P123', 'CS101', 3, 'Fall', 2023),
('P124', 'CS102', 3, 'Spring', 2024),
('PH101', 'PHYS201', 1, 'Fall', 2023),
('PH102', 'PHYS202', 1, 'Spring', 2024),
('SP707', 'SPORT101', 4, 'Fall', 2023),
('SP707', 'SPORT102', 4, 'Spring', 2024);

-- --------------------------------------------------------

--
-- Table structure for table `time_slot`
--

CREATE TABLE `time_slot` (
  `time_slot_id` int(11) NOT NULL,
  `day` varchar(10) DEFAULT NULL,
  `start_time` time DEFAULT NULL,
  `end_time` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `time_slot`
--

INSERT INTO `time_slot` (`time_slot_id`, `day`, `start_time`, `end_time`) VALUES
(1, 'Monday', '09:00:00', '10:00:00'),
(2, 'Wednesday', '11:00:00', '12:00:00'),
(3, 'Friday', '13:00:00', '14:00:00'),
(4, 'Tuesday', '09:00:00', '10:00:00'),
(5, 'Thursday', '11:00:00', '12:00:00'),
(6, 'Friday', '15:00:00', '16:00:00'),
(7, 'Monday', '11:00:00', '12:00:00'),
(8, 'Wednesday', '13:00:00', '14:00:00'),
(9, 'Friday', '09:00:00', '10:00:00'),
(10, 'Tuesday', '11:00:00', '12:00:00'),
(11, 'Monday', '01:30:00', '02:30:00'),
(12, 'Tuesday', '01:30:00', '02:30:00');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `UID` varchar(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` int(11) NOT NULL,
  `requires_password_reset` tinyint(1) NOT NULL DEFAULT 0,
  `student_id` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`UID`, `username`, `password`, `role`, `requires_password_reset`, `student_id`) VALUES
('1', 'admin', 'admin', 1, 0, NULL),
('2', 'good', 'temp', 3, 0, NULL),
('3', 'jignesh', '1234', 2, 0, NULL),
('S123', 'S123', '1234', 4, 0, 'S123');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `advisor`
--
ALTER TABLE `advisor`
  ADD PRIMARY KEY (`sID`,`iID`),
  ADD KEY `iID` (`iID`);

--
-- Indexes for table `classroom`
--
ALTER TABLE `classroom`
  ADD PRIMARY KEY (`building`,`room_number`);

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`course_id`),
  ADD KEY `dept_name` (`dept_name`);

--
-- Indexes for table `department`
--
ALTER TABLE `department`
  ADD PRIMARY KEY (`dept_name`),
  ADD KEY `building` (`building`);

--
-- Indexes for table `grade_points`
--
ALTER TABLE `grade_points`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `instructor`
--
ALTER TABLE `instructor`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `dept_name` (`dept_name`);

--
-- Indexes for table `login_history`
--
ALTER TABLE `login_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_login_history_users` (`uid`);

--
-- Indexes for table `marks`
--
ALTER TABLE `marks`
  ADD PRIMARY KEY (`mark_id`,`student_id`,`course_id`,`sec_id`,`semester`,`year`,`instructor_id`),
  ADD KEY `idx_marks_student` (`student_id`),
  ADD KEY `idx_marks_course` (`course_id`),
  ADD KEY `idx_marks_section` (`course_id`,`sec_id`,`semester`,`year`),
  ADD KEY `idx_marks_semester` (`semester`,`year`),
  ADD KEY `fk_marks_instructor` (`instructor_id`),
  ADD KEY `fk_marks_grades` (`letter_grade`);

--
-- Indexes for table `password_requests`
--
ALTER TABLE `password_requests`
  ADD PRIMARY KEY (`request_id`),
  ADD KEY `username` (`username`);

--
-- Indexes for table `permissions`
--
ALTER TABLE `permissions`
  ADD PRIMARY KEY (`username`,`table_name`);

--
-- Indexes for table `prereq`
--
ALTER TABLE `prereq`
  ADD PRIMARY KEY (`course_id`,`prereq_id`),
  ADD KEY `prereq_id` (`prereq_id`);

--
-- Indexes for table `result`
--
ALTER TABLE `result`
  ADD PRIMARY KEY (`result_id`),
  ADD UNIQUE KEY `uniq_result` (`student_id`,`semester`,`year`,`result_type`),
  ADD KEY `idx_result_student` (`student_id`),
  ADD KEY `idx_result_semester` (`semester`,`year`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `section`
--
ALTER TABLE `section`
  ADD PRIMARY KEY (`course_id`,`sec_id`,`semester`,`year`),
  ADD KEY `section_ibfk_2` (`building`,`room_number`),
  ADD KEY `section_ibfk_3` (`time_slot_id`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`ID`,`dept_name`),
  ADD KEY `dept_name` (`dept_name`);

--
-- Indexes for table `takes`
--
ALTER TABLE `takes`
  ADD PRIMARY KEY (`ID`,`course_id`,`sec_id`,`semester`,`year`),
  ADD KEY `course_id` (`course_id`,`sec_id`,`semester`,`year`);

--
-- Indexes for table `teaches`
--
ALTER TABLE `teaches`
  ADD PRIMARY KEY (`ID`,`course_id`,`sec_id`,`semester`,`year`),
  ADD KEY `course_id` (`course_id`,`sec_id`,`semester`,`year`);

--
-- Indexes for table `time_slot`
--
ALTER TABLE `time_slot`
  ADD PRIMARY KEY (`time_slot_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`UID`,`username`),
  ADD KEY `fk_users_roles` (`role`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `grade_points`
--
ALTER TABLE `grade_points`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `login_history`
--
ALTER TABLE `login_history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- AUTO_INCREMENT for table `marks`
--
ALTER TABLE `marks`
  MODIFY `mark_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `password_requests`
--
ALTER TABLE `password_requests`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `result`
--
ALTER TABLE `result`
  MODIFY `result_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

-- --------------------------------------------------------

--
-- Structure for view `student_gpa_annual`
--
DROP TABLE IF EXISTS `student_gpa_annual`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `student_gpa_annual`  AS SELECT `t`.`ID` AS `student_id`, round(coalesce(sum(`g`.`points` * `c`.`credits`) / nullif(sum(`c`.`credits`),0),0),2) AS `GPA` FROM ((`takes` `t` join `grade_points` `g` on(`t`.`grade` = `g`.`grade`)) join `course` `c` on(`t`.`course_id` = `c`.`course_id`)) GROUP BY `t`.`ID` ;

-- --------------------------------------------------------

--
-- Structure for view `student_gpa_sem`
--
DROP TABLE IF EXISTS `student_gpa_sem`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `student_gpa_sem`  AS SELECT `t`.`ID` AS `student_id`, `t`.`semester` AS `semester`, `t`.`year` AS `year`, round(coalesce(sum(`g`.`points` * `c`.`credits`) / nullif(sum(`c`.`credits`),0),0),2) AS `SGPA` FROM ((`takes` `t` join `grade_points` `g` on(`t`.`grade` = `g`.`grade`)) join `course` `c` on(`t`.`course_id` = `c`.`course_id`)) GROUP BY `t`.`ID`, `t`.`semester`, `t`.`year` ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `login_history`
--
ALTER TABLE `login_history`
  ADD CONSTRAINT `fk_login_history_users` FOREIGN KEY (`uid`) REFERENCES `users` (`UID`);

--
-- Constraints for table `marks`
--
ALTER TABLE `marks`
  ADD CONSTRAINT `fk_marks_grades` FOREIGN KEY (`letter_grade`) REFERENCES `grade_points` (`id`),
  ADD CONSTRAINT `fk_marks_instructor` FOREIGN KEY (`instructor_id`) REFERENCES `instructor` (`ID`),
  ADD CONSTRAINT `fk_marks_section` FOREIGN KEY (`course_id`,`sec_id`,`semester`,`year`) REFERENCES `section` (`course_id`, `sec_id`, `semester`, `year`),
  ADD CONSTRAINT `fk_marks_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`ID`);

--
-- Constraints for table `result`
--
ALTER TABLE `result`
  ADD CONSTRAINT `fk_result_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`ID`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `fk_users_roles` FOREIGN KEY (`role`) REFERENCES `roles` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
