package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Department;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

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