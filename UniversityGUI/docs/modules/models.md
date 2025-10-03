# Models Module Documentation

The Models module contains entity classes that represent the data structures and business objects used throughout the University Management System.

## Overview

The model classes serve as:
- **Data Transfer Objects (DTOs)** between layers
- **Entity representations** of database tables
- **Business objects** with validation and behavior
- **Type-safe containers** for application data

## Core Entity Models

### User Model

Represents system users with authentication and authorization information.

```java
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private Role role;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private boolean isActive;
    private String email;
    private String firstName;
    private String lastName;
    
    // Constructors
    public User() {}
    
    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = true;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { 
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username.trim(); 
    }
    
    // Business methods
    public boolean hasPermission(String permission) {
        return role != null && role.hasPermission(permission);
    }
    
    public boolean isAccountActive() {
        return isActive;
    }
    
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return username;
    }
    
    // Validation
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
               passwordHash != null && role != null;
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', role='%s', active=%b}", 
                           userId, username, role.getRoleName(), isActive);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
```

### Student Model

Represents student information and academic records.

```java
public class Student {
    private String studentId;
    private int userId;          // Foreign key to User
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private Date enrollmentDate;
    private int departmentId;
    private String advisorId;
    private Department department;
    private Instructor advisor;
    private User user;
    
    // Academic information
    private double gpa;
    private int totalCredits;
    private String academicStanding;
    
    // Constructors
    public Student() {
        this.enrollmentDate = new Date();
    }
    
    public Student(String studentId, String firstName, String lastName, String email) {
        this();
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters and Setters with validation
    public void setStudentId(String studentId) {
        if (studentId == null || !studentId.matches("\\d{8}")) {
            throw new IllegalArgumentException("Student ID must be 8 digits");
        }
        this.studentId = studentId;
    }
    
    public void setEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }
    
    public void setPhone(String phone) {
        if (phone != null && !phone.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone must be 10 digits");
        }
        this.phone = phone;
    }
    
    // Business methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getAcademicStanding() {
        if (gpa >= 3.5) return "Dean's List";
        if (gpa >= 3.0) return "Good Standing";
        if (gpa >= 2.0) return "Academic Warning";
        return "Academic Probation";
    }
    
    public boolean isEligibleForHonors() {
        return gpa >= 3.5 && totalCredits >= 60;
    }
    
    public int getAcademicYear() {
        if (totalCredits >= 90) return 4;  // Senior
        if (totalCredits >= 60) return 3;  // Junior
        if (totalCredits >= 30) return 2;  // Sophomore
        return 1;                          // Freshman
    }
    
    public String getClassLevel() {
        switch (getAcademicYear()) {
            case 1: return "Freshman";
            case 2: return "Sophomore";
            case 3: return "Junior";
            case 4: return "Senior";
            default: return "Graduate";
        }
    }
    
    // Validation
    public boolean isValid() {
        return studentId != null && !studentId.trim().isEmpty() &&
               firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               email != null && email.contains("@");
    }
    
    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s %s', department='%s'}", 
                           studentId, firstName, lastName, 
                           department != null ? department.getDeptName() : "N/A");
    }
}
```

### Course Model

Represents course information and metadata.

