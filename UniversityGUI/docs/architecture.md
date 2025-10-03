# System Architecture

This document describes the architecture and design patterns used in the University Management System.

## Architecture Overview

The application follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────┐
│           Presentation Layer        │
│         (UI Components)             │
├─────────────────────────────────────┤
│           Service Layer             │
│        (Business Logic)             │
├─────────────────────────────────────┤
│        Data Access Layer            │
│            (DAO Pattern)            │
├─────────────────────────────────────┤
│           Model Layer               │
│        (Entity Classes)             │
├─────────────────────────────────────┤
│          Database Layer             │
│        (SQL Database)               │
└─────────────────────────────────────┘
```

## Design Patterns

### 1. Model-View-Controller (MVC)
- **Model**: Entity classes representing database tables
- **View**: Swing UI components and dialogs
- **Controller**: Service layer managing business logic

### 2. Data Access Object (DAO)
- Abstracts database operations
- Provides CRUD operations for each entity
- Implements generic DAO interface for consistency

### 3. Singleton Pattern
- `DatabaseConnector`: Ensures single database connection
- `UserSession`: Manages current user session state

### 4. Factory Pattern
- Dialog creation for different entity types
- Service instantiation based on user roles

## Package Structure

```
com.university.app/
├── App.java                    # Main entry point
├── dao/                        # Data Access Layer
│   ├── GenericDAO.java         # Base DAO interface
│   ├── DatabaseDAO.java        # Database operations
│   ├── UserDAO.java           # User-specific operations
│   ├── StudentDAO.java        # Student operations
│   ├── CourseDAO.java         # Course operations
│   └── ...                    # Other entity DAOs
├── db/                        # Database Layer
│   └── DatabaseConnector.java # Database connection management
├── model/                     # Entity Models
│   ├── User.java             # User entity
│   ├── Student.java          # Student entity
│   ├── Course.java           # Course entity
│   └── ...                   # Other entities
├── service/                   # Business Logic Layer
│   └── UserSession.java      # Session management
└── ui/                       # Presentation Layer
    ├── MainFrame.java        # Main application window
    ├── LoginDialog.java      # Authentication dialog
    ├── DataExplorerFrame.java # Data exploration
    └── ...                   # Other UI components
```

## Layer Details

### Presentation Layer (UI)

#### Main Components
- **MainFrame**: Primary application window with tabbed interface
- **LoginDialog**: Authentication and user verification
- **DataExplorerFrame**: Advanced data viewing and manipulation
- **Various Dialogs**: Add/Edit forms for different entities

#### Key Features
- Role-based UI rendering
- Dynamic form generation
- Background image support
- Responsive layout management

#### UI Architecture
```java
MainFrame
├── TabbedPane
│   ├── UserManagementPanel (Admin only)
│   ├── MarksEntryPanel (Entry users)
│   ├── PasswordRequestPanel
│   └── DataExplorerPanel (Admin/Reporting)
└── MenuBar
    ├── File Menu
    ├── Tools Menu
    └── Help Menu
```

### Service Layer

#### UserSession
- Manages current user state
- Handles role-based permissions
- Maintains login history

```java
public class UserSession {
    private static UserSession instance;
    private User currentUser;
    private LoginHistory currentSession;
    
    public boolean hasPermission(String permission);
    public void logActivity(String activity);
}
```

### Data Access Layer (DAO)

#### Generic DAO Pattern
```java
public interface GenericDAO<T, ID> {
    T findById(ID id);
    List<T> findAll();
    void save(T entity);
    void update(T entity);
    void delete(ID id);
}
```

#### Specialized DAOs
Each entity has its own DAO with specific operations:

- **UserDAO**: Authentication, role management
- **StudentDAO**: Student enrollment, academic records
- **CourseDAO**: Course management, prerequisites
- **MarksDAO**: Grade entry, result calculation
- **InstructorDAO**: Faculty management, teaching assignments

### Model Layer

#### Entity Relationships
```
User (1) ←→ (1) Student
User (1) ←→ (1) Instructor

