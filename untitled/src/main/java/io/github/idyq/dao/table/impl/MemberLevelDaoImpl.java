package io.github.idyq.dao.table.impl;

import io.github.idyq.dao.table.MemberLevelDao;
import io.github.idyq.model.entity.MemberLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberLevelDaoImpl implements MemberLevelDao {

    @Override
    public boolean insert(Connection conn, MemberLevel level) throws SQLException {
        String sql = "INSERT INTO MemberLevel (LevelName, MinPoints, DiscountRate, Benefits) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, level.getLevelName());
            ps.setInt(2, level.getMinPoints());
            ps.setDouble(3, level.getDiscountRate());
            ps.setString(4, level.getBenefits());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Connection conn, MemberLevel level) throws SQLException {
        String sql = "UPDATE MemberLevel SET LevelName = ?, MinPoints = ?, DiscountRate = ?, Benefits = ? WHERE LevelID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, level.getLevelName());
            ps.setInt(2, level.getMinPoints());
            ps.setDouble(3, level.getDiscountRate());
            ps.setString(4, level.getBenefits());
            ps.setInt(5, level.getLevelID());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Connection conn, int levelID) throws SQLException {
        String sql = "DELETE FROM MemberLevel WHERE LevelID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, levelID);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public MemberLevel findById(Connection conn, int levelID) throws SQLException {
        String sql = "SELECT * FROM MemberLevel WHERE LevelID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, levelID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MemberLevel level = new MemberLevel();
                    level.setLevelID(rs.getInt("LevelID"));
                    level.setLevelName(rs.getString("LevelName"));
                    level.setMinPoints(rs.getInt("MinPoints"));
                    level.setDiscountRate(rs.getDouble("DiscountRate"));
                    level.setBenefits(rs.getString("Benefits"));
                    return level;
                }
                return null;
            }
        }
    }

    @Override
    public List<MemberLevel> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM MemberLevel";
        List<MemberLevel> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MemberLevel level = new MemberLevel();
                level.setLevelID(rs.getInt("LevelID"));
                level.setLevelName(rs.getString("LevelName"));
                level.setMinPoints(rs.getInt("MinPoints"));
                level.setDiscountRate(rs.getDouble("DiscountRate"));
                level.setBenefits(rs.getString("Benefits"));
                list.add(level);
            }
        }
        return list;
    }
}
