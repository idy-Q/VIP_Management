package io.github.idyq.dao.table.impl;

import io.github.idyq.dao.table.AdminDao;
import io.github.idyq.model.entity.Admin;
import java.sql.*;

public class AdminDaoImpl implements AdminDao {

    @Override
    public void insertAdmin(Connection conn, Admin admin) throws SQLException {
        String sql = "INSERT INTO Admin (username, password_hash, salt, algorithm, iterations, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getPasswordHash());
            pstmt.setString(3, admin.getSalt());
            pstmt.setString(4, admin.getAlgorithm());
            pstmt.setInt(5, admin.getIterations());
            pstmt.setTimestamp(6, Timestamp.valueOf(admin.getCreatedAt()));

            pstmt.executeUpdate();
        }
    }

    @Override
    public Admin findByUsername(Connection conn, String username) throws SQLException {
        String sql = "SELECT admin_id, username, password_hash, salt, algorithm, iterations, created_at "
                + "FROM Admin WHERE username = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setPasswordHash(rs.getString("password_hash"));
                admin.setSalt(rs.getString("salt"));
                admin.setAlgorithm(rs.getString("algorithm"));
                admin.setIterations(rs.getInt("iterations"));
                admin.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return admin;
            }

            return null;
        }
    }
}