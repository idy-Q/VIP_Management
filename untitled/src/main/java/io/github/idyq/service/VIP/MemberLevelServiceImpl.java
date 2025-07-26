package io.github.idyq.service.VIP;

import io.github.idyq.dao.table.MemberLevelDao;
import io.github.idyq.dao.table.impl.MemberLevelDaoImpl;
import io.github.idyq.model.entity.MemberLevel;
import io.github.idyq.util.SqliteJdbcUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 会员等级业务实现
 */
public class MemberLevelServiceImpl implements MemberLevelService {
    private final MemberLevelDao levelDao = new MemberLevelDaoImpl();

    @Override
    public List<MemberLevel> listAll() throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return levelDao.findAll(conn);
        }
    }

    @Override
    public MemberLevel findById(int levelID) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return levelDao.findById(conn, levelID);
        }
    }

    @Override
    public boolean create(MemberLevel level) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return levelDao.insert(conn, level);
        }
    }

    @Override
    public boolean update(MemberLevel level) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return levelDao.update(conn, level);
        }
    }

    @Override
    public boolean delete(int levelID) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return levelDao.deleteById(conn, levelID);
        }
    }
}
