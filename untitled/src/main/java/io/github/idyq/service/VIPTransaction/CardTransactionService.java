package io.github.idyq.service.VIPTransaction;

import io.github.idyq.model.entity.CardTransaction;

import java.sql.SQLException;
import java.util.List;

/**
 * 会员充值/消费流水相关业务接口
 */
public interface CardTransactionService {
    /**
     * 查询指定会员的所有流水记录（按时间降序排序）
     * @param memberId 会员ID
     * @return List&lt;CardTransaction&gt;，可能空列表，但不会为 null
     * @throws SQLException 数据库操作异常
     */
    List<CardTransaction> listByMember(int memberId) throws SQLException;

    /**
     * 添加一条充值或消费流水，并自动计算新的余额
     * @param transaction 传入的 CardTransaction 对象，需包含：
     *                    - memberID
     *                    - type（"Recharge" 或 "Consume"）
     *                    - amount（正数，不含正负号）
     *                    - notes（可选备注）
     *                    transactionDate 与 balanceAfter 在 Service 内设置
     * @return 如果插入成功返回 true，否则 false
     * @throws SQLException 数据库操作异常
     */
    boolean addTransaction(CardTransaction transaction) throws SQLException;
}
