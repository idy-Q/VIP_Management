package io.github.idyq.dao.table.impl;

import io.github.idyq.dao.table.MemberDao;
import io.github.idyq.model.entity.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDaoImpl implements MemberDao {

    @Override
    public boolean insert(Connection conn, Member member) throws SQLException {
        String sql = "INSERT INTO Member (Name, Gender, BirthDate, Phone, Email, JoinDate, LevelID, Points, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getGender());
            ps.setObject(3, member.getBirthDate());
            ps.setString(4, member.getPhone());
            ps.setString(5, member.getEmail());
            ps.setObject(6, member.getJoinDate());
            ps.setInt(7, member.getLevelID());
            ps.setInt(8, member.getPoints());
            ps.setString(9, member.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Connection conn, Member member) throws SQLException {
        String sql = "UPDATE Member SET Name = ?, Gender = ?, BirthDate = ?, Phone = ?, Email = ?, JoinDate = ?, LevelID = ?, Points = ?, Status = ? WHERE MemberID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getGender());
            ps.setObject(3, member.getBirthDate());
            ps.setString(4, member.getPhone());
            ps.setString(5, member.getEmail());
            ps.setObject(6, member.getJoinDate());
            ps.setInt(7, member.getLevelID());
            ps.setInt(8, member.getPoints());
            ps.setString(9, member.getStatus());
            ps.setInt(10, member.getMemberID());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Connection conn, int memberID) throws SQLException {
        String sql = "DELETE FROM Member WHERE MemberID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberID);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Member findById(Connection conn, int memberID) throws SQLException {
        String sql = "SELECT * FROM Member WHERE MemberID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member m = new Member();
                    m.setMemberID(rs.getInt("MemberID"));
                    m.setName(rs.getString("Name"));
                    m.setGender(rs.getString("Gender"));
                    m.setBirthDate(rs.getObject("BirthDate", java.time.LocalDate.class));
                    m.setPhone(rs.getString("Phone"));
                    m.setEmail(rs.getString("Email"));
                    m.setJoinDate(rs.getObject("JoinDate", java.time.LocalDate.class));
                    m.setLevelID(rs.getInt("LevelID"));
                    m.setPoints(rs.getInt("Points"));
                    m.setStatus(rs.getString("Status"));
                    return m;
                }
                return null;
            }
        }
    }

    @Override
    public List<Member> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM Member";
        List<Member> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Member m = new Member();
                m.setMemberID(rs.getInt("MemberID"));
                m.setName(rs.getString("Name"));
                m.setGender(rs.getString("Gender"));
                m.setBirthDate(rs.getObject("BirthDate", java.time.LocalDate.class));
                m.setPhone(rs.getString("Phone"));
                m.setEmail(rs.getString("Email"));
                m.setJoinDate(rs.getObject("JoinDate", java.time.LocalDate.class));
                m.setLevelID(rs.getInt("LevelID"));
                m.setPoints(rs.getInt("Points"));
                m.setStatus(rs.getString("Status"));
                list.add(m);
            }
        }
        return list;
    }
}