```java
public class Course {
    private String courseId;
    private String title;
    private String description;
    private int credits;
    private int departmentId;
    private Department department;
    private List<Course> prerequisites;
    private List<Section> sections;
    
    // Course metadata
    private String level;           // Undergraduate, Graduate
    private String category;        // Core, Elective, Lab
    private boolean isActive;
    
    // Constructors
    public Course() {
        this.prerequisites = new ArrayList<>();
        this.sections = new ArrayList<>();
        this.isActive = true;
    }
    
    public Course(String courseId, String title, int credits, int departmentId) {
        this();
        this.courseId = courseId;
        this.title = title;
        this.credits = credits;
        this.departmentId = departmentId;
    }
    
    // Getters and Setters with validation
    public void setCourseId(String courseId) {
        if (courseId == null || !courseId.matches("[A-Z]{2,4}\\d{3,4}")) {
            throw new IllegalArgumentException("Invalid course ID format");
        }
        this.courseId = courseId.toUpperCase();
    }
    
    public void setCredits(int credits) {
        if (credits < 1 || credits > 6) {
            throw new IllegalArgumentException("Credits must be between 1 and 6");
        }
        this.credits = credits;
    }
    
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be empty");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Course title cannot exceed 100 characters");
        }
        this.title = title.trim();
    }
    
    // Business methods
    public String getCourseLevel() {
        if (courseId != null && courseId.length() >= 3) {
            char levelDigit = courseId.charAt(courseId.length() - 3);
            if (levelDigit >= '1' && levelDigit <= '4') {
                return "Undergraduate";
            } else if (levelDigit >= '5' && levelDigit <= '9') {
                return "Graduate";
            }
        }
        return "Unknown";
    }
    
    public boolean hasPrerequisites() {
        return prerequisites != null && !prerequisites.isEmpty();
    }
    
    public void addPrerequisite(Course prerequisite) {
        if (prerequisites == null) {
            prerequisites = new ArrayList<>();
        }
        if (!prerequisites.contains(prerequisite)) {
            prerequisites.add(prerequisite);
        }
    }
    
    public void removePrerequisite(Course prerequisite) {
        if (prerequisites != null) {
            prerequisites.remove(prerequisite);
        }
    }
    
    public boolean isPrerequisiteOf(Course course) {
        return course.getPrerequisites().contains(this);
    }
    
    // Section management
    public Section getSectionBySemester(String semester, int year) {
        return sections.stream()
            .filter(s -> s.getSemester().equals(semester) && s.getYear() == year)
            .findFirst()
            .orElse(null);
    }
    
    public List<Section> getActiveSections() {
        return sections.stream()
            .filter(Section::isActive)
            .collect(Collectors.toList());
    }
    
    // Validation
    public boolean isValid() {
        return courseId != null && !courseId.trim().isEmpty() &&
               title != null && !title.trim().isEmpty() &&
               credits > 0 && credits <= 6 &&
               departmentId > 0;
    }
    
    @Override
    public String toString() {
        return String.format("Course{id='%s', title='%s', credits=%d}", 
                           courseId, title, credits);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(courseId, course.courseId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
```

### Section Model

Represents a specific offering of a course.

```java
public class Section {
    private int sectionId;
    private String courseId;
    private String semester;
    private int year;
    private String instructorId;
    private String classroomId;
    private int timeSlotId;
    private int capacity;
    private int enrolledCount;
    
    // Related objects
    private Course course;
    private Instructor instructor;
    private Classroom classroom;
    private TimeSlot timeSlot;
    private List<Student> enrolledStudents;
    
    // Status
    private boolean isActive;
    private String status; // OPEN, CLOSED, CANCELLED
    
    // Constructors
    public Section() {
        this.enrolledStudents = new ArrayList<>();
        this.isActive = true;
        this.status = "OPEN";
        this.enrolledCount = 0;
    }
    
    public Section(String courseId, String semester, int year, String instructorId) {
        this();
        this.courseId = courseId;
        this.semester = semester;
        this.year = year;
        this.instructorId = instructorId;
    }
    
    // Business methods
    public boolean hasCapacity() {
        return enrolledCount < capacity;
    }
    
    public int getAvailableSpots() {
        return Math.max(0, capacity - enrolledCount);
    }
    
    public double getUtilizationRate() {
        return capacity > 0 ? (double) enrolledCount / capacity : 0.0;
    }
    
    public boolean isOpenForEnrollment() {
        return isActive && "OPEN".equals(status) && hasCapacity();
    }
    
    public void enrollStudent(Student student) {
        if (!hasCapacity()) {
            throw new IllegalStateException("Section is at full capacity");
        }
        if (enrolledStudents.contains(student)) {
            throw new IllegalArgumentException("Student already enrolled");
        }
        enrolledStudents.add(student);
        enrolledCount++;
        
        if (enrolledCount >= capacity) {
            status = "CLOSED";
        }
    }
    
    public void dropStudent(Student student) {
        if (enrolledStudents.remove(student)) {
            enrolledCount--;
            if ("// filepath: /home/hackycoder/mca_labs/UniverityApp/UniversityGUI/docs/modules/models.md
# Models Module Documentation

The Models module contains entity classes that represent the data structures and business objects used throughout the University Management System.

## Overview

The model classes serve as:
- **Data Transfer Objects (DTOs)** between layers
- **Entity representations** of database tables
- **Business objects** with validation and behavior
- **Type-safe containers** for application data

## Core Entity Models

### User Model

Represents system users with authentication and authorization information.

```java
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private Role role;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private boolean isActive;
    private String email;
    private String firstName;
    private String lastName;
    
    // Constructors
    public User() {}
    
    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = true;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { 
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username.trim(); 
    }
    
    // Business methods
    public boolean hasPermission(String permission) {
        return role != null && role.hasPermission(permission);
    }
    
    public boolean isAccountActive() {
        return isActive;
    }
    
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return username;
    }
    
    // Validation
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
               passwordHash != null && role != null;
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', role='%s', active=%b}", 
                           userId, username, role.getRoleName(), isActive);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
