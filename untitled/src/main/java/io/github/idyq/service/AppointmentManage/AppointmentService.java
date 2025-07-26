package io.github.idyq.service.AppointmentManage;

import io.github.idyq.model.entity.Appointment;

import java.sql.SQLException;
import java.util.List;

/**
 * 预约相关业务接口
 */
public interface AppointmentService {
    /**
     * 查询所有预约（不带任何过滤条件）
     * @return 可能为空的 List，但不返回 null
     * @throws SQLException
     */
    List<Appointment> listAll() throws SQLException;

    /**
     * 根据主键查询单条预约记录
     * @param appointmentID 要查询的预约ID
     * @return 如果不存在则返回 null
     * @throws SQLException
     */
    Appointment findById(int appointmentID) throws SQLException;

    /**
     * 新增一条预约记录
     * @param appointment 待新增的实体，至少要有 ServiceItemID、AppointmentDate、Status 等必要字段
     * @return 插入成功返回 true，否则返回 false
     * @throws SQLException
     */
    boolean create(Appointment appointment) throws SQLException;

    /**
     * 更新一条已有的预约记录
     * @param appointment 包含已存在的 appointmentID 和所有要更新的字段
     * @return 更新成功返回 true，否则返回 false
     * @throws SQLException
     */
    boolean update(Appointment appointment) throws SQLException;

    /**
     * 根据 ID 删除一条预约记录
     * @param appointmentID 要删除的预约 ID
     * @return 删除成功返回 true，否则返回 false
     * @throws SQLException
     */
    boolean delete(int appointmentID) throws SQLException;

    /**
     * 基于关键字对当前所有预约做简单过滤（示例为按数字字段做匹配）
     * 关键字如果能解析为整数，将和 appointmentID、memberID、serviceItemID、staffID 做相等比较；
     * 如果解析不出整数，则直接返回所有记录（或也可返回空列表）。此处示例先返回所有。
     *
     * @param keyword 用户输入的关键字
     * @return 过滤后的预约列表
     * @throws SQLException
     */
    List<Appointment> search(String keyword) throws SQLException;
}
