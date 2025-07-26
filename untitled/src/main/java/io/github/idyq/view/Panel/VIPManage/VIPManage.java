/*
 * Created by JFormDesigner on Mon May 26 17:34:38 CST 2025
 */

package io.github.idyq.view.Panel.VIPManage;

import io.github.idyq.dao.table.MemberDao;
import io.github.idyq.dao.table.impl.MemberDaoImpl;
import io.github.idyq.model.entity.Member;
import io.github.idyq.service.VIP.*;
import io.github.idyq.util.SqliteJdbcUtil;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * @author guox100
 */
public class VIPManage extends JPanel {
    private final MemberService memberService = new MemberServiceImpl();
    private static final String NAME_PLACEHOLDER = "请输入姓名查找";


    public VIPManage() {
        initComponents();
        textLogic();
        loadMemberTable();
    }

    /** 从数据库查询所有会员，并填充到 table */
    private void loadMemberTable() {
        // 列标题
        String[] columns = {
                "ID", "姓名", "性别", "出生日期",
                "电话", "邮箱", "加入日期",
                "等级ID", "积分", "状态"
        };
        // 新建空 Model
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            // 从 service 层拿数据
            List<Member> members = memberService.listAll();
            for (Member m : members) {
                model.addRow(new Object[]{
                        m.getMemberID(),
                        m.getName(),
                        m.getGender(),
                        m.getBirthDate(),
                        m.getPhone(),
                        m.getEmail(),
                        m.getJoinDate(),
                        m.getLevelID(),
                        m.getPoints(),
                        m.getStatus()
                });
            }
        } catch (Exception ex) {
            showError("加载会员数据失败", ex);
        }