```

### Student Model

Represents student information and academic records.

```java
public class Student {
    private String studentId;
    private int userId;          // Foreign key to User
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private Date enrollmentDate;
    private int departmentId;
    private String advisorId;
    private Department department;
    private Instructor advisor;
    private User user;
    
    // Academic information
    private double gpa;
    private int totalCredits;
    private String academicStanding;
    
    // Constructors
    public Student() {
        this.enrollmentDate = new Date();
    }
    
    public Student(String studentId, String firstName, String lastName, String email) {
        this();
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters and Setters with validation
    public void setStudentId(String studentId) {
        if (studentId == null || !studentId.matches("\\d{8}")) {
            throw new IllegalArgumentException("Student ID must be 8 digits");
        }
        this.studentId = studentId;
    }
    
    public void setEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }
    
    public void setPhone(String phone) {
        if (phone != null && !phone.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone must be 10 digits");
        }
        this.phone = phone;
    }
    
    // Business methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getAcademicStanding() {
        if (gpa >= 3.5) return "Dean's List";
        if (gpa >= 3.0) return "Good Standing";
        if (gpa >= 2.0) return "Academic Warning";
        return "Academic Probation";
    }
    
    public boolean isEligibleForHonors() {
        return gpa >= 3.5 && totalCredits >= 60;
    }
    
    public int getAcademicYear() {
        if (totalCredits >= 90) return 4;  // Senior
        if (totalCredits >= 60) return 3;  // Junior
        if (totalCredits >= 30) return 2;  // Sophomore
        return 1;                          // Freshman
    }
    
    public String getClassLevel() {
        switch (getAcademicYear()) {
            case 1: return "Freshman";
            case 2: return "Sophomore";
            case 3: return "Junior";
            case 4: return "Senior";
            default: return "Graduate";
        }
    }
    
    // Validation
    public boolean isValid() {
        return studentId != null && !studentId.trim().isEmpty() &&
               firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               email != null && email.contains("@");
    }
    
    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s %s', department='%s'}", 
                           studentId, firstName, lastName, 
                           department != null ? department.getDeptName() : "N/A");
    }
}
```

### Course Model

Represents course information and metadata.

```java
public class Course {
    private String courseId;
    private String title;
    private String description;
    private int credits;
    private int departmentId;
    private Department department;
    private List<Course> prerequisites;
    private List<Section> sections;
    
    // Course metadata
    private String level;           // Undergraduate, Graduate
    private String category;        // Core, Elective, Lab
    private boolean isActive;
    
    // Constructors
    public Course() {
        this.prerequisites = new ArrayList<>();
        this.sections = new ArrayList<>();
        this.isActive = true;
    }
    
    public Course(String courseId, String title, int credits, int departmentId) {
        this();
        this.courseId = courseId;
        this.title = title;
        this.credits = credits;
        this.departmentId = departmentId;
    }
    
    // Getters and Setters with validation
    public void setCourseId(String courseId) {
        if (courseId == null || !courseId.matches("[A-Z]{2,4}\\d{3,4}")) {
            throw new IllegalArgumentException("Invalid course ID format");
        }
        this.courseId = courseId.toUpperCase();
    }
    
