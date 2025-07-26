package io.github.idyq.dao.table;

import io.github.idyq.model.entity.CardSettings;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CardSettingsDao {
    boolean insert(Connection conn, CardSettings settings) throws SQLException;
    boolean update(Connection conn, CardSettings settings) throws SQLException;
    boolean deleteById(Connection conn, int settingID) throws SQLException;
    CardSettings findById(Connection conn, int settingID) throws SQLException;
    List<CardSettings> findAll(Connection conn) throws SQLException;
}
