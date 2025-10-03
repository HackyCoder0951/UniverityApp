# Data Access Object (DAO) Module Documentation

The DAO module implements the Data Access Object pattern to provide a clean abstraction layer between the business logic and database operations.

## Overview

The DAO layer encapsulates all database access logic, providing a consistent interface for CRUD operations across all entities in the University Management System.

## Architecture

### Generic DAO Interface

The foundation of the DAO layer is the `GenericDAO` interface that defines common operations:

```java
public interface GenericDAO<T, ID> {
    T findById(ID id);
    List<T> findAll();
    void save(T entity);
    void update(T entity);
    void delete(ID id);
    boolean exists(ID id);
    long count();
}
```

### Base DAO Implementation

```java
public abstract class BaseDAO<T, ID> implements GenericDAO<T, ID> {
    protected Connection connection;
    protected String tableName;
    protected Class<T> entityClass;
    
    public BaseDAO(String tableName, Class<T> entityClass) {
        this.tableName = tableName;
        this.entityClass = entityClass;
        this.connection = DatabaseConnector.getInstance().getConnection();
    }
    
    // Common implementation methods
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract void mapEntityToStatement(PreparedStatement stmt, T entity) throws SQLException;
}
```

## DAO Implementations

### UserDAO

Handles user authentication and user management operations.

#### Key Methods

```java
public class UserDAO extends BaseDAO<User, Integer> {
    
    // Authentication
    public User authenticate(String username, String password);
    public boolean changePassword(int userId, String newPassword);
    
    // User management
    public User findByUsername(String username);
    public List<User> findByRole(Role role);
    public boolean isUsernameAvailable(String username);
    
    // Account management
    public void activateUser(int userId);
    public void deactivateUser(int userId);
    public void updateLastLogin(int userId);
}
```

#### Implementation Details

```java
public User authenticate(String username, String password) {
    String sql = "SELECT u.*, r.role_name, r.permissions " +
                "FROM users u " +
                "JOIN roles r ON u.role_id = r.role_id " +
                "WHERE u.username = ? AND u.password_hash = ? AND u.is_active = true";
    
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, username);
        stmt.setString(2, hashPassword(password));
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapResultSetToUser(rs);
        }
    } catch (SQLException e) {
        throw new DAOException("Authentication failed", e);
    }
    return null;
}

private User mapResultSetToUser(ResultSet rs) throws SQLException {
    User user = new User();
    user.setUserId(rs.getInt("user_id"));
    user.setUsername(rs.getString("username"));
    user.setRole(new Role(rs.getInt("role_id"), rs.getString("role_name")));
    user.setCreatedAt(rs.getTimestamp("created_at"));
    user.setLastLogin(rs.getTimestamp("last_login"));
    user.setActive(rs.getBoolean("is_active"));
    return user;
}
```

### StudentDAO

Manages student records and academic information.

#### Key Methods

```java
public class StudentDAO extends BaseDAO<Student, String> {
    
    // Student management
    public Student findByEmail(String email);
    public List<Student> findByDepartment(int departmentId);
    public List<Student> findByAdvisor(String advisorId);
    
    // Academic records
    public List<Course> getEnrolledCourses(String studentId, String semester, int year);
    public List<Takes> getAcademicHistory(String studentId);
    public double calculateGPA(String studentId);
    
    // Enrollment management
    public boolean enrollInSection(String studentId, int sectionId);
    public boolean dropFromSection(String studentId, int sectionId);
    public boolean hasPrerequisites(String studentId, String courseId);
}
```

#### Implementation Example

```java
public List<Course> getEnrolledCourses(String studentId, String semester, int year) {
    String sql = "SELECT c.* FROM courses c " +
                "JOIN sections s ON c.course_id = s.course_id " +
                "JOIN takes t ON s.section_id = t.section_id " +
                "WHERE t.student_id = ? AND t.semester = ? AND t.year = ?";
    
    List<Course> courses = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, studentId);
        stmt.setString(2, semester);
        stmt.setInt(3, year);
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            courses.add(mapResultSetToCourse(rs));
        }
    } catch (SQLException e) {
        throw new DAOException("Failed to retrieve enrolled courses", e);
    }
    return courses;
}

public double calculateGPA(String studentId) {
    String sql = "SELECT AVG(CASE " +
                "WHEN grade = 'A+' THEN 4.3 " +
                "WHEN grade = 'A' THEN 4.0 " +
                "WHEN grade = 'A-' THEN 3.7 " +
                "WHEN grade = 'B+' THEN 3.3 " +
                "WHEN grade = 'B' THEN 3.0 " +
                "WHEN grade = 'B-' THEN 2.7 " +
                "WHEN grade = 'C+' THEN 2.3 " +
                "WHEN grade = 'C' THEN 2.0 " +
                "WHEN grade = 'C-' THEN 1.7 " +
                "WHEN grade = 'D' THEN 1.0 " +
                "WHEN grade = 'F' THEN 0.0 " +
                "END) as gpa " +
                "FROM takes WHERE student_id = ? AND grade IS NOT NULL";
    
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, studentId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble("gpa");
        }
    } catch (SQLException e) {
        throw new DAOException("Failed to calculate GPA", e);
    }
    return 0.0;
}
```