    public void setCredits(int credits) {
        if (credits < 1 || credits > 6) {
            throw new IllegalArgumentException("Credits must be between 1 and 6");
        }
        this.credits = credits;
    }
    
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be empty");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Course title cannot exceed 100 characters");
        }
        this.title = title.trim();
    }
    
    // Business methods
    public String getCourseLevel() {
        if (courseId != null && courseId.length() >= 3) {
            char levelDigit = courseId.charAt(courseId.length() - 3);
            if (levelDigit >= '1' && levelDigit <= '4') {
                return "Undergraduate";
            } else if (levelDigit >= '5' && levelDigit <= '9') {
                return "Graduate";
            }
        }
        return "Unknown";
    }
    
    public boolean hasPrerequisites() {
        return prerequisites != null && !prerequisites.isEmpty();
    }
    
    public void addPrerequisite(Course prerequisite) {
        if (prerequisites == null) {
            prerequisites = new ArrayList<>();
        }
        if (!prerequisites.contains(prerequisite)) {
            prerequisites.add(prerequisite);
        }
    }
    
    public void removePrerequisite(Course prerequisite) {
        if (prerequisites != null) {
            prerequisites.remove(prerequisite);
        }
    }
    
    public boolean isPrerequisiteOf(Course course) {
        return course.getPrerequisites().contains(this);
    }
    
    // Section management
    public Section getSectionBySemester(String semester, int year) {
        return sections.stream()
            .filter(s -> s.getSemester().equals(semester) && s.getYear() == year)
            .findFirst()
            .orElse(null);
    }
    
    public List<Section> getActiveSections() {
        return sections.stream()
            .filter(Section::isActive)
            .collect(Collectors.toList());
    }
    
    // Validation
    public boolean isValid() {
        return courseId != null && !courseId.trim().isEmpty() &&
               title != null && !title.trim().isEmpty() &&
               credits > 0 && credits <= 6 &&
               departmentId > 0;
    }
    
    @Override
    public String toString() {
        return String.format("Course{id='%s', title='%s', credits=%d}", 
                           courseId, title, credits);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(courseId, course.courseId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
```

### Section Model

Represents a specific offering of a course.

```java
public class Section {
    private int sectionId;
    private String courseId;
    private String semester;
    private int year;
    private String instructorId;
    private String classroomId;
    private int timeSlotId;
    private int capacity;
    private int enrolledCount;
    
    // Related objects
    private Course course;
    private Instructor instructor;
    private Classroom classroom;
    private TimeSlot timeSlot;
    private List<Student> enrolledStudents;
    
    // Status
    private boolean isActive;
    private String status; // OPEN, CLOSED, CANCELLED
    
    // Constructors
    public Section() {
        this.enrolledStudents = new ArrayList<>();
        this.isActive = true;
        this.status = "OPEN";
        this.enrolledCount = 0;
    }
    
    public Section(String courseId, String semester, int year, String instructorId) {
        this();
        this.courseId = courseId;
        this.semester = semester;
        this.year = year;
        this.instructorId = instructorId;
    }
    
    // Business methods
    public boolean hasCapacity() {
        return enrolledCount < capacity;
    }
    
    public int getAvailableSpots() {
        return Math.max(0, capacity - enrolledCount);
    }
    
    public double getUtilizationRate() {
        return capacity > 0 ? (double) enrolledCount / capacity : 0.0;
    }
    
    public boolean isOpenForEnrollment() {
        return isActive && "OPEN".equals(status) && hasCapacity();
    }
    
    public void enrollStudent(Student student) {
        if (!hasCapacity()) {
            throw new IllegalStateException("Section is at full capacity");
        }
        if (enrolledStudents.contains(student)) {
            throw new IllegalArgumentException("Student already enrolled");
        }
        enrolledStudents.add(student);
        enrolledCount++;
        
        if (enrolledCount >= capacity) {
            status = "CLOSED";
        }
    }
    
    public void dropStudent(Student student) {
        if (enrolledStudents.remove(student)) {
            enrolledCount--;
            if ("