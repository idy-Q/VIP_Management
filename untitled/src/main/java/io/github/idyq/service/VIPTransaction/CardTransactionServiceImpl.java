package io.github.idyq.service.VIPTransaction;

import io.github.idyq.dao.table.CardTransactionDao;
import io.github.idyq.dao.table.impl.CardTransactionDaoImpl;
import io.github.idyq.model.entity.CardTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import io.github.idyq.util.SqliteJdbcUtil;

/**
 * CardTransactionService 的默认实现：
 *  - listByMember：读取所有流水后，Service 层过滤并排序
 *  - addTransaction：根据当前会员最后一笔流水的余额，计算新余额并插入
 */
public class CardTransactionServiceImpl implements CardTransactionService {

    // DAO 实例
    private final CardTransactionDao transactionDao = new CardTransactionDaoImpl();

    @Override
    public List<CardTransaction> listByMember(int memberId) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            // 1. 拿到所有流水
            List<CardTransaction> all = transactionDao.findAll(conn);
            if (all == null || all.isEmpty()) {
                return List.of();
            }
            // 2. 过滤出指定 memberId 的记录，并按 transactionDate 降序排序
            return all.stream()
                    .filter(tx -> tx.getMemberID() == memberId)
                    .sorted(Comparator.comparing(CardTransaction::getTransactionDate).reversed())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean addTransaction(CardTransaction transaction) throws SQLException {
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            // 1. 查询当前会员的所有流水，找出最新一条以获取“上一余额”
            List<CardTransaction> all = transactionDao.findAll(conn);
            double prevBalance = 0.0;
            if (all != null && !all.isEmpty()) {
                // 过滤并取出指定会员的流水，按时间降序取第一条
                prevBalance = all.stream()
                        .filter(tx -> tx.getMemberID() == transaction.getMemberID())
                        .max(Comparator.comparing(CardTransaction::getTransactionDate))
                        .map(CardTransaction::getBalanceAfter)
                        .orElse(0.0);
            }
            // 2. 根据类型和金额计算新余额
            double amount = transaction.getAmount();
            double newBalance;
            if ("Consume".equalsIgnoreCase(transaction.getType())) {
                newBalance = prevBalance - amount;
            } else { // 默认视作 Recharge
                newBalance = prevBalance + amount;
            }
            // 3. 把当前时间和新余额写入 transaction 对象
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setBalanceAfter(newBalance);

            // 4. 执行插入
            return transactionDao.insert(conn, transaction);
        }
    }
}
