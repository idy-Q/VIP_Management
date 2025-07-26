package io.github.idyq.dao.table;

import io.github.idyq.model.entity.Appointment;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AppointmentDao {
    boolean insert(Connection conn, Appointment appointment) throws SQLException;
    boolean update(Connection conn, Appointment appointment) throws SQLException;
    boolean deleteById(Connection conn, int appointmentID) throws SQLException;
    Appointment findById(Connection conn, int appointmentID) throws SQLException;
    List<Appointment> findAll(Connection conn) throws SQLException;
}