        // 设置到表格
        table1.setModel(model);
        // 调整列宽
        adjustColumnWidth();
    }


    /**
     * =====================逻辑事件区======================
     * **/

    /**查询框文本提示内容**/
    private void textLogic() {
        // 1. 设置初始提示文字及样式
        final String placeholder = "请输入姓名查找";
        txtName.setText(placeholder);
        txtName.setForeground(Color.GRAY);

        // 2. 添加焦点监听：聚焦时清除提示，失焦时恢复提示
        txtName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtName.getText().equals(placeholder)) {
                    txtName.setText("");
                    txtName.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtName.getText().isEmpty()) {
                    txtName.setText(placeholder);
                    txtName.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void btnAddAction(ActionEvent e) {
        // TODO add your code here
        AddVIP av = new AddVIP(null);
        av.setVisible(true);
        av.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void btnUpdateAction(ActionEvent e) {
        // TODO add your code here
        int selectedRow = table1.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先在表格中选中一行要修改的会员",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 假设 table1 的第一列保存的是 MemberID
        Integer memberId = (Integer) table1.getValueAt(selectedRow, 0);

        // 从数据库里把整条记录查询出来（也可以在加载表格时就把 Member 对象缓存到某个 List里）
        Member memberToEdit = null;
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            MemberDao dao = new MemberDaoImpl();
            memberToEdit = dao.findById(conn, memberId);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "读取会员数据失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // 如果查到了再弹出修改对话框
        if (memberToEdit != null) {
            UpdateVIP dialog = new UpdateVIP(SwingUtilities.getWindowAncestor(this), memberToEdit);
            dialog.setModal(true);
            dialog.setVisible(true);

            // 对话框关闭以后，重新刷新主表格（因为可能有修改）
            loadMemberTable();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "没有找到对应的会员信息",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void btnDeleteAction(ActionEvent e) {
        // TODO add your code here
        // 1. 取到所有选中的行索引
        int[] selectedRows = table1.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先在表格中选中要删除的会员行",
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
            return; // 用户取消删除
        }

        // 3. 获取 MemberDao
        MemberDao dao = new MemberDaoImpl();
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            // 启动事务（如果需要批量更安全，可自行开启/提交事务；这里简化直接逐条删除）
            boolean allSuccess = true;
            for (int rowIndex : selectedRows) {
                // 假设第一列是 MemberID (Integer)
                Object idObj = table1.getValueAt(rowIndex, 0);
                if (idObj instanceof Integer) {
                    int memberId = (Integer) idObj;
                    boolean ok = dao.deleteById(conn, memberId);
                    if (!ok) {
                        allSuccess = false;
                        // 如果想要一旦失败就中断，可在这里 break；
                        // 但为了尽量删除选中的所有行，这里选择继续循环
                    }
                }
            }
            // 4. 根据删除结果弹窗
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
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "删除过程中发生错误：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // 5. 刷新表格，显示最新数据
        loadMemberTable();
    }

    private void btnSelectAction(ActionEvent e) {
        // TODO add your code here
        // 1. 取出关键字
        String key = txtName.getText().trim();

        // 2. 准备一个空的、不可编辑的表格模型
        DefaultTableModel model = createEmptyModel();

        try {
            // 3. 根据关键字决定查询全部或模糊查询
            List<Member> members;
            if (key.isEmpty() || NAME_PLACEHOLDER.equals(key)) {
                members = memberService.listAll();
            } else {
                members = memberService.searchByName(key);
            }

            // 4. 将查询结果逐行填充到模型
            for (Member m : members) {
                model.addRow(new Object[]{
                        m.getMemberID(),
                        m.getName(),
                        m.getGender(),
                        m.getBirthDate(),
                        m.getPhone(),
                        m.getEmail(),
                        m.getJoinDate(),
                        m.getLevelID(),
                        m.getPoints(),
                        m.getStatus()
                });
            }
        } catch (Exception ex) {
            // 5. 出错时弹框提示
            showError("查询失败", ex);
        }

        // 6. 把模型设置到表格，并调整列宽
        table1.setModel(model);
        adjustColumnWidth();
    }


    /**
     * =====================辅助方法区======================
     * **/

    /** 返回一个空的、带列名但不允许编辑的 DefaultTableModel */
    private DefaultTableModel createEmptyModel() {
        String[] columns = {
                "ID", "姓名", "性别", "出生日期",
                "电话", "邮箱", "加入日期",
                "等级ID", "积分", "状态"
        };
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /** 根据内容和标题自动调整 table1 各列的首选宽度 */
    private void adjustColumnWidth() {
        // 简单示例：只调 ID 和 姓名 这两列
        table1.getColumnModel().getColumn(0).setPreferredWidth(50);
        table1.getColumnModel().getColumn(1).setPreferredWidth(100);
        // 你可以根据实际情况再调其他列
    }

    /** 弹出错误对话框并在控制台打印堆栈 */
    private void showError(String message, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                this,
                message + "：\n" + ex.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE
        );
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        txtName = new JTextField();
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        btnSelect = new JButton();
        btnAdd = new JButton();
        btnUpdate = new JButton();
        btnDelete = new JButton();
        label1 = new JLabel();

        //======== this ========
        setLayout(null);
        add(txtName);
        txtName.setBounds(25, 35, 205, 35);

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(table1);
        }
        add(scrollPane1);
        scrollPane1.setBounds(25, 105, 580, 260);

        //---- btnSelect ----
        btnSelect.setText("\u67e5\u8be2");
        btnSelect.addActionListener(e -> btnSelectAction(e));
        add(btnSelect);
        btnSelect.setBounds(240, 35, 80, 35);

        //---- btnAdd ----
        btnAdd.setText("\u6dfb\u52a0");
        btnAdd.addActionListener(e -> btnAddAction(e));
        add(btnAdd);
        btnAdd.setBounds(330, 35, 80, 35);

        //---- btnUpdate ----
        btnUpdate.setText("\u4fee\u6539");
        btnUpdate.addActionListener(e -> btnUpdateAction(e));
        add(btnUpdate);
        btnUpdate.setBounds(420, 35, 75, 35);

        //---- btnDelete ----
        btnDelete.setText("\u5220\u9664");
        btnDelete.addActionListener(e -> btnDeleteAction(e));
        add(btnDelete);
        btnDelete.setBounds(505, 35, 75, 35);

        //---- label1 ----
        label1.setText("\u9009\u4e2d\u6570\u636e\u8fdb\u884c\u64cd\u4f5c");
        label1.setForeground(Color.gray);
        label1.setFont(label1.getFont().deriveFont(Font.BOLD|Font.ITALIC, label1.getFont().getSize() - 1f));
        add(label1);
        label1.setBounds(500, 85, 100, label1.getPreferredSize().height);

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
    private JTextField txtName;
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton btnSelect;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
