package io.github.idyq.dao.table.impl;

import io.github.idyq.dao.table.CardTransactionDao;
import io.github.idyq.model.entity.CardTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardTransactionDaoImpl implements CardTransactionDao {

    @Override
    public boolean insert(Connection conn, CardTransaction transaction) throws SQLException {
        String sql = "INSERT INTO CardTransaction (MemberID, Type, Amount, BalanceAfter, TransactionDate, Notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transaction.getMemberID());
            ps.setString(2, transaction.getType());
            ps.setDouble(3, transaction.getAmount());
            ps.setDouble(4, transaction.getBalanceAfter());
            ps.setObject(5, transaction.getTransactionDate());
            ps.setString(6, transaction.getNotes());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Connection conn, CardTransaction transaction) throws SQLException {
        String sql = "UPDATE CardTransaction SET MemberID = ?, Type = ?, Amount = ?, BalanceAfter = ?, TransactionDate = ?, Notes = ? WHERE TransactionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transaction.getMemberID());
            ps.setString(2, transaction.getType());
            ps.setDouble(3, transaction.getAmount());
            ps.setDouble(4, transaction.getBalanceAfter());
            ps.setObject(5, transaction.getTransactionDate());
            ps.setString(6, transaction.getNotes());
            ps.setInt(7, transaction.getTransactionID());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Connection conn, int transactionID) throws SQLException {
        String sql = "DELETE FROM CardTransaction WHERE TransactionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionID);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public CardTransaction findById(Connection conn, int transactionID) throws SQLException {
        String sql = "SELECT * FROM CardTransaction WHERE TransactionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CardTransaction ct = new CardTransaction();
                    ct.setTransactionID(rs.getInt("TransactionID"));
                    ct.setMemberID(rs.getInt("MemberID"));
                    ct.setType(rs.getString("Type"));
                    ct.setAmount(rs.getDouble("Amount"));
                    ct.setBalanceAfter(rs.getDouble("BalanceAfter"));
                    ct.setTransactionDate(rs.getObject("TransactionDate", java.time.LocalDateTime.class));
                    ct.setNotes(rs.getString("Notes"));
                    return ct;
                }
                return null;
            }
        }
    }

    @Override
    public List<CardTransaction> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM CardTransaction";
        List<CardTransaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CardTransaction ct = new CardTransaction();
                ct.setTransactionID(rs.getInt("TransactionID"));
                ct.setMemberID(rs.getInt("MemberID"));
                ct.setType(rs.getString("Type"));
                ct.setAmount(rs.getDouble("Amount"));
                ct.setBalanceAfter(rs.getDouble("BalanceAfter"));
                ct.setTransactionDate(rs.getObject("TransactionDate", java.time.LocalDateTime.class));
                ct.setNotes(rs.getString("Notes"));
                list.add(ct);
            }
        }
        return list;
    }
}
