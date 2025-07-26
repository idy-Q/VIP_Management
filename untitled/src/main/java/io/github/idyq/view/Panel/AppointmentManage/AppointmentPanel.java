/*
 * Created by JFormDesigner on Fri Jun 06 00:02:24 CST 2025
 */

package io.github.idyq.view.Panel.AppointmentManage;

import io.github.idyq.dao.table.MemberDao;
import io.github.idyq.dao.table.ServiceItemDao;
import io.github.idyq.dao.table.impl.MemberDaoImpl;
import io.github.idyq.dao.table.impl.ServiceItemDaoImpl;
import io.github.idyq.model.entity.Appointment;
import io.github.idyq.model.entity.Member;
import io.github.idyq.model.entity.ServiceItem;
import io.github.idyq.service.AppointmentManage.AppointmentService;
import io.github.idyq.service.AppointmentManage.AppointmentServiceImpl;
import io.github.idyq.util.SqliteJdbcUtil;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.List;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.*;

/**
 * @author guox100
 */
public class AppointmentPanel extends JPanel {
    private final AppointmentService apptService = new AppointmentServiceImpl();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MemberDao memberDao = new MemberDaoImpl();
    private final ServiceItemDao serviceItemDao = new ServiceItemDaoImpl();

    public AppointmentPanel() {
        initComponents();
        // 构造完后先加载全部预约
        loadAppointmentTable();
    }

