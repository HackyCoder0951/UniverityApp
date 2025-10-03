# UI Module Documentation

This document describes the presentation layer implemented with Java Swing. It explains core components, threading considerations, and how to extend the UI.

## Overview

The UI module contains all Swing components: frames, panels, dialogs, and custom renderers. The UI interacts with the `service` layer to perform operations and with DAOs indirectly through services.

## Main Components

### `MainFrame`

- Primary application window.
- Holds the main `JTabbedPane` with panels for Users, Students, Courses, Marks, and Data Explorer.
- Responsible for initializing menus, toolbar, and global keyboard shortcuts.
- Handles session start/stop (showing login dialog when needed) and role-based UI rendering.

### `LoginDialog`

- Modal dialog for user authentication.
- Validates input locally before calling `UserService.login`.
- On successful login, stores session information in `UserSession` and closes.

### Data Panels

- `UserManagementPanel` — CRUD UI for users; admin-only.
- `StudentPanel` — List and edit student records; supports search and pagination.
- `CoursePanel` — Manage courses and prerequisites.
- `MarksEntryPanel` — Enter and update grades; optimized for data entry workflows.
- `DataExplorerPanel` — Flexible data viewer for ad-hoc queries and exports.

### Dialogs

- `AddStudentDialog`, `AddCourseDialog`, `AddInstructorDialog` — Forms for creating entities.
- `ChangePasswordDialog` — Self-service password change.
- `ForcePasswordResetDialog` — Admin trigger to reset passwords.
- `DynamicRecordDialog` — Generic dialog for small record edits.

## Swing Threading

- Always perform UI updates on the Event Dispatch Thread (EDT).
- Use `SwingUtilities.invokeLater()` or `SwingWorker` for long-running tasks.

Example:

```java
SwingWorker<List<Student>, Void> worker = new SwingWorker<>() {
    @Override
    protected List<Student> doInBackground() throws Exception {
        return studentService.findAll();
    }

    @Override
    protected void done() {
        try {
            List<Student> students = get();
            tableModel.setStudents(students);
        } catch (Exception e) {
            // show error
        }
    }
};
worker.execute();
```

## Table Models and Renderers

- Use custom `TableModel` implementations (e.g., `StudentTableModel`) to back `JTable` components.
- Implement `TableCellRenderer` and `TableCellEditor` for specialized column types (dates, status badges, action buttons).
- Support sorting and filtering via `TableRowSorter`.

## Actions and Commands

- Use `Action` classes for menu and toolbar items to centralize behavior and enable/disable logic based on permissions.
- Provide keyboard accelerators and mnemonics for accessibility.

## Validation and Feedback

- Validate forms client-side with informative messages.
- Use `JOptionPane` for simple feedback; use inline validation for forms.
- Display long-running progress with `JProgressBar` in modal dialogs or status bar.

## Internationalization (i18n)

- Externalize strings into `ResourceBundle` files in `src/main/resources`.
- Use `MessageFormat` for dynamic messages.

## Extending the UI

- Add new panels by implementing `ModulePanel` interface and registering it in `MainFrame`.
- Follow consistent look-and-feel: central `UIManager` configuration.
- Place common components (search box, pagination control) in a shared package for reuse.

## Accessibility

- Set accessible names on components for screen readers.
- Ensure keyboard navigation and focus traversal.

## Example: Adding a new module panel

1. Create `MyModulePanel extends JPanel implements ModulePanel`.
2. Implement required lifecycle methods: `init()`, `onShow()`, `onHide()`.
3. Register in `MainFrame` using `mainFrame.registerModulePanel("MyModule", new MyModulePanel());`.

## Best Practices

- Keep UI code focused on presentation; delegate business logic to services.
- Avoid database calls directly from UI code.
- Reuse components and models to reduce duplication.
- Unit test UI logic where possible (e.g., view models, input validators).

## Troubleshooting

- UI frozen: ensure heavy work runs off the EDT.
- Table slow with many rows: implement pagination or lazy loading.
- Memory leak: unregister listeners and stop background workers on panel close.

## Files of Interest

- `src/main/java/com/university/app/ui/MainFrame.java`
- `src/main/java/com/university/app/ui/LoginDialog.java`
- `src/main/java/com/university/app/ui/*.java` (other panels and dialogs)


---

Next steps: update `docs/README.md` index to link module docs and finish any lint cleanup if desired.
