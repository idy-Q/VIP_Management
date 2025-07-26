package io.github.idyq.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteJdbcUtil {
    // SQLite 驱动类
    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String URL;
    private static final String USER = "";
    private static final String PASSWORD = "";

    static {
        try {
            // 获取当前运行的代码位置
            File codeLocation = new File(SqliteJdbcUtil.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());

            File projectRoot;

            // 如果路径中包含 "target/classes"，说明在IDEA运行
            if (codeLocation.getAbsolutePath().replace("\\", "/").contains("/target/classes")) {
                projectRoot = codeLocation.getParentFile().getParentFile(); // 回到项目根目录
            } else {
                // 打包后的JAR运行，取JAR所在目录
                projectRoot = codeLocation.getParentFile();
            }

            // 数据库路径固定为 项目根目录/data/MyDB.db
            File dbFile = new File(projectRoot, "data/MyDB.db");

            if (!dbFile.exists()) {
                throw new RuntimeException("数据库文件不存在: " + dbFile.getAbsolutePath());
            }

            URL = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            System.out.println("最终数据库路径：" + URL);
        } catch (Exception e) {
            throw new RuntimeException("找不到数据库文件", e);
        }

        try {
            Class.forName(DRIVER);
            System.out.println("SQLite JDBC 驱动加载成功");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("无法加载 SQLite JDBC 驱动", e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取 SQLite 连接失败", e);
        }
    }

    public static void close(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) rs.close();
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 测试连接
//    public static void main(String[] args) {
//        Connection conn = null;
//        try {
//            conn = getConnection();
//            if (conn != null) {
//                System.out.println("成功连接到 SQLite 数据库！");
//            }
//        } finally {
//            close(conn, null, null);
//        }
//    }
}

