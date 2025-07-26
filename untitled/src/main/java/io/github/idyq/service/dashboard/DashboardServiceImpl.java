package io.github.idyq.service.dashboard;

import io.github.idyq.dao.dashboard.DashboardDao;
import io.github.idyq.dao.dashboard.DashboardDaoImpl;
import io.github.idyq.util.SqliteJdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DashboardServiceImpl implements DashboardService {

    private final DashboardDao dashboardDao = new DashboardDaoImpl();

    @Override
    public int getTotalMembers() {
        try {
            return dashboardDao.countTotalMembers();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

//    // DashboardServiceImpl.java 把 catch 去掉，直接抛
//    @Override
//    public int getTotalMembers() throws SQLException {
//        return dashboardDao.countTotalMembers();
//    }


    @Override
    public int getNewMembersThisMonth() {
        try {
            return dashboardDao.countNewMembersThisMonth();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public double getActiveMemberRate() {
        try {
            return dashboardDao.calcActiveMemberRate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    public int getPendingAppointments() {
        try {
            return dashboardDao.countPendingAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getLowBalanceMembers() {
        try {
            return dashboardDao.countLowBalanceMembers();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<Date> getRecentDates() {
        try {
            return dashboardDao.listChartDays();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<Double> getRechargeData() {
        try {
            return dashboardDao.sumRechargePerDay();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<Double> getConsumeData() {
        try {
            return dashboardDao.sumConsumePerDay();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 返回本月每天的“充值总额”映射，key=日期(java.util.Date，只包含年月日)，value=当天充值总额。
     * 如果某天没有充值，则 map.put(那天, 0.0)。
     */
    @Override
    public Map<Date, Double> getRechargePerDayMap() throws SQLException {
        // 先拿“本月从1号到今天”的所有日期
        List<Date> dayList = dashboardDao.listChartDays();
        Map<Date, Double> map = new LinkedHashMap<>();
        for (Date d : dayList) {
            map.put(d, 0.0);
        }

        // 再从数据库里查询每天的充值总额
        String sql =
                "SELECT date(TransactionDate) AS dayStr, " +
                        "       SUM(CASE WHEN Type='Recharge' THEN Amount ELSE 0 END) AS val " +
                        "  FROM CardTransaction " +
                        " WHERE date(TransactionDate) BETWEEN date('now','start of month') AND date('now') " +
                        " GROUP BY dayStr " +
                        " ORDER BY dayStr";

        try (Connection conn = SqliteJdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String dayStr = rs.getString("dayStr");   // e.g. "2025-06-03"
                double val    = rs.getDouble("val");      // 当天充值总额

                // 把字符串转换为 java.util.Date
                java.sql.Date sqlDate = java.sql.Date.valueOf(dayStr);
                map.put(new Date(sqlDate.getTime()), val);
            }
        }
        return map;
    }

    /**
     * 返回本月每天的“消费总额”映射，key=日期(java.util.Date)，value=当天消费总额（正数）。
     * SQL 中 CASE WHEN Type='Consume' THEN Amount ELSE 0 END，所以取到的是正值。
     * 如果某天没有消费，则 map.put(那天, 0.0)。
     */
    @Override
    public Map<Date, Double> getConsumePerDayMap() throws SQLException {
        List<Date> dayList = dashboardDao.listChartDays();
        Map<Date, Double> map = new LinkedHashMap<>();
        for (Date d : dayList) {
            map.put(d, 0.0);
        }

        String sql =
                "SELECT date(TransactionDate) AS dayStr, " +
                        "       SUM(CASE WHEN Type='Consume' THEN Amount ELSE 0 END) AS val " +
                        "  FROM CardTransaction " +
                        " WHERE date(TransactionDate) BETWEEN date('now','start of month') AND date('now') " +
                        " GROUP BY dayStr " +
                        " ORDER BY dayStr";

        try (Connection conn = SqliteJdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String dayStr = rs.getString("dayStr");
                double val    = rs.getDouble("val");  // 当天消费总额（已经是正值）

                java.sql.Date sqlDate = java.sql.Date.valueOf(dayStr);
                map.put(new Date(sqlDate.getTime()), val);
            }
        }
        return map;
    }
}
