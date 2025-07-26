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

public class AdminService {
    private AdminDao adminDao = new AdminDaoImpl();


    private static final int ITERATIONS      = 65536;
    private static final int KEY_LENGTH_BYTES = 32;  // 32 bytes = 256 bits

    // 注册方法（移除 throws Exception）
    public void registerAdmin(String username, char[] password) {
        Connection conn = null;
        try {
            conn = SqliteJdbcUtil.getConnection();
            conn.setAutoCommit(false);

            // 输入验证
            validateInput(username, password);

            // 检查用户名是否已存在
            if (adminDao.findByUsername(conn, username) != null) {
                throw new IllegalArgumentException("用户名已存在");
            }

            // 生成盐和哈希
            byte[] salt = PasswordUtil.generateSalt();
            byte[] hash = PasswordUtil.hashPassword(password, salt, ITERATIONS, KEY_LENGTH_BYTES);

            // 转换为存储格式
            String saltHex = PasswordUtil.bytesToHex(salt);
            String hashHex = PasswordUtil.bytesToHex(hash);

            // 创建Admin对象
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPasswordHash(hashHex);
            admin.setSalt(saltHex);
            admin.setIterations(ITERATIONS);
            admin.setAlgorithm("PBKDF2WithHmacSHA256");
            admin.setCreatedAt(LocalDateTime.now());

            // 插入数据库
            adminDao.insertAdmin(conn, admin);
            conn.commit();

        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            rollback(conn);
            throw new RuntimeException("注册失败: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            rollback(conn);
            throw e; // 直接传递业务异常
        } finally {
            closeConnection(conn);
            Arrays.fill(password, '\0');
        }
    }

    // 登录验证（移除 throws Exception）
    public boolean login(String username, char[] password) {
        Connection conn = null;
        try {
            conn = SqliteJdbcUtil.getConnection();
            Admin admin = adminDao.findByUsername(conn, username);

            if (admin == null) return false;

            // 从数据库读取参数
            byte[] salt = PasswordUtil.hexToBytes(admin.getSalt());

            // 重新计算哈希
            // 注意：这里取的也是同一个迭代次数和输出长度
            byte[] testHash = PasswordUtil.hashPassword(password, salt, ITERATIONS, KEY_LENGTH_BYTES);
            byte[] storedHash = PasswordUtil.hexToBytes(admin.getPasswordHash());

            return PasswordUtil.slowEquals(testHash, storedHash);

        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("登录失败: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
            Arrays.fill(password, '\0');
        }
    }

    // 输入验证
    private void validateInput(String username, char[] password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.length < 8) {
            throw new IllegalArgumentException("密码长度至少8位");
        }
    }

    // 回滚事务
    private void rollback(Connection conn) {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException ex) {
            System.err.println("回滚失败: " + ex.getMessage());
        }
    }

    // 关闭连接
    private void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("关闭连接失败: " + e.getMessage());
        }
    }
}