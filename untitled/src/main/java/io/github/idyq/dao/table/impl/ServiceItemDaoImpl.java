package io.github.idyq.dao.table.impl;

import io.github.idyq.dao.table.ServiceItemDao;
import io.github.idyq.model.entity.ServiceItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceItemDaoImpl implements ServiceItemDao {

    @Override
    public boolean insert(Connection conn, ServiceItem item) throws SQLException {
        String sql = "INSERT INTO ServiceItem (Name, Description, Price, Duration, CreatedAt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getPrice());
            ps.setInt(4, item.getDuration());
            ps.setObject(5, item.getCreatedAt());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Connection conn, ServiceItem item) throws SQLException {
        String sql = "UPDATE ServiceItem SET Name = ?, Description = ?, Price = ?, Duration = ?, CreatedAt = ? WHERE ServiceItemID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getPrice());
            ps.setInt(4, item.getDuration());
            ps.setObject(5, item.getCreatedAt());
            ps.setInt(6, item.getServiceItemID());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Connection conn, int serviceItemID) throws SQLException {
        String sql = "DELETE FROM ServiceItem WHERE ServiceItemID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceItemID);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public ServiceItem findById(Connection conn, int serviceItemID) throws SQLException {
        String sql = "SELECT * FROM ServiceItem WHERE ServiceItemID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceItemID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ServiceItem item = new ServiceItem();
                    item.setServiceItemID(rs.getInt("ServiceItemID"));
                    item.setName(rs.getString("Name"));
                    item.setDescription(rs.getString("Description"));
                    item.setPrice(rs.getDouble("Price"));
                    item.setDuration(rs.getInt("Duration"));
                    item.setCreatedAt(rs.getObject("CreatedAt", java.time.LocalDateTime.class));
                    return item;
                }
                return null;
            }
        }
    }

    @Override
    public List<ServiceItem> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM ServiceItem";
        List<ServiceItem> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ServiceItem item = new ServiceItem();
                item.setServiceItemID(rs.getInt("ServiceItemID"));
                item.setName(rs.getString("Name"));
                item.setDescription(rs.getString("Description"));
                item.setPrice(rs.getDouble("Price"));
                item.setDuration(rs.getInt("Duration"));
                item.setCreatedAt(rs.getObject("CreatedAt", java.time.LocalDateTime.class));
                list.add(item);
            }
        }
        return list;
    }
}
