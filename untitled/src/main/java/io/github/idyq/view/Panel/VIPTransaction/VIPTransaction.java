/*
 * Created by JFormDesigner on Wed Jun 04 22:51:50 CST 2025
 */

package io.github.idyq.view.Panel.VIPTransaction;

import io.github.idyq.dao.table.MemberDao;
import io.github.idyq.dao.table.impl.MemberDaoImpl;
import io.github.idyq.model.entity.CardSettings;
import io.github.idyq.model.entity.CardTransaction;
import io.github.idyq.model.entity.Member;
import io.github.idyq.model.entity.MemberLevel;
import io.github.idyq.service.VIP.MemberLevelService;
import io.github.idyq.service.VIP.MemberLevelServiceImpl;
import io.github.idyq.service.VIPTransaction.CardSettingsService;
import io.github.idyq.service.VIPTransaction.CardSettingsServiceImpl;
import io.github.idyq.service.VIPTransaction.CardTransactionService;
import io.github.idyq.service.VIPTransaction.CardTransactionServiceImpl;
import io.github.idyq.util.SqliteJdbcUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * @author guox100
 */
public class VIPTransaction extends JPanel {
    /** 用于查询会员信息 */
    private final MemberDao memberDao = new MemberDaoImpl();
    /** 用于处理充值/消费流水 */
    private final CardTransactionService txnService = new CardTransactionServiceImpl();

    private final CardSettingsService settingsService = new CardSettingsServiceImpl();
    private final MemberLevelService levelService = new MemberLevelServiceImpl();

    /** 当前选中的会员 ID；0 表示未选中 */
    private int currentMemberId = 0;

    public VIPTransaction() {
        initComponents();
        // 初始时禁用操作区与刷新按钮
        setOperationEnabled(false);
    }


