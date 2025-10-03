# Database Module Documentation

The database module handles all database connectivity and low-level database operations for the University Management System.

## Overview

The database layer provides a centralized connection management system and serves as the foundation for all data persistence operations in the application.

## Key Components

### DatabaseConnector.java

The main database connection manager that implements the Singleton pattern to ensure a single database connection throughout the application lifecycle.

#### Core Functionality

```java
public class DatabaseConnector {
    private static DatabaseConnector instance;
    private Connection connection;
    
    // Singleton instance getter
    public static DatabaseConnector getInstance()
    
    // Get active database connection
    public Connection getConnection()
    
    // Test database connectivity
    public boolean testConnection()
    
    // Close database connection
    public void closeConnection()
}
```

#### Connection Configuration

The database connection supports multiple database types:

**MySQL Configuration**
```java
private static final String URL = "jdbc:mysql://localhost:3306/university_db";
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

**PostgreSQL Configuration**
```java
private static final String URL = "jdbc:postgresql://localhost:5432/university_db";
private static final String DRIVER = "org.postgresql.Driver";
```

**SQLite Configuration**
```java
private static final String URL = "jdbc:sqlite:university.db";
private static final String DRIVER = "org.sqlite.JDBC";
```

## Database Schema

### Core Tables

#### Users Table
```sql
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);
```

#### Students Table
```sql
CREATE TABLE students (
    student_id VARCHAR(20) PRIMARY KEY,
    user_id INT UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    enrollment_date DATE,
    department_id INT,
    advisor_id VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (department_id) REFERENCES departments(department_id),
    FOREIGN KEY (advisor_id) REFERENCES instructors(instructor_id)
);
```

#### Courses Table
```sql
CREATE TABLE courses (
    course_id VARCHAR(20) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    credits INT NOT NULL,
    department_id INT,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);
```

#### Sections Table
```sql
CREATE TABLE sections (
    section_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id VARCHAR(20),
    semester VARCHAR(20),
    year INT,
    instructor_id VARCHAR(20),
    classroom_id VARCHAR(20),
    time_slot_id INT,
    capacity INT,
    enrolled_count INT DEFAULT 0,
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id),
    FOREIGN KEY (classroom_id) REFERENCES classrooms(classroom_id),
    FOREIGN KEY (time_slot_id) REFERENCES time_slots(time_slot_id)
);
```

### Relationship Tables

#### Takes (Student Enrollments)
```sql
CREATE TABLE takes (
    student_id VARCHAR(20),
    section_id INT,
    semester VARCHAR(20),
    year INT,
    grade VARCHAR(2),
    enrollment_date DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY (student_id, section_id, semester, year),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id)
);
```

#### Teaches (Instructor Assignments)
```sql
CREATE TABLE teaches (
    instructor_id VARCHAR(20),
    section_id INT,
    semester VARCHAR(20),
    year INT,
    PRIMARY KEY (instructor_id, section_id, semester, year),
    FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id)
);
```

#### Prerequisites
```sql
CREATE TABLE prereq (
    course_id VARCHAR(20),
    prereq_id VARCHAR(20),
    PRIMARY KEY (course_id, prereq_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (prereq_id) REFERENCES courses(course_id)
);
```

### Support Tables

#### Departments
```sql
CREATE TABLE departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL,
    building VARCHAR(50),
    budget DECIMAL(12,2)
);
```

#### Instructors
```sql
CREATE TABLE instructors (
    instructor_id VARCHAR(20) PRIMARY KEY,
    user_id INT UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    department_id INT,
    hire_date DATE,
    salary DECIMAL(10,2),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);
```

#### Classrooms
```sql
CREATE TABLE classrooms (
    classroom_id VARCHAR(20) PRIMARY KEY,
    building VARCHAR(50),
    room_number VARCHAR(10),
    capacity INT
);
```

#### Time Slots
```sql
CREATE TABLE time_slots (
    time_slot_id INT PRIMARY KEY AUTO_INCREMENT,
    start_time TIME,
    end_time TIME,
    days VARCHAR(20)
);
```

#### Roles
```sql
CREATE TABLE roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    permissions TEXT
);
```

## Connection Management

### Connection Lifecycle

1. **Initialization**: Connection established when first requested
2. **Reuse**: Same connection reused for subsequent operations
3. **Validation**: Connection tested before use
4. **Cleanup**: Connection closed on application shutdown

### Error Handling

```java
public Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database", e);
        }
    }
    return connection;
}
```

### Connection Testing

```java
public boolean testConnection() {
    try {
        Connection conn = getConnection();
        return conn != null && !conn.isClosed();
    } catch (SQLException e) {
        return false;
    }
}
```

## Database Operations

### Transaction Management

The database connector supports both auto-commit and manual transaction management:

```java
// Auto-commit mode (default)
connection.setAutoCommit(true);