### CourseDAO

Manages course information and prerequisites.

#### Key Methods

```java
public class CourseDAO extends BaseDAO<Course, String> {
    
    // Course management
    public List<Course> findByDepartment(int departmentId);
    public List<Course> searchByTitle(String titlePattern);
    
    // Prerequisites
    public List<Course> getPrerequisites(String courseId);
    public void addPrerequisite(String courseId, String prereqId);
    public void removePrerequisite(String courseId, String prereqId);
    
    // Sections
    public List<Section> getSections(String courseId, String semester, int year);
    public Section createSection(String courseId, Section section);
}
```

### InstructorDAO

Manages instructor information and teaching assignments.

#### Key Methods

```java
public class InstructorDAO extends BaseDAO<Instructor, String> {
    
    // Instructor management
    public List<Instructor> findByDepartment(int departmentId);
    public Instructor findByEmail(String email);
    
    // Teaching assignments
    public List<Section> getTeachingSections(String instructorId, String semester, int year);
    public List<Course> getTaughtCourses(String instructorId);
    public int getTeachingLoad(String instructorId, String semester, int year);
    
    // Schedule management
    public boolean hasScheduleConflict(String instructorId, int timeSlotId, String semester, int year);
    public List<TimeSlot> getSchedule(String instructorId, String semester, int year);
}
```

### MarksDAO

Handles grade entry and result management.

#### Key Methods

```java
public class MarksDAO extends BaseDAO<Marks, Integer> {
    
    // Grade entry
    public void enterGrade(String studentId, int sectionId, String grade);
    public void updateGrade(String studentId, int sectionId, String grade);
    
    // Result queries
    public List<Marks> getStudentGrades(String studentId);
    public List<Marks> getSectionGrades(int sectionId);
    public Result calculateResult(String studentId, String semester, int year);
    
    // Grade statistics
    public Map<String, Integer> getGradeDistribution(int sectionId);
    public double getSectionAverage(int sectionId);
    public List<Student> getHonorsList(String semester, int year);
}
```

#### Implementation Example

```java
public void enterGrade(String studentId, int sectionId, String grade) {
    // First, check if enrollment exists
    String checkSql = "SELECT 1 FROM takes WHERE student_id = ? AND section_id = ?";
    String updateSql = "UPDATE takes SET grade = ? WHERE student_id = ? AND section_id = ?";
    
    try {
        // Check enrollment
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, studentId);
            checkStmt.setInt(2, sectionId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                throw new IllegalArgumentException("Student not enrolled in section");
            }
        }
        
        // Update grade
        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
            updateStmt.setString(1, grade);
            updateStmt.setString(2, studentId);
            updateStmt.setInt(3, sectionId);
            
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Failed to update grade");
            }
        }
        
        // Create audit record
        createGradeAudit(studentId, sectionId, grade);
        
    } catch (SQLException e) {
        throw new DAOException("Failed to enter grade", e);
    }
}

public Map<String, Integer> getGradeDistribution(int sectionId) {
    String sql = "SELECT grade, COUNT(*) as count " +
                "FROM takes WHERE section_id = ? AND grade IS NOT NULL " +
                "GROUP BY grade ORDER BY grade";
    
    Map<String, Integer> distribution = new LinkedHashMap<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, sectionId);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            distribution.put(rs.getString("grade"), rs.getInt("count"));
        }
    } catch (SQLException e) {
        throw new DAOException("Failed to get grade distribution", e);
    }
    return distribution;
}
```

### SectionDAO

Manages course sections and scheduling.

#### Key Methods

```java
public class SectionDAO extends BaseDAO<Section, Integer> {
    
    // Section management
    public List<Section> findByCourse(String courseId);
    public List<Section> findBySemester(String semester, int year);
    public List<Section> findByInstructor(String instructorId);
    
    // Enrollment management
    public List<Student> getEnrolledStudents(int sectionId);
    public int getEnrollmentCount(int sectionId);
    public boolean hasCapacity(int sectionId);
    
    // Schedule management
    public boolean hasScheduleConflict(int timeSlotId, String classroomId, String semester, int year);
    public List<Section> getScheduleByRoom(String classroomId, String semester, int year);
}
```

### PasswordRequestDAO

Manages password reset requests.

#### Key Methods