    /**
     * 查询按钮的逻辑：
     * 如果搜索框为空，则等同于 loadAppointmentTable()；否则调用搜索方法并刷新表格
     */
    private void btnSearchAction(ActionEvent e) {
        // TODO add your code here
        String keyword = txtSearchKey.getText().trim();
        List<Appointment> list;
        try {
            if (keyword.isEmpty()) {
                list = apptService.listAll();
            } else {
                list = apptService.search(keyword);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "查询失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        refreshTableModel(list);
    }

    private void btnAddAction(ActionEvent e) {
        // TODO add your code here
        AddAppointmentDialog dialog = new AddAppointmentDialog(
                SwingUtilities.getWindowAncestor(this)
        );
        dialog.setVisible(true);
        // 对话框关闭后，刷新表格
        loadAppointmentTable();
    }

    private void btnUpdateAction(ActionEvent e) {
        // TODO add your code here
        int row = tableAppointments.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先选中一行要修改的预约",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        DefaultTableModel model = (DefaultTableModel) tableAppointments.getModel();
        Integer apptId = (Integer) model.getValueAt(row, 0);

        Appointment appt;
        try {
            appt = apptService.findById(apptId);
            if (appt == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "未找到 ID=" + apptId + " 的预约信息",
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "读取预约数据失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        UpdateAppointmentDialog dialog = new UpdateAppointmentDialog(
                SwingUtilities.getWindowAncestor(this),
                appt
        );
        dialog.setVisible(true);
        loadAppointmentTable();
    }

    private void btnDeleteAction(ActionEvent e) {
        // TODO add your code here
        int row = tableAppointments.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "请先选中一行要删除的预约",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        DefaultTableModel model = (DefaultTableModel) tableAppointments.getModel();
        Integer apptId = (Integer) model.getValueAt(row, 0);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "确认要删除预约 ID = " + apptId + " 吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean ok = apptService.delete(apptId);
            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "删除成功",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
                loadAppointmentTable();
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


    /**
     * 加载所有预约并刷新表格
     */
    private void loadAppointmentTable() {
        List<Appointment> list;
        try {
            list = apptService.listAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "加载预约数据失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        // 使用通用的刷新表格方法
        refreshTableModel(list);
    }

    /**
     * 把给定的预约列表渲染到表格中
     */
    private void refreshTableModel(List<Appointment> list) {
        // 列名改为：预约ID、会员姓名、服务项目（名称）、预约时间、状态
        String[] columns = {
                "预约ID", "会员姓名", "服务项目", "预约时间", "状态"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        // 对于每一个 Appointment，分别去 MemberDao、ServiceItemDao 拿名称
        for (Appointment ap : list) {
            String memberName = "";
            if (ap.getMemberID() != null) {
                try (Connection conn = SqliteJdbcUtil.getConnection()) {
                    Member m = memberDao.findById(conn, ap.getMemberID());
                    if (m != null) {
                        memberName = m.getName();
                    } else {
                        // 找不到对应会员，就显示 ID 本身
                        memberName = String.valueOf(ap.getMemberID());
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    memberName = String.valueOf(ap.getMemberID());
                }
            }

            String serviceName = "";
            try (Connection conn = SqliteJdbcUtil.getConnection()) {
                ServiceItem si = serviceItemDao.findById(conn, ap.getServiceItemID());
                if (si != null) {
                    serviceName = si.getName();
                } else {
                    serviceName = String.valueOf(ap.getServiceItemID());
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                serviceName = String.valueOf(ap.getServiceItemID());
            }

            String apptTime = ap.getAppointmentDate() != null
                    ? ap.getAppointmentDate().format(dtf)
                    : "";

            model.addRow(new Object[]{
                    ap.getAppointmentID(),
                    memberName,
                    serviceName,
                    apptTime,
                    ap.getStatus()
            });
        }

        tableAppointments.setModel(model);
        if (tableAppointments.getColumnModel().getColumnCount() > 0) {
            tableAppointments.getColumnModel().getColumn(0).setPreferredWidth(60);
            tableAppointments.getColumnModel().getColumn(1).setPreferredWidth(100);
            tableAppointments.getColumnModel().getColumn(2).setPreferredWidth(150);
            tableAppointments.getColumnModel().getColumn(3).setPreferredWidth(150);
            tableAppointments.getColumnModel().getColumn(4).setPreferredWidth(80);
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        panelTop = new JPanel();
        lblSearch = new JLabel();
        txtSearchKey = new JTextField();
        btnSearch = new JButton();
        btnAdd = new JButton();
        btnUpdate = new JButton();
        btnDelete = new JButton();
        lblTip = new JLabel();
        scrollPaneAppointments = new JScrollPane();
        tableAppointments = new JTable();

        //======== this ========
        setLayout(null);

        //======== panelTop ========
        {
            panelTop.setPreferredSize(new Dimension(790, 40));
            panelTop.setLayout(null);

            //---- lblSearch ----
            lblSearch.setText("\u4f1a\u5458/\u624b\u673a\u53f7/\u9884\u7ea6ID\uff1a");
            lblSearch.setPreferredSize(new Dimension(120, 24));
            panelTop.add(lblSearch);
            lblSearch.setBounds(new Rectangle(new Point(10, 8), lblSearch.getPreferredSize()));

            //---- txtSearchKey ----
            txtSearchKey.setPreferredSize(new Dimension(180, 24));
            panelTop.add(txtSearchKey);
            txtSearchKey.setBounds(new Rectangle(new Point(130, 8), txtSearchKey.getPreferredSize()));

            //---- btnSearch ----
            btnSearch.setText("\u67e5\u8be2");
            btnSearch.setPreferredSize(new Dimension(70, 28));
            btnSearch.addActionListener(e -> btnSearchAction(e));
            panelTop.add(btnSearch);
            btnSearch.setBounds(new Rectangle(new Point(320, 6), btnSearch.getPreferredSize()));

            //---- btnAdd ----
            btnAdd.setText("\u65b0\u589e");
            btnAdd.setPreferredSize(new Dimension(70, 28));
            btnAdd.addActionListener(e -> btnAddAction(e));
            panelTop.add(btnAdd);
            btnAdd.setBounds(410, 6, 70, 28);

            //---- btnUpdate ----
            btnUpdate.setText("\u4fee\u6539");
            btnUpdate.setPreferredSize(new Dimension(70, 28));
            btnUpdate.addActionListener(e -> btnUpdateAction(e));
            panelTop.add(btnUpdate);
            btnUpdate.setBounds(490, 6, 70, 28);

            //---- btnDelete ----
            btnDelete.setText("\u5220\u9664");
            btnDelete.setPreferredSize(new Dimension(70, 28));
            btnDelete.addActionListener(e -> btnDeleteAction(e));
            panelTop.add(btnDelete);
            btnDelete.setBounds(570, 6, 70, 28);

            //---- lblTip ----
            lblTip.setText("\u9009\u4e2d\u6570\u636e\u8fdb\u884c\u64cd\u4f5c");
            lblTip.setFont(new Font("Microsoft YaHei UI", Font.BOLD | Font.ITALIC, 11));
            lblTip.setForeground(new Color(0x666666));
            panelTop.add(lblTip);
            lblTip.setBounds(650, 10, 120, 20);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panelTop.getComponentCount(); i++) {
                    Rectangle bounds = panelTop.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panelTop.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panelTop.setMinimumSize(preferredSize);
                panelTop.setPreferredSize(preferredSize);
            }
        }
        add(panelTop);
        panelTop.setBounds(new Rectangle(new Point(5, 5), panelTop.getPreferredSize()));

        //======== scrollPaneAppointments ========
        {

            //---- tableAppointments ----
            tableAppointments.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                    "\u9884\u7ea6ID", "\u4f1a\u5458\u59d3\u540d", "\u670d\u52a1\u9879\u76ee", "\u9884\u7ea6\u65f6\u95f4", "\u72b6\u6001"
                }
            ));
            scrollPaneAppointments.setViewportView(tableAppointments);
        }
        add(scrollPaneAppointments);
        scrollPaneAppointments.setBounds(5, 55, 790, 350);

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
    private JLabel lblSearch;
    private JTextField txtSearchKey;
    private JButton btnSearch;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JLabel lblTip;
    private JScrollPane scrollPaneAppointments;
    private JTable tableAppointments;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
