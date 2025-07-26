package io.github.idyq.dao.table.impl;

import io.github.idyq.dao.table.AppointmentDao;
import io.github.idyq.model.entity.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDaoImpl implements AppointmentDao {

    @Override
    public boolean insert(Connection conn, Appointment appointment) throws SQLException {
        String sql = "INSERT INTO Appointment (MemberID, ServiceItemID, StaffID, AppointmentDate, Status, CreatedAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (appointment.getMemberID() != null) {
                ps.setInt(1, appointment.getMemberID());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setInt(2, appointment.getServiceItemID());
            if (appointment.getStaffID() != null) {
                ps.setInt(3, appointment.getStaffID());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setObject(4, appointment.getAppointmentDate());
            ps.setString(5, appointment.getStatus());
            ps.setObject(6, appointment.getCreatedAt());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Connection conn, Appointment appointment) throws SQLException {
        String sql = "UPDATE Appointment SET MemberID = ?, ServiceItemID = ?, StaffID = ?, AppointmentDate = ?, Status = ?, CreatedAt = ? WHERE AppointmentID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (appointment.getMemberID() != null) {
                ps.setInt(1, appointment.getMemberID());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setInt(2, appointment.getServiceItemID());
            if (appointment.getStaffID() != null) {
                ps.setInt(3, appointment.getStaffID());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setObject(4, appointment.getAppointmentDate());
            ps.setString(5, appointment.getStatus());
            ps.setObject(6, appointment.getCreatedAt());
            ps.setInt(7, appointment.getAppointmentID());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Connection conn, int appointmentID) throws SQLException {
        String sql = "DELETE FROM Appointment WHERE AppointmentID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentID);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Appointment findById(Connection conn, int appointmentID) throws SQLException {
        String sql = "SELECT * FROM Appointment WHERE AppointmentID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Appointment ap = new Appointment();
                    ap.setAppointmentID(rs.getInt("AppointmentID"));
                    int memId = rs.getInt("MemberID");
                    if (!rs.wasNull()) ap.setMemberID(memId);
                    ap.setServiceItemID(rs.getInt("ServiceItemID"));
                    int staffId = rs.getInt("StaffID");
                    if (!rs.wasNull()) ap.setStaffID(staffId);
                    ap.setAppointmentDate(rs.getObject("AppointmentDate", java.time.LocalDateTime.class));
                    ap.setStatus(rs.getString("Status"));
                    ap.setCreatedAt(rs.getObject("CreatedAt", java.time.LocalDateTime.class));
                    return ap;
                }
                return null;
            }
        }
    }

    @Override
    public List<Appointment> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM Appointment";
        List<Appointment> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Appointment ap = new Appointment();
                ap.setAppointmentID(rs.getInt("AppointmentID"));
                int memId = rs.getInt("MemberID");
                if (!rs.wasNull()) ap.setMemberID(memId);
                ap.setServiceItemID(rs.getInt("ServiceItemID"));
                int staffId = rs.getInt("StaffID");
                if (!rs.wasNull()) ap.setStaffID(staffId);
                ap.setAppointmentDate(rs.getObject("AppointmentDate", java.time.LocalDateTime.class));
                ap.setStatus(rs.getString("Status"));
                ap.setCreatedAt(rs.getObject("CreatedAt", java.time.LocalDateTime.class));
                list.add(ap);
            }
        }
        return list;
    }
}
