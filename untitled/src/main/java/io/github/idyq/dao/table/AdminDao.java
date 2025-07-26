package io.github.idyq.dao.table;

import io.github.idyq.model.entity.Admin;

import java.sql.Connection;
import java.sql.SQLException;

public interface AdminDao {
    // 插入管理员
    void insertAdmin(Connection conn, Admin admin) throws SQLException;

    // 根据用户名查询
    Admin findByUsername(Connection conn, String username) throws SQLException;

    // 其他方法（可选）
    // void updatePassword(Connection conn, String username, String newHash) throws SQLException;
    // void deleteAdmin(Connection conn, String username) throws SQLException;
}