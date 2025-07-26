package io.github.idyq.service.login;

import io.github.idyq.dao.table.AdminDao;
import io.github.idyq.dao.table.impl.AdminDaoImpl;
import io.github.idyq.model.entity.Admin;
import io.github.idyq.util.PasswordUtil;
import io.github.idyq.util.SqliteJdbcUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class AdminRegistrationService {

    private final AdminDao adminDao = new AdminDaoImpl();

    /**
     * 注册新管理员
     * @param username 用户名（需唯一）
     * @param password 密码字符数组
     * @throws RegistrationException 包含失败原因
     */
    public void registerAdmin(String username, char[] password) throws RegistrationException {
        validateInput(username, password);

        Connection conn = null;
        try {
            conn = SqliteJdbcUtil.getConnection();
            conn.setAutoCommit(false);

            // 检查用户名是否存在
            if (adminDao.findByUsername(conn, username) != null) {
                throw new RegistrationException("用户名已存在");
            }

            // 生成安全凭证（此处可能抛出加密异常）
            Admin admin = generateAdminCredentials(username, password);

            // 插入数据库
            adminDao.insertAdmin(conn, admin);
            conn.commit();

        } catch (SQLException e) {
            rollbackTransaction(conn);
            throw new RegistrationException("数据库操作失败: " + e.getMessage(), e);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            rollbackTransaction(conn);
            throw new RegistrationException("密码加密失败: " + e.getMessage(), e);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw new RegistrationException("系统错误: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
            clearPassword(password);
        }
    }

    // 生成安全凭证（添加异常声明）
    private Admin generateAdminCredentials(String username, char[] password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = PasswordUtil.generateSalt();
        int iterations = 10000;
        int keyLength = 32; // 256位

        byte[] hash = PasswordUtil.hashPassword(password, salt, iterations, keyLength);

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPasswordHash(PasswordUtil.bytesToHex(hash));
        admin.setSalt(PasswordUtil.bytesToHex(salt));
        admin.setAlgorithm("PBKDF2WithHmacSHA256");
        admin.setIterations(iterations);
        admin.setCreatedAt(LocalDateTime.now());
        return admin;
    }

    // 输入验证（保持不变）
    private void validateInput(String username, char[] password) throws RegistrationException {
        if (username == null || username.trim().isEmpty()) {
            throw new RegistrationException("用户名不能为空");
        }
        if (password == null || password.length < 8) {
            throw new RegistrationException("密码长度至少8位");
        }
        if (username.length() > 50) {
            throw new RegistrationException("用户名最长50字符");
        }
    }

    // 以下工具方法保持不变
    private void clearPassword(char[] password) {
        Arrays.fill(password, '\0');
    }

    private void rollbackTransaction(Connection conn) {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException e) {
            System.err.println("回滚事务失败: " + e.getMessage());
        }
    }

    private void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("关闭连接失败: " + e.getMessage());
        }
    }

    /**
     * 自定义注册异常
     */
    public static class RegistrationException extends Exception {
        public RegistrationException(String message) {
            super(message);
        }

        public RegistrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}