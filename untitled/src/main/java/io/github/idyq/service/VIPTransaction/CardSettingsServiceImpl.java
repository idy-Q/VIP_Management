package io.github.idyq.service.VIPTransaction;

import io.github.idyq.dao.table.CardSettingsDao;
import io.github.idyq.dao.table.impl.CardSettingsDaoImpl;
import io.github.idyq.model.entity.CardSettings;
import io.github.idyq.util.SqliteJdbcUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * CardSettingsService 的默认实现
 * 通过 SqliteJdbcUtil 获取连接，并调用 DAO 完成增删改查
 */
public class CardSettingsServiceImpl implements CardSettingsService {

    private final CardSettingsDao settingsDao = new CardSettingsDaoImpl();

    @Override
    public List<CardSettings> listAll() throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return settingsDao.findAll(conn);
        }
    }

    @Override
    public boolean insert(CardSettings settings) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            // 插入时 CreatedAt 会自动使用默认值（当前时间），因此无需手动设置
            return settingsDao.insert(conn, settings);
        }
    }

    @Override
    public boolean update(CardSettings settings) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return settingsDao.update(conn, settings);
        }
    }

    @Override
    public boolean deleteById(int settingID) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return settingsDao.deleteById(conn, settingID);
        }
    }
}