// Manual transaction management
connection.setAutoCommit(false);
try {
    // Multiple operations
    operation1();
    operation2();
    connection.commit();
} catch (SQLException e) {
    connection.rollback();
    throw e;
}
```

### Prepared Statements

All database operations use prepared statements to prevent SQL injection:

```java
String sql = "SELECT * FROM students WHERE student_id = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, studentId);
ResultSet rs = stmt.executeQuery();
```

### Batch Operations

For bulk operations, the database layer supports batch processing:

```java
PreparedStatement stmt = connection.prepareStatement(sql);
for (Entity entity : entities) {
    stmt.setParameter(1, entity.getValue());
    stmt.addBatch();
}
stmt.executeBatch();
```

## Performance Optimization

### Connection Pooling

For production environments, consider implementing connection pooling:

```java
// Using HikariCP or similar connection pool
HikariConfig config = new HikariConfig();
config.setJdbcUrl(URL);
config.setUsername(USERNAME);
config.setPassword(PASSWORD);
config.setMaximumPoolSize(10);
HikariDataSource dataSource = new HikariDataSource(config);
```

### Query Optimization

- Use indexes on frequently queried columns
- Optimize JOIN operations
- Limit result sets with LIMIT clauses
- Use appropriate data types

### Caching Strategy

Implement caching for frequently accessed data:

```java
// Simple cache implementation
private Map<String, Object> cache = new ConcurrentHashMap<>();

public Object getCachedData(String key) {
    return cache.computeIfAbsent(key, this::loadFromDatabase);
}
```

## Database Migrations

### Schema Versioning

Track database schema versions:

```sql
CREATE TABLE schema_version (
    version INT PRIMARY KEY,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT
);
```

### Migration Scripts

Organize migration scripts by version:

```
sqlDB_files/
├── migrations/
│   ├── 001_initial_schema.sql
│   ├── 002_add_indexes.sql
│   ├── 003_add_audit_tables.sql
│   └── ...
└── UniversityDB.sql  # Complete schema
```

## Security Considerations

### Database Security

1. **Connection Security**: Use SSL/TLS for database connections
2. **Access Control**: Implement proper database user permissions
3. **SQL Injection Prevention**: Always use prepared statements
4. **Data Encryption**: Encrypt sensitive data at rest

### Configuration Security

```java
// Load from environment variables or secure configuration
private static final String USERNAME = System.getenv("DB_USERNAME");
private static final String PASSWORD = System.getenv("DB_PASSWORD");
```

## Monitoring and Logging

### Connection Monitoring

```java
public void logConnectionStatus() {
    try {
        Connection conn = getConnection();
        DatabaseMetaData metaData = conn.getMetaData();
        logger.info("Database: " + metaData.getDatabaseProductName());
        logger.info("Version: " + metaData.getDatabaseProductVersion());
        logger.info("Connection valid: " + conn.isValid(5));
    } catch (SQLException e) {
        logger.error("Connection check failed", e);
    }
}
```

### Query Logging

For debugging and performance monitoring:

```java
public void logQuery(String sql, Object[] parameters) {
    logger.debug("Executing query: " + sql);
    logger.debug("Parameters: " + Arrays.toString(parameters));
}
```

## Best Practices

1. **Always close resources**: Use try-with-resources for automatic cleanup
2. **Handle exceptions properly**: Provide meaningful error messages
3. **Use connection validation**: Test connections before use
4. **Implement retry logic**: Handle temporary connection failures
5. **Monitor performance**: Log slow queries and connection issues
6. **Regular backups**: Implement automated database backups
7. **Documentation**: Keep database schema documentation updated

## Troubleshooting

### Common Issues

#### Connection Timeout
```java
// Increase connection timeout
DriverManager.setLoginTimeout(30);
```

#### Memory Leaks
```java
// Always close ResultSets and Statements
try (PreparedStatement stmt = conn.prepareStatement(sql);
     ResultSet rs = stmt.executeQuery()) {
    // Process results
}
```

#### Character Encoding
```java
// Ensure proper character encoding
String url = "jdbc:mysql://localhost:3306/university_db?useUnicode=true&characterEncoding=UTF-8";
```

This database module provides a robust foundation for all data operations in the University Management System, ensuring reliable connectivity, data integrity, and optimal performance.