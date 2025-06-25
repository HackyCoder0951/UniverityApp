package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Department;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for retrieving Department information.
 * This class was primarily used to populate department selection dropdowns in the UI.
 * Note: While still functional, its role in adding/updating data has been largely
 * superseded by the dynamic GenericDAO.
 */
public class DepartmentDAO {

    /**
     * Fetches a list of all departments from the database.
     *
     * @return A list of Department objects, typically used to populate a JComboBox.
     */
    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM department";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                departments.add(new Department(
                        rs.getString("dept_name"),
                        rs.getString("building"),
                        rs.getDouble("budget")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }
} 