Student (M) ←→ (M) Course (via Takes)
Instructor (M) ←→ (M) Course (via Teaches)

Course (1) ←→ (M) Section
Course (M) ←→ (M) Course (via Prereq)

Student (M) ←→ (M) Section (via Takes)
Section (1) ←→ (M) Marks
```

#### Key Entities
- **User**: Authentication and authorization
- **Student**: Academic information and enrollment
- **Instructor**: Faculty details and assignments
- **Course**: Course information and prerequisites
- **Section**: Class sections and scheduling
- **Marks**: Grade records and results

### Database Layer

#### Connection Management
```java
public class DatabaseConnector {
    private static DatabaseConnector instance;
    private Connection connection;
    
    public Connection getConnection();
    public void closeConnection();
    public boolean testConnection();
}
```

#### Database Schema
- Normalized relational design
- Foreign key constraints
- Indexes for performance
- Stored procedures for complex operations

## Security Architecture

### Authentication Flow
```
1. User enters credentials
2. LoginDialog validates input
3. UserDAO verifies against database
4. UserSession stores authenticated user
5. UI adapts to user role permissions
```

### Authorization Levels
- **Administrator**: Full system access
- **Entry**: Limited to marks entry and student records
- **Reporting**: Read-only access to reports and data

### Password Security
- Hashed password storage
- Password complexity requirements
- Password reset functionality
- Session timeout management

## Data Flow

### Typical Operation Flow
```
UI Component → Service Layer → DAO Layer → Database
     ↓              ↓             ↓          ↓
User Action → Business Logic → SQL Query → Database
     ↑              ↑             ↑          ↑
UI Update ← Service Response ← DAO Result ← Query Result
```

### Example: Student Registration
1. **UI**: AddStudentDialog collects student information
2. **Service**: Validates business rules (duplicate check, etc.)
3. **DAO**: StudentDAO.save() executes INSERT statement
4. **Database**: Record stored with generated ID
5. **Response**: Success/failure propagated back to UI

## Error Handling

### Exception Hierarchy
```
ApplicationException
├── DatabaseException
│   ├── ConnectionException
│   └── QueryException
├── ValidationException
│   ├── DuplicateRecordException
│   └── InvalidDataException
└── SecurityException
    ├── AuthenticationException
    └── AuthorizationException
```

### Error Handling Strategy
- Try-catch blocks at appropriate levels
- User-friendly error messages
- Logging for debugging
- Graceful degradation

## Performance Considerations

### Database Optimization
- Connection pooling for multiple concurrent operations
- Prepared statements for parameterized queries
- Lazy loading for large datasets
- Caching for frequently accessed data

### UI Optimization
- Background threading for long-running operations
- Progressive loading for large tables
- Efficient event handling
- Memory management for large forms

## Extensibility

### Adding New Features
1. **Model**: Create entity class
2. **DAO**: Implement DAO interface
3. **Service**: Add business logic
4. **UI**: Create corresponding dialogs/panels
5. **Integration**: Update main frame and navigation

### Configuration
- Database connection parameters
- UI themes and styling
- Business rule parameters
- Security settings

## Testing Strategy

### Unit Testing
- DAO layer testing with embedded database
- Service layer testing with mocked DAOs
- Model validation testing

### Integration Testing
- Database integration tests
- UI automation testing
- End-to-end workflow testing

### Test Structure
```
src/test/java/
├── dao/           # DAO layer tests
├── service/       # Service layer tests
├── model/         # Model validation tests
└── integration/   # Integration tests
```

## Deployment Architecture

### Standalone Application
- Single JAR with embedded dependencies
- Local database file (SQLite) or remote database
- Desktop installation package

### Network Deployment
- Client-server architecture option
- Shared database server
- Multiple concurrent users

This architecture provides a solid foundation for a maintainable, scalable university management system with clear separation of concerns and room for future enhancements.