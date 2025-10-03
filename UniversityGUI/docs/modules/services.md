# Services Module Documentation

The Services module implements business logic and orchestrates DAOs and models to perform application use-cases.

## Overview

Services sit between the UI and DAO layers. They validate input, enforce business rules, handle transactions where necessary, and convert DAO exceptions into user-friendly errors.

## Common Responsibilities

- Input validation and normalization
- Transaction management for multi-step operations
- Permission checks and role-based business logic
- Logging and auditing
- Mapping between DTOs and model entities when needed

## Core Services

### UserService

Responsibilities:
- Authenticate users and initiate `UserSession`
- Register and manage user accounts
- Change and reset passwords (via `PasswordService`)
- Assign and manage roles
- Record login history

Key methods:
```java
public class UserService {
    public User login(String username, String password) throws AuthenticationException;
    public void logout();
    public User register(User user) throws ValidationException;
    public boolean changePassword(int userId, String oldPassword, String newPassword);
    public boolean forceResetPassword(int userId, String newPassword);
    public List<User> listUsers();
}
```

Implementation notes:
- Use `UserDAO` for persistence
- Hash passwords using a secure algorithm (bcrypt/PBKDF2)
- Record login attempts in `LoginHistoryDAO`
- Limit login attempts to mitigate brute-force attacks

### StudentService

Responsibilities:
- Create and update student profiles
- Manage enrollments and academic records
- Calculate GPA and academic standing

Key methods:
```java
public class StudentService {
    public Student createStudent(Student student) throws ValidationException;
    public Student updateStudent(Student student) throws ValidationException;
    public boolean enrollStudentInSection(String studentId, int sectionId) throws BusinessException;
    public boolean dropStudentFromSection(String studentId, int sectionId) throws BusinessException;
    public double calculateGPA(String studentId);
}
```

Implementation notes:
- Check prerequisites before enrollment (use `CourseDAO`/`PrereqDAO`)
- Enclose enroll/drop operations in a transaction to ensure data consistency
- Update section `enrolled_count` atomically to avoid race conditions

### CourseService

Responsibilities:
- Manage course metadata and prerequisites
- Create and manage sections
- Search and list courses

Key methods:
```java
public class CourseService {
    public Course createCourse(Course course) throws ValidationException;
    public Course updateCourse(Course course) throws ValidationException;
    public void addPrerequisite(String courseId, String prereqId);
    public void removePrerequisite(String courseId, String prereqId);
    public List<Section> createSections(String courseId, List<Section> sections);
}
```

Implementation notes:
- Validate course code formats
- Prevent circular prerequisites
- Notify dependent services when course metadata changes (e.g., sections or credits)

### MarksService

Responsibilities:
- Enter and update grades
- Calculate results and generate transcripts
- Produce grade statistics and reports

Key methods:
```java
public class MarksService {
    public void enterGrade(String studentId, int sectionId, String grade) throws BusinessException;
    public void updateGrade(String studentId, int sectionId, String grade) throws BusinessException;
    public Result calculateResult(String studentId, String semester, int year);
    public Map<String, Integer> getGradeDistribution(int sectionId);
}
```

Implementation notes:
- Validate grade scale and allowed values
- Create audit trails for grade changes
- Use transactions when updating multiple related records (e.g., takes + marks)

### PasswordService

Responsibilities:
- Create and manage password reset requests
- Force resets and approvals by administrators
- Generate secure, time-limited reset tokens

Key methods:
```java
public class PasswordService {
    public PasswordRequest requestPasswordReset(int userId, String reason);
    public boolean approvePasswordRequest(int requestId, int adminId);
    public boolean rejectPasswordRequest(int requestId, int adminId, String reason);
    public boolean resetPasswordWithToken(String token, String newPassword);
}
```

Implementation notes:
- Store tokens hashed and with expiration timestamps
- Limit frequency of reset requests
- Notify users via email on approvals/rejections (optional)

## Transaction Management

- For operations that modify multiple tables (enrollments, grade entry), use manual transactions via `DatabaseConnector`:

```java
Connection conn = DatabaseConnector.getInstance().getConnection();
try {
    conn.setAutoCommit(false);
    // multiple DAO operations using the same connection
    conn.commit();
} catch (Exception e) {
    conn.rollback();
    throw e;
} finally {
    conn.setAutoCommit(true);
}
```

- Alternatively, implement a `TransactionalService` base class that manages transaction lifecycle and retries.

## Error Handling

- Convert `SQLException` into domain `ServiceException`/`BusinessException` with meaningful messages
- Validate inputs early and throw `ValidationException` with field-level errors
- Log exceptions with contextual information but avoid leaking sensitive data (passwords)

## Examples

### Enrolling a Student (pseudo-code)

```java
public boolean enrollStudentInSection(String studentId, int sectionId) {
    Connection conn = DatabaseConnector.getInstance().getConnection();
    try {
        conn.setAutoCommit(false);

        if (!studentDAO.exists(studentId)) throw new BusinessException("Student not found");
        if (!sectionDAO.hasCapacity(sectionId)) throw new BusinessException("Section full");
        if (takesDAO.exists(studentId, sectionId)) throw new BusinessException("Already enrolled");

        takesDAO.insert(studentId, sectionId);
        sectionDAO.incrementEnrollmentCount(sectionId);

        conn.commit();
        return true;
    } catch (Exception e) {
        conn.rollback();
        throw new ServiceException("Enrollment failed", e);
    } finally {
        conn.setAutoCommit(true);
    }
}
```

## Best Practices

- Keep services thin: delegate data operations to DAOs
- Keep business rules in services, not in UI or DAOs
- Use DTOs for cross-layer data transfer when needed
- Add unit tests for business logic and integration tests for transactions

## Next steps

- Link service docs from `docs/modules/README.md` and `docs/README.md`
- Create `docs/modules/ui.md` next
