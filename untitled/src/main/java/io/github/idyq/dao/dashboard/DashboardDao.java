package io.github.idyq.dao.dashboard;

import java.util.Date;
import java.util.List;
import java.sql.SQLException;

/**
 * 仪表盘数据访问接口，提供首页所需的聚合数据方法
 */
public interface DashboardDao {
    int countTotalMembers() throws SQLException;
    int countNewMembersThisMonth() throws SQLException;
    double calcActiveMemberRate() throws SQLException;
    int countPendingAppointments() throws SQLException;
    int countLowBalanceMembers() throws SQLException;

    /**
     * 获取当月日期列表，从当月1号到当前日期
     */
    List<Date> listChartDays();

    /**
     * 获取当月每天的充值总额，与 listChartDays 返回的日期对应
     */
    List<Double> sumRechargePerDay() throws SQLException;

    /**
     * 获取当月每天的消费总额，与 listChartDays 返回的日期对应
     */
    List<Double> sumConsumePerDay() throws SQLException;
}