package io.github.idyq.dao.table.impl;

import io.github.idyq.dao.table.CardSettingsDao;
import io.github.idyq.model.entity.CardSettings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardSettingsDaoImpl implements CardSettingsDao {

    @Override
    public boolean insert(Connection conn, CardSettings settings) throws SQLException {
        String sql = "INSERT INTO CardSettings (PlanName, ChargeAmount, BonusAmount, EffectivePeriod, CreatedAt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, settings.getPlanName());
            ps.setDouble(2, settings.getChargeAmount());
            ps.setDouble(3, settings.getBonusAmount());
            if (settings.getEffectivePeriod() != null) {
                ps.setInt(4, settings.getEffectivePeriod());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setObject(5, settings.getCreatedAt());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Connection conn, CardSettings settings) throws SQLException {
        String sql = "UPDATE CardSettings SET PlanName = ?, ChargeAmount = ?, BonusAmount = ?, EffectivePeriod = ?, CreatedAt = ? WHERE SettingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, settings.getPlanName());
            ps.setDouble(2, settings.getChargeAmount());
            ps.setDouble(3, settings.getBonusAmount());
            if (settings.getEffectivePeriod() != null) {
                ps.setInt(4, settings.getEffectivePeriod());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setObject(5, settings.getCreatedAt());
            ps.setInt(6, settings.getSettingID());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Connection conn, int settingID) throws SQLException {
        String sql = "DELETE FROM CardSettings WHERE SettingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, settingID);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public CardSettings findById(Connection conn, int settingID) throws SQLException {
        String sql = "SELECT * FROM CardSettings WHERE SettingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, settingID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CardSettings cs = new CardSettings();
                    cs.setSettingID(rs.getInt("SettingID"));
                    cs.setPlanName(rs.getString("PlanName"));
                    cs.setChargeAmount(rs.getDouble("ChargeAmount"));
                    cs.setBonusAmount(rs.getDouble("BonusAmount"));
                    int period = rs.getInt("EffectivePeriod");
                    if (!rs.wasNull()) cs.setEffectivePeriod(period);
                    cs.setCreatedAt(rs.getObject("CreatedAt", java.time.LocalDateTime.class));
                    return cs;
                }
                return null;
            }
        }
    }

    @Override
    public List<CardSettings> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM CardSettings";
        List<CardSettings> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CardSettings cs = new CardSettings();
                cs.setSettingID(rs.getInt("SettingID"));
                cs.setPlanName(rs.getString("PlanName"));
                cs.setChargeAmount(rs.getDouble("ChargeAmount"));
                cs.setBonusAmount(rs.getDouble("BonusAmount"));
                int period = rs.getInt("EffectivePeriod");
                if (!rs.wasNull()) cs.setEffectivePeriod(period);
                cs.setCreatedAt(rs.getObject("CreatedAt", java.time.LocalDateTime.class));
                list.add(cs);
            }
        }
        return list;
    }
}
