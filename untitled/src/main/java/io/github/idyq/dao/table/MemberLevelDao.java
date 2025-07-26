package io.github.idyq.dao.table;

import io.github.idyq.model.entity.MemberLevel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface MemberLevelDao {
    boolean insert(Connection conn, MemberLevel level) throws SQLException;
    boolean update(Connection conn, MemberLevel level) throws SQLException;
    boolean deleteById(Connection conn, int levelID) throws SQLException;
    MemberLevel findById(Connection conn, int levelID) throws SQLException;
    List<MemberLevel> findAll(Connection conn) throws SQLException;
}
