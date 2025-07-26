package io.github.idyq.view.Panel.ServiceItem;

import io.github.idyq.model.entity.ServiceItem;
import io.github.idyq.service.serviceitem.ServiceItemService;
import io.github.idyq.service.serviceitem.ServiceItemServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * “服务项目”管理面板
 */
public class ServiceItemPanel extends JPanel {
    // Service 层，用于与数据库交互
    private final ServiceItemService itemService = new ServiceItemServiceImpl();

    public ServiceItemPanel() {
        initComponents();
        loadServiceItemTable(); // 刚打开面板时自动加载一次
    }

    /** 点击“刷新”时重新加载表格 */
    private void btnRefreshAction(ActionEvent e) {
        loadServiceItemTable();
    }

    /** 点击“添加项目”时弹出对话框 */
    private void btnAddAction(ActionEvent e) {
        AddServiceItemDialog dialog = new AddServiceItemDialog(
                SwingUtilities.getWindowAncestor(this)
        );
        dialog.setVisible(true);
        // 如果对话框里新增成功，就刷新表格
        loadServiceItemTable();
    }

    /** 点击“修改项目”时，先检查是否选中一行，然后点击修改 */
    private void btnUpdateAction(ActionEvent e) {
        int row = tableItems.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先选中一行要修改的项目",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        // 从表格模型获取选中行第 0 列的 serviceItemID
        DefaultTableModel model = (DefaultTableModel) tableItems.getModel();
        Integer serviceItemID = (Integer) model.getValueAt(row, 0);

        ServiceItem item;
        try {
            item = itemService.findById(serviceItemID);
            if (item == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "未找到 ID=" + serviceItemID + " 的服务项目",
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "读取项目数据失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        UpdateServiceItemDialog dialog = new UpdateServiceItemDialog(
                SwingUtilities.getWindowAncestor(this),
                item
        );
        dialog.setVisible(true);
        loadServiceItemTable();
    }

    /** 点击“删除项目”时，先确认再删除并刷新 */
    private void btnDeleteAction(ActionEvent e) {
        int row = tableItems.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先选中一行要删除的项目",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        DefaultTableModel model = (DefaultTableModel) tableItems.getModel();
        Integer serviceItemID = (Integer) model.getValueAt(row, 0);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "确认要删除项目 ID = " + serviceItemID + " 吗？",
                "删除确认",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean ok = itemService.delete(serviceItemID);
            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "删除成功",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
                loadServiceItemTable();
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
                    "删除时发生错误：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** 从数据库读取所有 ServiceItem 并填充到表格里 */
    private void loadServiceItemTable() {
        // 定义列名
        String[] columns = {
                "项目ID", "项目名称", "描述", "价格(元)", "时长(分钟)"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        try {
            List<ServiceItem> list = itemService.listAll();
            for (ServiceItem si : list) {
                model.addRow(new Object[] {
                        si.getServiceItemID(),
                        si.getName(),
                        si.getDescription(),
                        si.getPrice(),
                        si.getDuration()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "加载服务项目失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        tableItems.setModel(model);
        if (tableItems.getColumnModel().getColumnCount() > 0) {
            tableItems.getColumnModel().getColumn(0).setPreferredWidth(60);
            tableItems.getColumnModel().getColumn(1).setPreferredWidth(120);
            tableItems.getColumnModel().getColumn(2).setPreferredWidth(200);
            tableItems.getColumnModel().getColumn(3).setPreferredWidth(80);
            tableItems.getColumnModel().getColumn(4).setPreferredWidth(80);
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
        scrollPaneTable = new JScrollPane();
        tableItems = new JTable();

        //======== this ========
        setLayout(null);

        //======== panelTop ========
        {
            panelTop.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

            //---- btnRefresh ----
            btnRefresh.setText("刷新");
            btnRefresh.setPreferredSize(new Dimension(80, 30));
            btnRefresh.addActionListener(e -> btnRefreshAction(e));
            panelTop.add(btnRefresh);

            //---- btnAdd ----
            btnAdd.setText("添加项目");
            btnAdd.setPreferredSize(new Dimension(120, 30));
            btnAdd.addActionListener(e -> btnAddAction(e));
            panelTop.add(btnAdd);

            //---- btnUpdate ----
            btnUpdate.setText("修改项目");
            btnUpdate.setPreferredSize(new Dimension(120, 30));
            btnUpdate.addActionListener(e -> btnUpdateAction(e));
            panelTop.add(btnUpdate);

            //---- btnDelete ----
            btnDelete.setText("删除项目");
            btnDelete.setPreferredSize(new Dimension(120, 30));
            btnDelete.addActionListener(e -> btnDeleteAction(e));
            panelTop.add(btnDelete);

            //---- labelTip ----
            labelTip.setText("选中数据进行操作");
            labelTip.setPreferredSize(new Dimension(120, 20));
            labelTip.setFont(labelTip.getFont().deriveFont(Font.BOLD | Font.ITALIC, labelTip.getFont().getSize() - 2f));
            labelTip.setForeground(Color.gray);
            panelTop.add(labelTip);
        }
        add(panelTop);
        panelTop.setBounds(new Rectangle(new Point(5, 5), panelTop.getPreferredSize()));

        //======== scrollPaneTable ========
        {
            scrollPaneTable.setPreferredSize(new Dimension(600, 350));
            scrollPaneTable.setViewportView(tableItems);
        }
        add(scrollPaneTable);
        scrollPaneTable.setBounds(new Rectangle(new Point(5, 50), scrollPaneTable.getPreferredSize()));

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < getComponentCount(); i++) {
                Rectangle bounds = getComponent(i).getBounds();
                preferredSize.width  = Math.max(bounds.x + bounds.width,  preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = getInsets();
            preferredSize.width  += insets.right;
            preferredSize.height += insets.bottom;
            setMinimumSize(preferredSize);
            setPreferredSize(preferredSize);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panelTop;
    private JButton btnRefresh;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JLabel labelTip;
    private JScrollPane scrollPaneTable;
    private JTable tableItems;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