```java
public class PasswordRequestDAO extends BaseDAO<PasswordRequest, Integer> {
    
    // Request management
    public PasswordRequest createRequest(int userId, String reason);
    public void approveRequest(int requestId, int approvedBy);
    public void rejectRequest(int requestId, int rejectedBy, String reason);
    
    // Query methods
    public List<PasswordRequest> getPendingRequests();
    public List<PasswordRequest> getRequestsByUser(int userId);
    public PasswordRequest findActiveRequest(int userId);
}
```

## Advanced DAO Features

### Transaction Support

```java
public abstract class TransactionalDAO<T, ID> extends BaseDAO<T, ID> {
    
    public void executeInTransaction(Runnable operation) {
        try {
            connection.setAutoCommit(false);
            operation.run();
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                // Log rollback exception
            }
            throw new DAOException("Transaction failed", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Log exception
            }
        }
    }
}
```

### Pagination Support

```java
public class PaginatedResult<T> {
    private List<T> data;
    private int totalCount;
    private int pageSize;
    private int currentPage;
    
    // Constructors, getters, setters
}

public PaginatedResult<Student> findStudentsPaginated(int page, int pageSize, String sortBy) {
    String countSql = "SELECT COUNT(*) FROM students";
    String dataSql = "SELECT * FROM students ORDER BY " + sortBy + 
                    " LIMIT ? OFFSET ?";
    
    try {
        // Get total count
        int totalCount;
        try (PreparedStatement countStmt = connection.prepareStatement(countSql)) {
            ResultSet rs = countStmt.executeQuery();
            rs.next();
            totalCount = rs.getInt(1);
        }
        
        // Get page data
        List<Student> students = new ArrayList<>();
        try (PreparedStatement dataStmt = connection.prepareStatement(dataSql)) {
            dataStmt.setInt(1, pageSize);
            dataStmt.setInt(2, (page - 1) * pageSize);
            
            ResultSet rs = dataStmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        
        return new PaginatedResult<>(students, totalCount, pageSize, page);
        
    } catch (SQLException e) {
        throw new DAOException("Failed to retrieve paginated students", e);
    }
}
```

### Caching Layer

```java
public abstract class CachedDAO<T, ID> extends BaseDAO<T, ID> {
    private final Cache<ID, T> cache;
    
    public CachedDAO(String tableName, Class<T> entityClass) {
        super(tableName, entityClass);
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();
    }
    
    @Override
    public T findById(ID id) {
        return cache.get(id, () -> super.findById(id));
    }
    
    @Override
    public void update(T entity) {
        super.update(entity);
        ID id = getEntityId(entity);
        cache.put(id, entity);
    }
    
    @Override
    public void delete(ID id) {
        super.delete(id);
        cache.invalidate(id);
    }
    
    protected abstract ID getEntityId(T entity);
}
```

## Error Handling

### Custom DAO Exceptions

```java
public class DAOException extends RuntimeException {
    public DAOException(String message) {
        super(message);
    }
    
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class EntityNotFoundException extends DAOException {
    public EntityNotFoundException(String entityType, Object id) {
        super(String.format("%s with id %s not found", entityType, id));
    }
}

public class DuplicateEntityException extends DAOException {
    public DuplicateEntityException(String entityType, String field, Object value) {
        super(String.format("%s with %s '%s' already exists", entityType, field, value));
    }
}
```

### Validation

```java
public abstract class ValidatingDAO<T, ID> extends BaseDAO<T, ID> {
    
    @Override
    public void save(T entity) {
        validate(entity);
        super.save(entity);
    }
    
    @Override
    public void update(T entity) {
        validate(entity);
        super.update(entity);
    }
    
    protected abstract void validate(T entity);
    
    protected void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
    
    protected void validateStringLength(String value, String fieldName, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }
}
```

## Best Practices

### Resource Management

```java
// Always use try-with-resources
public List<Student> findAllStudents() {
    String sql = "SELECT * FROM students";
    List<Student> students = new ArrayList<>();
    
    try (PreparedStatement stmt = connection.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            students.add(mapResultSetToStudent(rs));
        }
    } catch (SQLException e) {
        throw new DAOException("Failed to retrieve students", e);
    }
    
    return students;
}
```

### Parameter Validation

```java
public Student findById(String studentId) {
    if (studentId == null || studentId.trim().isEmpty()) {
        throw new IllegalArgumentException("Student ID cannot be null or empty");
    }
    
    // Implementation
}
```

### Logging

```java
private static final Logger logger = LoggerFactory.getLogger(StudentDAO.class);

public void save(Student student) {
    logger.debug("Saving student: {}", student.getStudentId());
    
    try {
        // Save implementation
        logger.info("Successfully saved student: {}", student.getStudentId());
    } catch (Exception e) {
        logger.error("Failed to save student: " + student.getStudentId(), e);
        throw e;
    }
}
```

The DAO module provides a robust, maintainable layer for all database operations, ensuring data integrity, performance, and consistency across the University Management System.