    /**
     * 点击“查询”按钮后的逻辑：
     * 根据 txtMemberKey 中的内容（数字则当作 MemberID，非数字则当作姓名）查询会员，
     * 如果找到，将其 ID 存入 currentMemberId，并显示当前余额与流水记录；
     * 否则弹出提示。
     */
    private void btnMemberSearchAction(ActionEvent e) {
        // TODO add your code here
        String key = txtMemberKey.getText().trim();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "请输入会员 ID 或姓名进行查询",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 1. 先尝试按数字 ID 查
        Member member = null;
        try {
            if (key.matches("\\d+")) {
                int id = Integer.parseInt(key);
                member = memberDao.findById(SqliteJdbcUtil.getConnection(), id);
            }
        } catch (Exception ex) {
            // 这里 catch SQLException 或其他，后面继续尝试按姓名
        }

        // 2. 如果按 ID 查不到，或者 key 不是数字，再按姓名查（简单地从 findAll 中匹配）
        if (member == null) {
            try (Connection conn = SqliteJdbcUtil.getConnection()) {
                List<Member> all = memberDao.findAll(conn);
                for (Member m : all) {
                    if (m.getName() != null && m.getName().equalsIgnoreCase(key)) {
                        member = m;
                        break;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "查询会员时出错：" + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        // 3. 如果仍未查到，提示并返回
        if (member == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "未找到该会员，请检查输入的 ID/姓名",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 4. 查询到会员，保存 ID，并展示其当前余额与流水
        currentMemberId = member.getMemberID();

        // 先从流水中取出最新一条余额（如果没有流水，则余额为 0.00）
        double balance = 0.0;
        try {
            List<CardTransaction> txs = txnService.listByMember(currentMemberId);
            if (!txs.isEmpty()) {
                balance = txs.get(0).getBalanceAfter();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "获取当前余额时出错：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        lblBalance.setText(String.format("%.2f", balance));

        // 刷新流水表格
        loadTransactionTable(currentMemberId);

        // 启用操作区和刷新按钮
        setOperationEnabled(true);
    }

    /**
     * 点击“刷新”按钮后的逻辑：
     * 如果已经选中会员，则重新加载该会员的流水；否则提示先查询会员。
     */
    private void btnRefreshAction(ActionEvent e) {
        // TODO add your code here
        if (currentMemberId <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先查询并选择会员",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        loadTransactionTable(currentMemberId);
    }

    /**
     * 点击“确认操作”按钮后的逻辑：
     * 1. 如果当前选中会员无效，提示并返回
     * 2. 解析用户输入的金额 amount > 0，否则提示
     * 3. 根据 rbRecharge 是否选中决定“充值”还是“消费”
     *
     * 充值时：
     *   a) 调用 settingsService.listAll() 拿到所有充值套餐，按 chargeAmount 升序排列
     *   b) 在这些套餐中，找出“第一个 chargeAmount > amount”作为 nextSetting
     *      - 如果 nextSetting != null，则弹出提示：您距离【nextSetting.getPlanName()】套餐还差 Δ 元，可加 Δ 以获赠 bonusAmount；有“返回充值”“直接充值”两个按钮
     *         • 如果用户选“返回充值”，直接 return，让用户重新输入
     *         • 如果用户选“直接充值”，不再赠送任何 bonus，按 amount 完成充值
     *      - 如果 nextSetting == null，则说明 amount ≥ 所有套餐的最高 chargeAmount，此时找到 chargeAmount ≤ amount 范围内的最大 oneSetting，将其 bonusAmount 赠送给会员
     *   c) 生成一条“Recharge”流水记录：先记录用户支付 amount，balanceAfter = prevBalance + amount + bonusAmount
     *   d) 如果 bonusAmount > 0，则额外生成一条“Bonus”流水记录，用于让前端流水表显示赠送情况
     *
     * 消费时：
     *   a) 先从 memberDao 和 levelService 查出该会员对应的 MemberLevel，获取 discountRate
     *   b) 如果 discountRate < 1.0，则弹出提示：您享 X 折，原价 Y，折后 Z，是否确认？
     *       • 若用户“取消”，return
     *       • 若用户“确认”，去掉折后金额
     *   c) 如果 discountRate == 1.0，直接按原价 amount 扣除
     *   d) 生成一条“Consume”流水记录，balanceAfter = prevBalance – realAmount
     *
     * 4. 最后刷新 lblBalance（再次从最新流水取余额）并调用 loadTransactionTable(...)
     */
    private void btnExecuteAction(ActionEvent e) {
        // TODO add your code here
        if (currentMemberId <= 0) {
            JOptionPane.showMessageDialog(this, "请先查询并选择会员", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. 校验金额输入
        String amtText = txtAmount.getText().trim();
        double amount;
        try {
            amount = Double.parseDouble(amtText);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入正确的正数金额", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. 判断“充值”或“消费”
        if (rbRecharge.isSelected()) {
            // --- 充值流程 ---
            List<CardSettings> allSettings;
            try {
                allSettings = settingsService.listAll();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "获取充值套餐失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 按 chargeAmount 升序排序
            allSettings.sort(Comparator.comparing(CardSettings::getChargeAmount));

//            // 找到第一档 chargeAmount > amount，即“下一档”
//            CardSettings nextSetting = null;
//            for (CardSettings cs : allSettings) {
//                if (cs.getChargeAmount() > amount) {
//                    nextSetting = cs;
//                    break;
//                }
//            }
//
//            double bonusToGive = 0.0;
//            // 如果存在下一档，则弹出提示看用户是否要凑到下一档
//            if (nextSetting != null) {
//                double diff = nextSetting.getChargeAmount() - amount;
//                String message = String.format("您距离“%s”套餐还差 %.2f 元，可再充值至 %.2f 元并获得赠送 %.2f 元，是否直接充值当前 %.2f 元？",
//                        nextSetting.getPlanName(),
//                        diff,
//                        nextSetting.getChargeAmount(),
//                        nextSetting.getBonusAmount(),
//                        amount);
//                // 提示框按钮：0=“返回充值”，1=“直接充值”
//                int choice = JOptionPane.showOptionDialog(
//                        this,
//                        message,
//                        "充值套餐提示",
//                        JOptionPane.DEFAULT_OPTION,
//                        JOptionPane.INFORMATION_MESSAGE,
//                        null,
//                        new Object[]{ "返回充值", "直接充值" },
//                        "返回充值"
//                );
//                if (choice == 0) {
//                    // 用户选“返回充值”，不做任何操作，退回到界面
//                    return;
//                }
//                // 用户选“直接充值”，则不再赠送 nextSetting.getBonusAmount()
//                bonusToGive = 0.0;
//            } else {
//                // nextSetting == null，说明 amount ≥ 所有档次的最大 chargeAmount，
//                // 那么找出 chargeAmount ≤ amount 中最大的那一档，赠送对应 bonusAmount
//                CardSettings best = null;
//                for (CardSettings cs : allSettings) {
//                    if (cs.getChargeAmount() <= amount) {
//                        best = cs;
//                    } else {
//                        break;
//                    }
//                }
//                if (best != null) {
//                    bonusToGive = best.getBonusAmount();
//                }
//            }


            // 根据 amount 匹配最适合的套餐（即最大的小于等于的）
            CardSettings bestSetting = null;
            CardSettings nextSetting = null;

            for (CardSettings cs : allSettings) {
                if (cs.getChargeAmount() <= amount) {
                    bestSetting = cs;
                } else if (nextSetting == null) {
                    nextSetting = cs;
                }
            }

            // 给用户弹提示（但不影响福利给 best）
            double bonusToGive = 0.0;

            if (nextSetting != null && bestSetting != null) {
                double diff = nextSetting.getChargeAmount() - amount;
                String message = String.format(
                        "您已达到“%s”套餐，可获得赠送 %.2f 元\n如果再充值 %.2f 元可升级至“%s”，可获得 %.2f 元赠送\n是否继续当前充值？",
                        bestSetting.getPlanName(),
                        bestSetting.getBonusAmount(),
                        diff,
                        nextSetting.getPlanName(),
                        nextSetting.getBonusAmount()
                );

                int choice = JOptionPane.showConfirmDialog(
                        this,
                        message,
                        "充值优惠提示",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            } else if (nextSetting != null && bestSetting == null) {
                // 没有符合当前金额的任何套餐，但有下一档
                double diff = nextSetting.getChargeAmount() - amount;
                String msg = String.format(
                        "当前金额尚未达到任何优惠档位，再充值 %.2f 元可享受“%s”赠送 %.2f 元\n是否继续当前充值？",
                        diff,
                        nextSetting.getPlanName(),
                        nextSetting.getBonusAmount()
                );
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        msg,
                        "充值提示",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            if (bestSetting != null) {
                bonusToGive = bestSetting.getBonusAmount();
            }


            // --- 生成充值流水 ---
            // 先找出上一余额
            double prevBalance = 0.0;
            try {
                List<CardTransaction> txs = txnService.listByMember(currentMemberId);
                if (!txs.isEmpty()) {
                    prevBalance = txs.get(0).getBalanceAfter();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "读取上一余额失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 1) 插入一条“Recharge”流水
            CardTransaction rechargeTx = new CardTransaction();
            rechargeTx.setMemberID(currentMemberId);
            rechargeTx.setType("Recharge");
            rechargeTx.setAmount(amount);
            rechargeTx.setNotes(txtRemark.getText().trim());
            rechargeTx.setTransactionDate(LocalDateTime.now());
            rechargeTx.setBalanceAfter(prevBalance + amount + bonusToGive);

            try {
                boolean ok = txnService.addTransaction(rechargeTx);
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "充值失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "充值出错：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2) 如果有 bonusToGive > 0，插入一条“Bonus”流水
            if (bonusToGive > 0) {
                CardTransaction bonusTx = new CardTransaction();
                bonusTx.setMemberID(currentMemberId);
                bonusTx.setType("Recharge");
                bonusTx.setAmount(bonusToGive);
                bonusTx.setNotes("充值赠送");
                bonusTx.setTransactionDate(LocalDateTime.now());
                bonusTx.setBalanceAfter(prevBalance + amount + bonusToGive);

                try {
                    txnService.addTransaction(bonusTx);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // 虽然赠送流水失败，但充值已经成功，这里只弹个警告
                    JOptionPane.showMessageDialog(this,
                            "充值成功，但赠送流水记录失败：" + ex.getMessage(),
                            "警告",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

        } else {
            // --- 消费流程 ---
            // 1) 找到该会员的折扣率
            double discountRate = 1.0;
            try (Connection conn = SqliteJdbcUtil.getConnection()) {
                Member m = memberDao.findById(conn, currentMemberId);
                if (m != null) {
                    MemberLevel level = levelService.findById(m.getLevelID());
                    if (level != null) {
                        discountRate = level.getDiscountRate();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "获取会员折扣失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double realAmount = amount;
            if (discountRate < 1.0) {
                // 有折扣，弹出提示
                double discounted = amount * discountRate;
                String msg = String.format("您享受 %.0f%% 折扣\n原价: %.2f 元  折后: %.2f 元\n是否确认消费？",
                        discountRate * 100,
                        amount,
                        discounted);
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        msg,
                        "消费折扣提示",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (choice != JOptionPane.YES_OPTION) {
                    // 用户选择“否”，返回界面
                    return;
                }
                realAmount = discounted;
            }
            // discountRate == 1.0，则直接 realAmount = amount，无需提示

            // --- 生成消费流水 ---
            // 先找出上一余额
            double prevBalance = 0.0;
            try {
                List<CardTransaction> txs = txnService.listByMember(currentMemberId);
                if (!txs.isEmpty()) {
                    prevBalance = txs.get(0).getBalanceAfter();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "读取上一余额失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CardTransaction consumeTx = new CardTransaction();
            consumeTx.setMemberID(currentMemberId);
            consumeTx.setType("Consume");
            consumeTx.setAmount(realAmount);
            consumeTx.setNotes(txtRemark.getText().trim());
            consumeTx.setTransactionDate(LocalDateTime.now());
            consumeTx.setBalanceAfter(prevBalance - realAmount);

            try {
                boolean ok = txnService.addTransaction(consumeTx);
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "消费失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "消费出错：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 4. 无论充值或消费成功，都要刷新 balance & 流水
        double newBalance = 0.0;
        try {
            List<CardTransaction> txs = txnService.listByMember(currentMemberId);
            if (!txs.isEmpty()) {
                newBalance = txs.get(0).getBalanceAfter();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "刷新余额失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
        lblBalance.setText(String.format("%.2f", newBalance));
        loadTransactionTable(currentMemberId);

        // 5. 清空输入区并恢复默认：充值单选为选中
        txtAmount.setText("");
        txtRemark.setText("");
        rbRecharge.setSelected(true);
    }

    /**
     * 私有工具：加载并展示指定会员的流水记录到 tableTrans
     * @param memberId 要查询的会员 ID
     */
    private void loadTransactionTable(int memberId) {
        // 1. 定义表格列头
        String[] cols = { "交易ID", "交易日期", "类型", "金额", "余额", "备注" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        try {
            List<CardTransaction> txs = txnService.listByMember(memberId);
            for (CardTransaction tx : txs) {
                // 若是消费 (type="Consume")，在界面显示时可以前面加负号或直接取负数
                double displayAmt = "Consume".equalsIgnoreCase(tx.getType())
                        ? -tx.getAmount()
                        : tx.getAmount();

                model.addRow(new Object[] {
                        tx.getTransactionID(),
                        tx.getTransactionDate(),
                        tx.getType(),
                        displayAmt,
                        tx.getBalanceAfter(),
                        tx.getNotes()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "加载流水记录失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        tableTrans.setModel(model);
        // 简单调整列宽，列索引可按实际需要微调
        if (tableTrans.getColumnModel().getColumnCount() > 0) {
            tableTrans.getColumnModel().getColumn(0).setPreferredWidth(60);
            tableTrans.getColumnModel().getColumn(1).setPreferredWidth(140);
            tableTrans.getColumnModel().getColumn(2).setPreferredWidth(80);
            tableTrans.getColumnModel().getColumn(3).setPreferredWidth(80);
            tableTrans.getColumnModel().getColumn(4).setPreferredWidth(80);
            tableTrans.getColumnModel().getColumn(5).setPreferredWidth(200);
        }
    }

    /**
     * 启用或禁用左侧“充值/消费”操作区和“刷新”按钮。
     */
    private void setOperationEnabled(boolean enabled) {
        rbRecharge.setEnabled(enabled);
        rbConsume.setEnabled(enabled);
        txtAmount.setEnabled(enabled);
        txtRemark.setEnabled(enabled);
        btnExecute.setEnabled(enabled);
        btnRefresh.setEnabled(enabled);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        panelNorth = new JPanel();
        label1 = new JLabel();
        txtMemberKey = new JTextField();
        btnMemberSearch = new JButton();
        btnRefresh = new JButton();
        panelWest = new JPanel();
        labelBalanceTitle = new JLabel();
        lblBalance = new JLabel();
        labelOpType = new JLabel();
        rbRecharge = new JRadioButton();
        rbConsume = new JRadioButton();
        labelAmount = new JLabel();
        txtAmount = new JTextField();
        labelRemark = new JLabel();
        txtRemark = new JTextField();
        btnExecute = new JButton();
        scrollPane1 = new JScrollPane();
        tableTrans = new JTable();

        //======== this ========
        setLayout(null);

        //======== panelNorth ========
        {
            panelNorth.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

            //---- label1 ----
            label1.setText("\u4f1a\u5458 ID\uff1a");
            panelNorth.add(label1);

            //---- txtMemberKey ----
            txtMemberKey.setPreferredSize(new Dimension(150, 34));
            panelNorth.add(txtMemberKey);

            //---- btnMemberSearch ----
            btnMemberSearch.setText("\u67e5\u8be2");
            btnMemberSearch.addActionListener(e -> btnMemberSearchAction(e));
            panelNorth.add(btnMemberSearch);

            //---- btnRefresh ----
            btnRefresh.setText("\u5237\u65b0");
            btnRefresh.addActionListener(e -> btnRefreshAction(e));
            panelNorth.add(btnRefresh);
        }
        add(panelNorth);
        panelNorth.setBounds(new Rectangle(new Point(5, 5), panelNorth.getPreferredSize()));

        //======== panelWest ========
        {
            panelWest.setPreferredSize(new Dimension(200, 200));
            panelWest.setLayout(null);

            //---- labelBalanceTitle ----
            labelBalanceTitle.setText("\u5f53\u524d\u4f59\u989d\uff1a");
            labelBalanceTitle.setPreferredSize(new Dimension(80, 30));
            panelWest.add(labelBalanceTitle);
            labelBalanceTitle.setBounds(new Rectangle(new Point(20, 70), labelBalanceTitle.getPreferredSize()));

            //---- lblBalance ----
            lblBalance.setText("0.00");
            lblBalance.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
            lblBalance.setPreferredSize(new Dimension(80, 30));
            panelWest.add(lblBalance);
            lblBalance.setBounds(100, 70, 100, 30);

            //---- labelOpType ----
            labelOpType.setText("\u64cd\u4f5c\u7c7b\u578b\uff1a");
            labelOpType.setPreferredSize(new Dimension(80, 30));
            panelWest.add(labelOpType);
            labelOpType.setBounds(new Rectangle(new Point(20, 110), labelOpType.getPreferredSize()));

            //---- rbRecharge ----
            rbRecharge.setText("\u5145\u503c");
            rbRecharge.setPreferredSize(new Dimension(80, 30));
            // Post-Initialization Code
            ButtonGroup bgOpType = new ButtonGroup();
            bgOpType.add(rbRecharge);
            bgOpType.add(rbConsume);
            panelWest.add(rbRecharge);
            rbRecharge.setBounds(new Rectangle(new Point(20, 140), rbRecharge.getPreferredSize()));

            //---- rbConsume ----
            rbConsume.setText("\u6d88\u8d39");
            rbConsume.setPreferredSize(new Dimension(80, 30));
            panelWest.add(rbConsume);
            rbConsume.setBounds(new Rectangle(new Point(100, 140), rbConsume.getPreferredSize()));

            //---- labelAmount ----
            labelAmount.setText("\u91d1\u989d\uff08\u5143\uff09\uff1a");
            labelAmount.setPreferredSize(new Dimension(80, 30));
            panelWest.add(labelAmount);
            labelAmount.setBounds(new Rectangle(new Point(20, 180), labelAmount.getPreferredSize()));

            //---- txtAmount ----
            txtAmount.setPreferredSize(new Dimension(100, 30));
            panelWest.add(txtAmount);
            txtAmount.setBounds(new Rectangle(new Point(100, 180), txtAmount.getPreferredSize()));

            //---- labelRemark ----
            labelRemark.setText("\u5907\u6ce8\uff1a");
            labelRemark.setPreferredSize(new Dimension(80, 30));
            panelWest.add(labelRemark);
            labelRemark.setBounds(new Rectangle(new Point(20, 220), labelRemark.getPreferredSize()));

            //---- txtRemark ----
            txtRemark.setPreferredSize(new Dimension(160, 30));
            panelWest.add(txtRemark);
            txtRemark.setBounds(new Rectangle(new Point(20, 250), txtRemark.getPreferredSize()));

            //---- btnExecute ----
            btnExecute.setText("\u786e\u8ba4\u64cd\u4f5c");
            btnExecute.setPreferredSize(new Dimension(120, 40));
            btnExecute.addActionListener(e -> btnExecuteAction(e));
            panelWest.add(btnExecute);
            btnExecute.setBounds(new Rectangle(new Point(40, 300), btnExecute.getPreferredSize()));

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panelWest.getComponentCount(); i++) {
                    Rectangle bounds = panelWest.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panelWest.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panelWest.setMinimumSize(preferredSize);
                panelWest.setPreferredSize(preferredSize);
            }
        }
        add(panelWest);
        panelWest.setBounds(5, 70, panelWest.getPreferredSize().width, 410);

        //======== scrollPane1 ========
        {
            scrollPane1.setPreferredSize(new Dimension(600, 500));

            //---- tableTrans ----
            tableTrans.setPreferredSize(new Dimension(700, 500));
            scrollPane1.setViewportView(tableTrans);
        }
        add(scrollPane1);
        scrollPane1.setBounds(280, 70, 510, 375);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < getComponentCount(); i++) {
                Rectangle bounds = getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            setMinimumSize(preferredSize);
            setPreferredSize(preferredSize);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panelNorth;
    private JLabel label1;
    private JTextField txtMemberKey;
    private JButton btnMemberSearch;
    private JButton btnRefresh;
    private JPanel panelWest;
    private JLabel labelBalanceTitle;
    private JLabel lblBalance;
    private JLabel labelOpType;
    private JRadioButton rbRecharge;
    private JRadioButton rbConsume;
    private JLabel labelAmount;
    private JTextField txtAmount;
    private JLabel labelRemark;
    private JTextField txtRemark;
    private JButton btnExecute;
    private JScrollPane scrollPane1;
    private JTable tableTrans;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
