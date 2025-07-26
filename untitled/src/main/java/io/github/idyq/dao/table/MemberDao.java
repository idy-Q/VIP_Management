package io.github.idyq.dao.table;

import io.github.idyq.model.entity.Member;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface MemberDao {
    boolean insert(Connection conn, Member member) throws SQLException;
    boolean update(Connection conn, Member member) throws SQLException;
    boolean deleteById(Connection conn, int memberID) throws SQLException;
    Member findById(Connection conn, int memberID) throws SQLException;
    List<Member> findAll(Connection conn) throws SQLException;
}
