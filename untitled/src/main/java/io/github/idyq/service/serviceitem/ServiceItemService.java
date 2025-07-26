package io.github.idyq.service.serviceitem;

import io.github.idyq.model.entity.ServiceItem;

import java.sql.SQLException;
import java.util.List;

/**
 * 服务项目相关业务接口
 */
public interface ServiceItemService {
    /**
     * 查询所有服务项目
     * @return 如果没有任何项目，返回空的 List，不返回 null
     * @throws SQLException 数据库操作异常
     */
    List<ServiceItem> listAll() throws SQLException;

    /**
     * 根据 ID 查询单个服务项目
     * @param serviceItemID 项目 ID
     * @return 如果找不到，返回 null
     * @throws SQLException 数据库操作异常
     */
    ServiceItem findById(int serviceItemID) throws SQLException;

    /**
     * 新增一个服务项目
     * @param item 要新增的 ServiceItem 对象
     * @return 新增成功返回 true，否则 false
     * @throws SQLException 数据库操作异常
     */
    boolean create(ServiceItem item) throws SQLException;

    /**
     * 修改一个已有的服务项目
     * @param item 带有完整 ID 和修改后字段的 ServiceItem 对象
     * @return 修改成功返回 true，否则 false
     * @throws SQLException 数据库操作异常
     */
    boolean update(ServiceItem item) throws SQLException;

    /**
     * 根据 ID 删除一个服务项目
     * @param serviceItemID 要删除的项目 ID
     * @return 删除成功返回 true，否则 false
     * @throws SQLException 数据库操作异常
     */
    boolean delete(int serviceItemID) throws SQLException;
}
