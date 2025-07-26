package io.github.idyq.service.AppointmentManage;

import io.github.idyq.dao.table.AppointmentDao;
import io.github.idyq.dao.table.impl.AppointmentDaoImpl;
import io.github.idyq.model.entity.Appointment;
import io.github.idyq.util.SqliteJdbcUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentService 的默认实现
 */
public class AppointmentServiceImpl implements AppointmentService {
    // 持有一个 DAO 对象
    private final AppointmentDao appointmentDao = new AppointmentDaoImpl();

    @Override
    public List<Appointment> listAll() throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return appointmentDao.findAll(conn);
        }
    }

    @Override
    public Appointment findById(int appointmentID) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return appointmentDao.findById(conn, appointmentID);
        }
    }

    @Override
    public boolean create(Appointment appointment) throws SQLException {
        // 在插入之前，通常需要给 createdAt 赋值为当前时间
        appointment.setCreatedAt(java.time.LocalDateTime.now());
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return appointmentDao.insert(conn, appointment);
        }
    }

    @Override
    public boolean update(Appointment appointment) throws SQLException {
        // 如果允许修改 createdAt，则调用者需事先给 appointment.setCreatedAt(...) 赋值；
        // 如果不允许修改创建时间，此处可以不更新该字段，或保持原值。
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return appointmentDao.update(conn, appointment);
        }
    }

    @Override
    public boolean delete(int appointmentID) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return appointmentDao.deleteById(conn, appointmentID);
        }
    }

    @Override
    public List<Appointment> search(String keyword) throws SQLException {
        // 简单示例：如果关键字能解析为整数，就筛选出 appointmentID、memberID、serviceItemID 或 staffID 匹配该整数的记录
        // 如果解析不出整数，直接返回所有记录（也可以根据需求改为返回空列表或按字符串字段做模糊匹配）
        List<Appointment> all;
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            all = appointmentDao.findAll(conn);
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return all;
        }
        String kw = keyword.trim();
        Integer ival = null;
        try {
            ival = Integer.parseInt(kw);
        } catch (NumberFormatException e) {
            // 无法解析为整数，示例直接返回全部，或可返回 new ArrayList<>()
            return all;
        }

        // 过滤出与 ival 相等的记录
        List<Appointment> filtered = new ArrayList<>();
        for (Appointment ap : all) {
            if (ap.getAppointmentID() == ival
                    || (ap.getMemberID() != null && ap.getMemberID().equals(ival))
                    || ap.getServiceItemID() == ival
                    || (ap.getStaffID() != null && ap.getStaffID().equals(ival))) {
                filtered.add(ap);
            }
        }
        return filtered;
    }
}
