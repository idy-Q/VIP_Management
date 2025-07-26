/*
 * Created by JFormDesigner on Tue Jun 03 23:30:03 CST 2025
 */

package io.github.idyq.view.Panel.VIPLevel;

import io.github.idyq.model.entity.MemberLevel;
import io.github.idyq.service.VIP.MemberLevelService;
import io.github.idyq.service.VIP.MemberLevelServiceImpl;

import java.sql.SQLException;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * @author guox100
 */
public class VIPLevel extends JPanel {
    private final MemberLevelService levelService = new MemberLevelServiceImpl();

    public VIPLevel() {
        initComponents();
        loadLevelTable();
    }

    private void btnSelectAction(ActionEvent e) {
        // TODO add your code here
        loadLevelTable();
    }

    private void btnAddAction(ActionEvent e) {
        // TODO add your code here
        AddLevel addLevel = new AddLevel(
                SwingUtilities.getWindowAncestor(this)
        );
        addLevel.setVisible(true);
        addLevel.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void btnUpdateAction(ActionEvent e) {
        // TODO add your code here
        int selectedRow = table1.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先在表格中选中要修改的等级",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 假设 table1 的第一列是 LevelID，确保 getValueAt 返回的是 Integer
        Object idObj = table1.getValueAt(selectedRow, 0);
        if (!(idObj instanceof Integer)) {
            JOptionPane.showMessageDialog(
                    this,
                    "无法获取选中行的 LevelID",
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        int levelId = (Integer) idObj;

        // 通过 Service 从数据库查询出完整的 MemberLevel 对象
        MemberLevel levelToEdit;
        try {
            levelToEdit = levelService.findById(levelId);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "读取等级数据失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (levelToEdit == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "未找到对应的等级记录",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 弹出 UpdateLevel 对话框进行编辑
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        UpdateLevel dialog = new UpdateLevel(parentWindow, levelToEdit);
        dialog.setModal(true);
        dialog.setVisible(true);

        // 用户关闭对话框后，刷新表格数据
        loadLevelTable();
    }

    private void btnDeleteAction(ActionEvent e) {
        // TODO add your code here
        // 1. 获取选中的行
        int[] selectedRows = table1.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先选中要删除的行",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. 弹出确认对话框
        int choice = JOptionPane.showConfirmDialog(
                this,
                "确认要删除选中的 " + selectedRows.length + " 条记录吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) {
            return; // 用户取消
        }

        // 3. 循环删除每个选中行对应的 LevelID
        boolean allSuccess = true;
        for (int rowIndex : selectedRows) {
            // 假设第 0 列存放的是 Integer 类型的 LevelID
            Object idObj = table1.getValueAt(rowIndex, 0);
            if (idObj instanceof Integer) {
                int levelId = (Integer) idObj;
                try {
                    boolean deleted = levelService.delete(levelId);
                    if (!deleted) {
                        allSuccess = false;
                        // 如果有一条删除失败，继续尝试删除剩余行
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    allSuccess = false;
                }
            }
        }

        // 4. 根据删除结果给用户反馈
        if (allSuccess) {
            JOptionPane.showMessageDialog(
                    this,
                    "删除成功",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "部分删除操作失败，请检查是否有外键约束或其他问题",
                    "警告",
                    JOptionPane.WARNING_MESSAGE
            );
        }

        // 5. 刷新表格
        loadLevelTable();
    }


    private void loadLevelTable() {
        String[] columns = {
                "LevelID", "LevelName", "MinPoints", "DiscountRate", "Benefits"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        try {
            List<MemberLevel> list = levelService.listAll();
            for (MemberLevel lvl : list) {
                model.addRow(new Object[] {
                        lvl.getLevelID(),
                        lvl.getLevelName(),
                        lvl.getMinPoints(),
                        lvl.getDiscountRate(),
                        lvl.getBenefits()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "加载等级数据失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        table1.setModel(model);
        if (table1.getColumnModel().getColumnCount() > 0) {
            table1.getColumnModel().getColumn(0).setPreferredWidth(60);
            table1.getColumnModel().getColumn(1).setPreferredWidth(100);
            table1.getColumnModel().getColumn(2).setPreferredWidth(80);
            table1.getColumnModel().getColumn(3).setPreferredWidth(100);
            table1.getColumnModel().getColumn(4).setPreferredWidth(200);
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        btnSelect = new JButton();
        btnAdd = new JButton();
        btnUpdate = new JButton();
        btnDelete = new JButton();
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();

        //======== this ========
        setLayout(null);

        //---- btnSelect ----
        btnSelect.setText("\u5237\u65b0");
        btnSelect.addActionListener(e -> btnSelectAction(e));
        add(btnSelect);
        btnSelect.setBounds(440, 415, 113, 34);

        //---- btnAdd ----
        btnAdd.setText("\u6dfb\u52a0");
        btnAdd.addActionListener(e -> btnAddAction(e));
        add(btnAdd);
        btnAdd.setBounds(35, 30, 80, 35);

        //---- btnUpdate ----
        btnUpdate.setText("\u4fee\u6539");
        btnUpdate.addActionListener(e -> btnUpdateAction(e));
        add(btnUpdate);
        btnUpdate.setBounds(130, 30, 75, 35);

        //---- btnDelete ----
        btnDelete.setText("\u5220\u9664");
        btnDelete.addActionListener(e -> btnDeleteAction(e));
        add(btnDelete);
        btnDelete.setBounds(220, 30, 75, 35);

        //---- label1 ----
        label1.setText("\u9009\u4e2d\u6570\u636e\u8fdb\u884c\u64cd\u4f5c");
        label1.setForeground(Color.gray);
        label1.setFont(label1.getFont().deriveFont(Font.BOLD|Font.ITALIC, label1.getFont().getSize() - 1f));
        add(label1);
        label1.setBounds(35, 70, 100, 15);

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }
        add(scrollPane1);
        scrollPane1.setBounds(35, 100, 540, 250);

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
    private JButton btnSelect;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTable table1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
