package io.github.idyq.service.login;

import io.github.idyq.dao.table.AdminDao;
import io.github.idyq.dao.table.impl.AdminDaoImpl;
import io.github.idyq.model.entity.Admin;
import io.github.idyq.util.PasswordUtil;
import io.github.idyq.util.SqliteJdbcUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class AdminAuthService {

    private final AdminDao adminDao = new AdminDaoImpl();

    /**
     * 管理员登录验证
     * @param username 用户名
     * @param password 密码字符数组（需前端保证非空）
     * @return 登录成功返回Admin对象，失败返回null
     */
    public Admin login(String username, char[] password) {
        Connection conn = null;
        try {
            conn = SqliteJdbcUtil.getConnection();
            Admin admin = adminDao.findByUsername(conn, username);

            if (admin == null) {
                return null; // 用户不存在
            }

            // 转换存储的哈希和盐
            byte[] storedHash = PasswordUtil.hexToBytes(admin.getPasswordHash());
            byte[] salt = PasswordUtil.hexToBytes(admin.getSalt());

            // 重新计算哈希
            byte[] computedHash = PasswordUtil.hashPassword(
                    password,
                    salt,
                    admin.getIterations(),
                    32 // 密钥长度需与注册时一致
            );

            // 安全比对
            if (PasswordUtil.slowEquals(computedHash, storedHash)) {
                return admin; // 验证成功
            }

            return null; // 密码错误

        } catch (SQLException e) {
            throw new AuthException("数据库访问失败", e);
        } catch (Exception e) {
            throw new AuthException("系统验证错误", e);
        } finally {
            closeConnection(conn);
            clearPassword(password);
        }
    }

    /**
     * 清理密码内存残留
     */
    private void clearPassword(char[] password) {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }

    /**
     * 安全关闭数据库连接
     */
    private void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("关闭连接时发生错误: " + e.getMessage());
        }
    }

    /**
     * 自定义认证异常
     */
    public static class AuthException extends RuntimeException {
        public AuthException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}