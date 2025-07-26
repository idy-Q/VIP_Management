/*
 * Created by JFormDesigner on Thu Jun 05 16:29:02 CST 2025
 */

package io.github.idyq.view.Panel.CardSettings;

import io.github.idyq.model.entity.CardSettings;
import io.github.idyq.service.VIPTransaction.CardSettingsService;
import io.github.idyq.service.VIPTransaction.CardSettingsServiceImpl;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * @author guox100
 */
public class CardSettingsPanel extends JPanel {
    private final CardSettingsService settingsService = new CardSettingsServiceImpl();

    public CardSettingsPanel() {
        initComponents();
        // 构造完成后立即加载并显示所有充值方案
        loadSettingsTable();
    }

    private void btnRefreshAction(ActionEvent e) {
        // TODO add your code here
        loadSettingsTable();
    }

    private void btnAddAction(ActionEvent e) {
        // TODO add your code here
        AddSettingsDialog dialog = new AddSettingsDialog(
                SwingUtilities.getWindowAncestor(this)
        );
        dialog.setVisible(true);
        loadSettingsTable();
    }

    private void btnUpdateAction(ActionEvent e) {
        // TODO add your code here
        int row = tableSettings.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先选中一行要修改的充值方案",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 从模型中读取各列值
        DefaultTableModel model = (DefaultTableModel) tableSettings.getModel();
        Integer settingID = (Integer) model.getValueAt(row, 0);
        String planName = (String) model.getValueAt(row, 1);
        Double chargeAmount = (Double) model.getValueAt(row, 2);
        Double bonusAmount = (Double) model.getValueAt(row, 3);
        // “有效期”列可能是空字符串或 Integer
        Object effObj = model.getValueAt(row, 4);
        Integer effectivePeriod = null;
        if (effObj instanceof Integer) {
            effectivePeriod = (Integer) effObj;
        }

        // 读取“CreatedAt”列，可能是 LocalDateTime 或者 String
        Object createdAtObj = model.getValueAt(row, 5);
        LocalDateTime createdAt = null;
        if (createdAtObj instanceof LocalDateTime) {
            createdAt = (LocalDateTime) createdAtObj;
        } else if (createdAtObj instanceof String) {
            // JTable 已把其渲染为字符串，需要解析回 LocalDateTime
            try {
                createdAt = LocalDateTime.parse((String) createdAtObj);
            } catch (DateTimeParseException ex) {
                // 如果解析失败，可以提示或直接用当前时间
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "无法解析创建时间: " + createdAtObj,
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        // 构造实体并赋值
        CardSettings settings = new CardSettings();
        settings.setSettingID(settingID);
        settings.setPlanName(planName);
        settings.setChargeAmount(chargeAmount);
        settings.setBonusAmount(bonusAmount);
        settings.setEffectivePeriod(effectivePeriod);
        // 一定要把原来的 createdAt 赋值给实体，否则 update 时会为 null
        settings.setCreatedAt(createdAt);

        // 弹出修改对话框
        UpdateSettingsDialog dialog = new UpdateSettingsDialog(
                SwingUtilities.getWindowAncestor(this),
                settings
        );
        dialog.setVisible(true);

        // 对话框关闭后，无论是否修改，都刷新表格
        loadSettingsTable();
    }

    private void btnDeleteAction(ActionEvent e) {
        // TODO add your code here
        int row = tableSettings.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先选中一行要删除的充值方案",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 从表格模型中读取 SettingID
        DefaultTableModel model = (DefaultTableModel) tableSettings.getModel();
        Integer settingID = (Integer) model.getValueAt(row, 0);

        // 弹出确认对话框
        int choice = JOptionPane.showConfirmDialog(
                this,
                "确定要删除 ID=" + settingID + " 的充值方案吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        // 调用 Service 删除
        try {
            boolean ok = settingsService.deleteById(settingID);
            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "删除成功",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
                loadSettingsTable();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "删除失败，请重试",
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "删除出错：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    /**
     * 私有方法：从数据库中读取所有 CardSettingsPanel，并填充到 tableSettings。
     */
    private void loadSettingsTable() {
        // 定义表头
        String[] columns = {
                "SettingID", "PlanName", "ChargeAmount", "BonusAmount", "EffectivePeriod", "CreatedAt"
        };
        // 创建一个不可编辑的表格模型
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        try {
            // 从 Service 中取出所有充值方案
            List<CardSettings> list = settingsService.listAll();
            for (CardSettings cs : list) {
                Object period = cs.getEffectivePeriod() != null
                        ? cs.getEffectivePeriod()
                        : ""; // 如果为 null，显示空字符串
                model.addRow(new Object[]{
                        cs.getSettingID(),
                        cs.getPlanName(),
                        cs.getChargeAmount(),
                        cs.getBonusAmount(),
                        period,
                        cs.getCreatedAt()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "加载充值方案失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        tableSettings.setModel(model);
        // 调整各列宽度（可按需微调）
        if (tableSettings.getColumnModel().getColumnCount() > 0) {
            tableSettings.getColumnModel().getColumn(0).setPreferredWidth(60);  // SettingID
            tableSettings.getColumnModel().getColumn(1).setPreferredWidth(150); // PlanName
            tableSettings.getColumnModel().getColumn(2).setPreferredWidth(100); // ChargeAmount
            tableSettings.getColumnModel().getColumn(3).setPreferredWidth(100); // BonusAmount
            tableSettings.getColumnModel().getColumn(4).setPreferredWidth(80);  // EffectivePeriod
            tableSettings.getColumnModel().getColumn(5).setPreferredWidth(140); // CreatedAt
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        panelTop = new JPanel();
        btnRefresh = new JButton();
        btnAdd = new JButton();
        btnUpdate = new JButton();
        btnDelete = new JButton();
        labelTip = new JLabel();
        scrollPaneSettings = new JScrollPane();
        tableSettings = new JTable();

        //======== this ========
        setLayout(null);

        //======== panelTop ========
        {
            panelTop.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

            //---- btnRefresh ----
            btnRefresh.setText("\u5237\u65b0");
            btnRefresh.setPreferredSize(new Dimension(80, 30));
            btnRefresh.addActionListener(e -> btnRefreshAction(e));
            panelTop.add(btnRefresh);

            //---- btnAdd ----
            btnAdd.setText("\u6dfb\u52a0");
            btnAdd.setPreferredSize(new Dimension(80, 30));
            btnAdd.addActionListener(e -> btnAddAction(e));
            panelTop.add(btnAdd);

            //---- btnUpdate ----
            btnUpdate.setText("\u4fee\u6539");
            btnUpdate.setPreferredSize(new Dimension(80, 30));
            btnUpdate.addActionListener(e -> btnUpdateAction(e));
            panelTop.add(btnUpdate);

            //---- btnDelete ----
            btnDelete.setText("\u5220\u9664");
            btnDelete.setPreferredSize(new Dimension(80, 30));
            btnDelete.addActionListener(e -> btnDeleteAction(e));
            panelTop.add(btnDelete);

            //---- labelTip ----
            labelTip.setText("\u9009\u4e2d\u6570\u636e\u8fdb\u884c\u64cd\u4f5c");
            labelTip.setPreferredSize(new Dimension(120, 20));
            labelTip.setFont(labelTip.getFont().deriveFont(Font.BOLD|Font.ITALIC, labelTip.getFont().getSize() - 2f));
            labelTip.setBackground(Color.gray);
            panelTop.add(labelTip);
        }
        add(panelTop);
        panelTop.setBounds(new Rectangle(new Point(5, 5), panelTop.getPreferredSize()));

        //======== scrollPaneSettings ========
        {
            scrollPaneSettings.setPreferredSize(new Dimension(600, 350));
            scrollPaneSettings.setViewportView(tableSettings);
        }
        add(scrollPaneSettings);
        scrollPaneSettings.setBounds(new Rectangle(new Point(5, 50), scrollPaneSettings.getPreferredSize()));

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
    private JPanel panelTop;
    private JButton btnRefresh;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JLabel labelTip;
    private JScrollPane scrollPaneSettings;
    private JTable tableSettings;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
