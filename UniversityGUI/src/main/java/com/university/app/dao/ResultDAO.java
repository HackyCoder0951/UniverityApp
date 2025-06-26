package com.university.app.dao;

import com.university.app.model.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {
    private Connection conn;

    public ResultDAO() {
        this.conn = DatabaseConnector.getConnection();
    }

    public void addResult(Result result) throws SQLException {
        String sql = "INSERT INTO result (student_id, semester, year, sgpa, cgpa, total_credits, result_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, result.getStudentId());
            stmt.setString(2, result.getSemester());
            stmt.setInt(3, result.getYear());
            stmt.setObject(4, result.getSgpa());
            stmt.setObject(5, result.getCgpa());
            stmt.setObject(6, result.getTotalCredits());
            stmt.setString(7, result.getResultType());
            stmt.executeUpdate();
        }
    }

    public void updateResult(Result result) throws SQLException {
        String sql = "UPDATE result SET sgpa=?, cgpa=?, total_credits=?, result_type=?, updated_at=NOW() WHERE result_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, result.getSgpa());
            stmt.setObject(2, result.getCgpa());
            stmt.setObject(3, result.getTotalCredits());
            stmt.setString(4, result.getResultType());
            stmt.setInt(5, result.getResultId());
            stmt.executeUpdate();
        }
    }

    public void deleteResult(int resultId) throws SQLException {
        String sql = "DELETE FROM result WHERE result_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resultId);
            stmt.executeUpdate();
        }
    }

    public Result getResultById(int resultId) throws SQLException {
        String sql = "SELECT * FROM result WHERE result_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resultId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractResult(rs);
                }
            }
        }
        return null;
    }

    public List<Result> getResultsForStudent(String studentId) throws SQLException {
        String sql = "SELECT * FROM result WHERE student_id=? ORDER BY year DESC, semester DESC";
        List<Result> resultList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultList.add(extractResult(rs));
                }
            }
        }
        return resultList;
    }

    public List<Result> getResultsByType(String studentId, String resultType) throws SQLException {
        String sql = "SELECT * FROM result WHERE student_id=? AND result_type=? ORDER BY year DESC, semester DESC";
        List<Result> resultList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, resultType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultList.add(extractResult(rs));
                }
            }
        }
        return resultList;
    }

    private Result extractResult(ResultSet rs) throws SQLException {
        Result result = new Result();
        result.setResultId(rs.getInt("result_id"));
        result.setStudentId(rs.getString("student_id"));
        result.setSemester(rs.getString("semester"));
        result.setYear(rs.getInt("year"));
        result.setSgpa((Double)rs.getObject("sgpa"));
        result.setCgpa((Double)rs.getObject("cgpa"));
        result.setTotalCredits((Integer)rs.getObject("total_credits"));
        result.setResultType(rs.getString("result_type"));
        result.setCreatedAt(rs.getTimestamp("created_at"));
        result.setUpdatedAt(rs.getTimestamp("updated_at"));
        return result;
    }
} 