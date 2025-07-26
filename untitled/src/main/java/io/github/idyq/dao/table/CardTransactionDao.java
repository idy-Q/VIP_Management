package io.github.idyq.dao.table;

import io.github.idyq.model.entity.CardTransaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CardTransactionDao {
    boolean insert(Connection conn, CardTransaction transaction) throws SQLException;
    boolean update(Connection conn, CardTransaction transaction) throws SQLException;
    boolean deleteById(Connection conn, int transactionID) throws SQLException;
    CardTransaction findById(Connection conn, int transactionID) throws SQLException;
    List<CardTransaction> findAll(Connection conn) throws SQLException;
}
