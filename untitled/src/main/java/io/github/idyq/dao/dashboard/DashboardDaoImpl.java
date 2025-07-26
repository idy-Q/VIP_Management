package io.github.idyq.dao.dashboard;

import io.github.idyq.util.SqliteJdbcUtil;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * DashboardDao 实现类，使用 SqliteJdbcUtil 获取连接并执行 SQL
 */
public class DashboardDaoImpl implements DashboardDao {
    private final Connection conn;

    public DashboardDaoImpl() {
        this.conn = SqliteJdbcUtil.getConnection();
    }

    @Override
    public int countTotalMembers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Member WHERE Status='Active'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

//    @Override
//    public int countTotalMembers() throws SQLException {
//        String sql = "SELECT COUNT(*) FROM Member WHERE Status='Active'";
//        System.out.println("Executing SQL: " + sql);
//        try (PreparedStatement ps = conn.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            int c = rs.next() ? rs.getInt(1) : -1;
//            System.out.println("Query result: " + c);
//            return c;
//        }
//    }


    @Override
    public int countNewMembersThisMonth() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Member WHERE JoinDate BETWEEN date('now','start of month') AND date('now')";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public double calcActiveMemberRate() throws SQLException {
        String sql = "SELECT 1.0 * SUM(CASE WHEN Status='Active' THEN 1 ELSE 0 END)/COUNT(*) FROM Member";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) * 100 : 0.0;
        }
    }

    @Override
    public int countPendingAppointments() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Appointment WHERE Status='Scheduled'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public int countLowBalanceMembers() throws SQLException {
        String sql =
                "SELECT COUNT(DISTINCT MemberID) FROM (" +
                        "  SELECT MemberID, BalanceAfter, ROW_NUMBER() OVER(PARTITION BY MemberID ORDER BY TransactionDate DESC) rn " +
                        "  FROM CardTransaction" +
                        ") t WHERE t.rn=1 AND t.BalanceAfter < 10";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public List<Date> listChartDays() {
        List<Date> days = new ArrayList<>();

        // 1. 先取当前日期的 Calendar 实例
        Calendar cal = Calendar.getInstance();

        // 2. 设置为当月 1 号，并把时分秒毫秒都清零
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // 3. 把“今天”也只保留年月日（清掉时分秒）
        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);
        Date end = todayCal.getTime();

        // 4. 从当月 1 号开始，每次加一天，一直到“今天午夜”结束
        while (!cal.getTime().after(end)) {
            days.add(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }

    @Override
    public List<Double> sumRechargePerDay() throws SQLException {
        // 注意这里 WHERE 子句里用 date(TransactionDate)
        String sql =
                "SELECT date(TransactionDate) AS day, \n" +
                        "       SUM(CASE WHEN Type='Recharge' THEN Amount ELSE 0 END) AS val\n" +
                        "  FROM CardTransaction\n" +
                        " WHERE date(TransactionDate) BETWEEN date('now','start of month') AND date('now')\n" +
                        " GROUP BY day\n" +
                        " ORDER BY day";

        List<Double> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getDouble("val"));
            }
        }
        return list;
    }

    @Override
    public List<Double> sumConsumePerDay() throws SQLException {
        // 同样用 date(TransactionDate) 做条件
        String sql =
                "SELECT date(TransactionDate) AS day, \n" +
                        "       SUM(CASE WHEN Type='Consume' THEN -Amount ELSE 0 END) AS val\n" +
                        "  FROM CardTransaction\n" +
                        " WHERE date(TransactionDate) BETWEEN date('now','start of month') AND date('now')\n" +
                        " GROUP BY day\n" +
                        " ORDER BY day";

        List<Double> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getDouble("val"));
            }
        }
        return list;
    }
}
