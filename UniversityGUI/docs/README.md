# University Management System

A comprehensive Java Swing-based GUI application for managing university operations including student management, course administration, marks entry, and user access control.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Setup Guide](./setup-guide.md)
- [Architecture](./architecture.md)
- [Module Documentation](#module-documentation)
- [User Guide](./user-guide.md)
- [Developer Guide](./developer-guide.md)
- [API Reference](./api-reference.md)

## 🎯 Project Overview

This University Management System provides:
- **User Management**: Role-based access control with different user types
- **Student Management**: Student registration, records, and academic tracking
- **Course Management**: Course creation, scheduling, and prerequisites
- **Marks Management**: Grade entry and result processing
- **Administrative Tools**: Data exploration and system administration

## 🏗️ Module Documentation

### Core Modules
- [Database Layer](./modules/database.md) - Database connectivity and operations
- [Data Access Objects (DAO)](./modules/dao.md) - Data persistence layer
- [Models](./modules/models.md) - Entity classes and data structures
- [Services](./modules/services.md) - Business logic layer
- [User Interface](./modules/ui.md) - GUI components and dialogs

### Feature Modules
- [User Management](./features/user-management.md)
- [Student Management](./features/student-management.md)
- [Course Management](./features/course-management.md)
- [Marks & Results](./features/marks-results.md)
- [Password Management](./features/password-management.md)
- [Data Explorer](./features/data-explorer.md)

## 🚀 Quick Start

1. **Setup Database**: Run the SQL script from `sqlDB_files/UniversityDB.sql`
2. **Build Project**: `mvn clean package`
3. **Run Application**: `mvn exec:java -Dexec.mainClass="com.university.app.App"`

For detailed setup instructions, see [Setup Guide](./setup-guide.md).

## 📚 Documentation Structure

```
docs/
├── README.md                    # This file
├── setup-guide.md              # Installation and setup
├── architecture.md             # System architecture
├── user-guide.md               # End-user documentation
├── developer-guide.md          # Developer documentation
├── api-reference.md            # API documentation
├── modules/                    # Module-specific docs
│   ├── database.md
│   ├── dao.md
│   ├── models.md
│   ├── services.md
│   └── ui.md
└── features/                   # Feature-specific docs
    ├── user-management.md
    ├── student-management.md
    ├── course-management.md
    ├── marks-results.md
    ├── password-management.md
    └── data-explorer.md
```

## 🔗 Key Features

- **Role-based Authentication**: Admin, Entry, and Reporting user roles
- **Comprehensive Student Records**: Complete academic tracking
- **Course Prerequisites**: Automatic prerequisite validation
- **Grade Management**: Flexible marks entry and result calculation
- **Data Visualization**: Advanced data exploration tools
- **Password Security**: Secure password management with reset functionality

## 🛠️ Technology Stack

- **Frontend**: Java Swing
- **Backend**: Java
- **Database**: SQL (configurable)
- **Build Tool**: Maven
- **Architecture**: MVC with DAO pattern