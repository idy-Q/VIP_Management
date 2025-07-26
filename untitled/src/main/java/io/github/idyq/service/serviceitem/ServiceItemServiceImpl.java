package io.github.idyq.service.serviceitem;

import io.github.idyq.dao.table.ServiceItemDao;
import io.github.idyq.dao.table.impl.ServiceItemDaoImpl;
import io.github.idyq.model.entity.ServiceItem;
import io.github.idyq.util.SqliteJdbcUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * ServiceItemService 的实现类，通过 Dao 层操作数据库
 */
public class ServiceItemServiceImpl implements ServiceItemService {

    // 直接使用 DaoImpl，DaoImpl 内会组装 SQL 并执行
    private final ServiceItemDao itemDao = new ServiceItemDaoImpl();

    @Override
    public List<ServiceItem> listAll() throws SQLException {
        // 每次操作都从连接池/工具类获取一个连接，使用完自动关闭
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return itemDao.findAll(conn);
        }
    }

    @Override
    public ServiceItem findById(int serviceItemID) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return itemDao.findById(conn, serviceItemID);
        }
    }

    @Override
    public boolean create(ServiceItem item) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return itemDao.insert(conn, item);
        }
    }

    @Override
    public boolean update(ServiceItem item) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return itemDao.update(conn, item);
        }
    }

    @Override
    public boolean delete(int serviceItemID) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            return itemDao.deleteById(conn, serviceItemID);
        }
    }
}
