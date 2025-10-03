# Setup Guide

This guide explains how to set up, configure, build, and run the University Management System locally.

It covers two database options (MySQL and SQLite), environment variables, build steps with Maven, and common troubleshooting tips.

## Prerequisites

- Java Development Kit (JDK) 8 or newer. Verify with:

```bash
java -version
```

- Maven 3.6+ installed. Verify with:

```bash
mvn -v
```

- A SQL database server (MySQL, PostgreSQL) or SQLite for quick local runs.

- Optional: An IDE such as IntelliJ IDEA or VS Code with Java support.

## Project layout

```
UniversityGUI/
├── pom.xml
├── src/main/java/com/university/app/...
├── src/main/resources/
├── docs/
│   ├── README.md
│   └── modules/
│       ├── models.md
│       ├── services.md
│       └── ui.md
└── sqlDB_files/UniversityDB.sql
```

## Database setup

The project includes `sqlDB_files/UniversityDB.sql` which contains the schema and seed data. Choose one of the following options.

### Option A — MySQL (recommended for production-like runs)

1. Install MySQL and start the server.
2. Create a database and user:

```sql
CREATE DATABASE university_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'uni_user'@'localhost' IDENTIFIED BY 'uni_password';
GRANT ALL PRIVILEGES ON university_db.* TO 'uni_user'@'localhost';
FLUSH PRIVILEGES;
```

3. Import the schema and seed data:

```bash
mysql -u uni_user -p university_db < sqlDB_files/UniversityDB.sql
```

4. Update the connection settings in `DatabaseConnector.java` (or configure via environment variables if your project uses them):

```java
private static final String URL = "jdbc:mysql://localhost:3306/university_db?useSSL=false&serverTimezone=UTC";
private static final String USERNAME = "uni_user";
private static final String PASSWORD = "uni_password";
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

> Note: If your `DatabaseConnector` loads configuration from environment variables or a properties file, prefer that approach and set environment variables described in the "Configuration via environment variables" section below.

### Option B — SQLite (quick local run)

1. Install SQLite3.
2. Create a local database file and import the schema:

```bash
sqlite3 university.db < sqlDB_files/UniversityDB.sql
```

3. Update `DatabaseConnector.java` to use SQLite (example):

```java
private static final String URL = "jdbc:sqlite:./university.db";
private static final String DRIVER = "org.sqlite.JDBC";
private static final String USERNAME = ""; // not used
private static final String PASSWORD = ""; // not used
```

4. Add SQLite JDBC dependency to `pom.xml` if not present:

```xml
<dependency>
  <groupId>org.xerial</groupId>
  <artifactId>sqlite-jdbc</artifactId>
  <version>3.41.2.1</version>
</dependency>
```

## Configuration via environment variables

If you prefer not to modify the source code, configure the application with environment variables. Edit `DatabaseConnector` to read settings from environment variables (recommended). Example variables:

- `DB_URL` — JDBC URL (e.g., `jdbc:mysql://localhost:3306/university_db`)
- `DB_USERNAME` — Database username
- `DB_PASSWORD` — Database password
- `DB_DRIVER` — JDBC driver class (e.g., `com.mysql.cj.jdbc.Driver`)

Set them in Linux/macOS:

```bash
export DB_URL="jdbc:mysql://localhost:3306/university_db?useSSL=false&serverTimezone=UTC"
export DB_USERNAME="uni_user"
export DB_PASSWORD="uni_password"
export DB_DRIVER="com.mysql.cj.jdbc.Driver"
```

In Windows (PowerShell):

```powershell
setx DB_URL "jdbc:mysql://localhost:3306/university_db?useSSL=false&serverTimezone=UTC"
setx DB_USERNAME "uni_user"
setx DB_PASSWORD "uni_password"
setx DB_DRIVER "com.mysql.cj.jdbc.Driver"
```

## Build the project

From the project root (`UniversityGUI/`):

```bash
# download dependencies and compile
mvn clean compile

# build a package (jar in target/)
mvn package

# skip tests if you want a faster build
mvn package -DskipTests
```

If you plan to run from the IDE, import the project as a Maven project.

## Run the application

### Option A — Run with Maven exec plugin

```bash
mvn exec:java -Dexec.mainClass="com.university.app.App"
```

This uses Maven to run the main class. The exact main class path is `com.university.app.App` based on the repository layout.

### Option B — Run the packaged JAR

If the `pom.xml` configures the JAR with dependencies, run:

```bash
java -jar target/university-app-1.0-SNAPSHOT.jar
```

If the compiled JAR is not executable with dependencies bundled, run with the classpath:

```bash
java -cp target/university-app-1.0-SNAPSHOT.jar:target/dependency/* com.university.app.App
```

### Option C — Run from IDE

- Open the project in IntelliJ IDEA or VS Code (Java extension).
- Make sure the Project SDK is set to a compatible JDK.
- Run the `App` main class.

## Default credentials (seed data)

The sample DB may include seeded accounts. Common defaults (if present in the SQL):

- Admin: `admin` / `admin123`
- Entry user: `entry_user` / `entry123`
- Reporter: `report_user` / `report123`

If login fails, inspect the `users` table in the database to find seeded users and their passwords (note: passwords may be hashed).

## Troubleshooting

### Database connection errors

- Verify the DB server is running.
- Verify the JDBC URL and credentials.
- Check for driver dependency in `pom.xml` (MySQL: `mysql-connector-java`, SQLite: `sqlite-jdbc`).
- For MySQL 8+, use `com.mysql.cj.jdbc.Driver` and add `serverTimezone=UTC` or appropriate timezone.

### Maven build issues

- Force update dependencies:

```bash
mvn clean package -U
```

- If dependency resolution fails, check your network or Maven proxy settings.

### UI freezes / slow operations

- The UI must not execute long-running database operations on the EDT. Use `SwingWorker` or background threads.

### Missing main class

- Confirm main class `com.university.app.App` exists in `src/main/java` and compiles.

## Advanced

### Running with a connection pool

For better performance with many users, configure a connection pool (HikariCP). Add dependency and move `DatabaseConnector` to use the pool.

### Production packaging

Create a native installer or package with tools like jpackage (JDK 14+) or build OS-specific installers.

---

If you want, I can:

- Update `DatabaseConnector` to read environment variables and provide an example implementation.
- Add Maven profiles for `mysql` and `sqlite` to simplify builds.
- Add instructions to create systemd service or desktop launcher for the application.

Which improvement would you like next?
