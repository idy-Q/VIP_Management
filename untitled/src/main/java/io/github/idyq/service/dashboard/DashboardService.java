package io.github.idyq.service.dashboard;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    int getTotalMembers();
    int getNewMembersThisMonth();
    double getActiveMemberRate();
    int getPendingAppointments();
    int getLowBalanceMembers();
    List<Date> getRecentDates(); // 用于折线图横坐标
    List<Double> getRechargeData(); // 折线图数据
    List<Double> getConsumeData();  // 折线图数据

    // 新增：返回“日→充值总额”映射
    Map<Date, Double> getRechargePerDayMap() throws SQLException;

    // 同理，消费映射
    Map<Date, Double> getConsumePerDayMap() throws SQLException;
}
