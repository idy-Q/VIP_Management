package io.github.idyq.service.VIP;

import io.github.idyq.model.entity.MemberLevel;

import java.sql.SQLException;
import java.util.List;

/**
 * 会员等级相关业务接口
 */
public interface MemberLevelService {
    /** 查询所有等级 */
    List<MemberLevel> listAll() throws SQLException;
    /** 根据 ID 查询单个等级 */
    MemberLevel findById(int levelID) throws SQLException;
    /** 新增等级 */
    boolean create(MemberLevel level) throws SQLException;
    /** 修改等级 */
    boolean update(MemberLevel level) throws SQLException;
    /** 删除等级 */
    boolean delete(int levelID) throws SQLException;
}
