package io.github.idyq.service.VIPTransaction;

import io.github.idyq.model.entity.CardSettings;

import java.sql.SQLException;
import java.util.List;

/**
 * 充值套餐设置相关业务接口
 */
public interface CardSettingsService {
    /**
     * 查询所有可用的充值套餐（按数据库 return 顺序即可）
     * @return 可能为空的 List，但不返回 null
     * @throws SQLException
     */
    List<CardSettings> listAll() throws SQLException;

    /**
     * 新增一条充值套餐
     * @param settings 待新增的实体，planName/chargeAmount/bonusAmount 至少非空
     * @return 插入成功返回 true，否则 false
     * @throws SQLException
     */
    boolean insert(CardSettings settings) throws SQLException;

    /**
     * 根据 SettingID 更新对应的充值套餐
     * @param settings 包含已存在的 settingID 及新的字段值
     * @return 更新成功返回 true，否则 false
     * @throws SQLException
     */
    boolean update(CardSettings settings) throws SQLException;

    /**
     * 根据 SettingID 删除对应的充值套餐
     * @param settingID 待删除的 ID
     * @return 删除成功返回 true，否则 false
     * @throws SQLException
     */
    boolean deleteById(int settingID) throws SQLException;
}
