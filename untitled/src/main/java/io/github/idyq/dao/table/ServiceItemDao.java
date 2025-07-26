package io.github.idyq.dao.table;

import io.github.idyq.model.entity.ServiceItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ServiceItemDao {
    boolean insert(Connection conn, ServiceItem item) throws SQLException;
    boolean update(Connection conn, ServiceItem item) throws SQLException;
    boolean deleteById(Connection conn, int serviceItemID) throws SQLException;
    ServiceItem findById(Connection conn, int serviceItemID) throws SQLException;
    List<ServiceItem> findAll(Connection conn) throws SQLException;
}
