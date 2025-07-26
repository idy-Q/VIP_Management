/*
 * Created by JFormDesigner on 2025-06-06
 * 修订版：状态使用英文值, 去掉“员工ID”
 */

package io.github.idyq.view.Panel.AppointmentManage;

import io.github.idyq.dao.table.MemberDao;
import io.github.idyq.dao.table.ServiceItemDao;
import io.github.idyq.dao.table.impl.MemberDaoImpl;
import io.github.idyq.dao.table.impl.ServiceItemDaoImpl;
import io.github.idyq.model.entity.Member;
import io.github.idyq.model.entity.ServiceItem;
import io.github.idyq.model.entity.Appointment;
import io.github.idyq.service.AppointmentManage.AppointmentService;
import io.github.idyq.service.AppointmentManage.AppointmentServiceImpl;
import io.github.idyq.util.SqliteJdbcUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 修改预约对话框（修订版）
 *  • 会员姓名使用 JTextField 输入
 *  • 服务项目使用 JComboBox
 *  • 去掉“员工ID”
 *  • 状态使用英文值：Scheduled, Completed, Cancelled
 */
public class UpdateAppointmentDialog extends JDialog {
    private final AppointmentService apptService = new AppointmentServiceImpl();
    private final MemberDao memberDao = new MemberDaoImpl();
    private final ServiceItemDao serviceItemDao = new ServiceItemDaoImpl();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Appointment originalAppt;

    // 缓存所有会员和服务项目
    private List<Member> memberList;
    private List<ServiceItem> serviceItemList;

    public UpdateAppointmentDialog(Window owner, Appointment appt) {
        super(owner);
        this.originalAppt = appt;
        initComponents();
        loadMembersAndServiceItems();
        loadFieldsFromAppointment();
    }

    /** 加载会员列表和服务项目列表到内存及服务项目下拉框 */
    private void loadMembersAndServiceItems() {
        // 加载会员列表
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            memberList = memberDao.findAll(conn);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "加载会员列表失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            memberList = List.of();
        }

        // 加载服务项目列表到下拉框
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            serviceItemList = serviceItemDao.findAll(conn);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "加载服务项目列表失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            serviceItemList = List.of();
        }
        cmbServiceItem.removeAllItems();
        for (ServiceItem s : serviceItemList) {
            cmbServiceItem.addItem(s.getName());
        }
    }

    /** 把原始 appointment 的值预填到各输入控件中 */
    private void loadFieldsFromAppointment() {
        // 预约ID（只读）
        txtAppointmentID.setText(String.valueOf(originalAppt.getAppointmentID()));

        // 会员姓名预填：如果 memberID 不为 null，就在 memberList 中找到对应姓名
        if (originalAppt.getMemberID() != null) {
            String foundName = "";
            for (Member m : memberList) {
                if (m.getMemberID() == originalAppt.getMemberID()) {
                    foundName = m.getName();
                    break;
                }
            }
            txtMemberName.setText(foundName);
        } else {
            txtMemberName.setText("");
        }

        // 服务项目预填
        for (ServiceItem s : serviceItemList) {
            if (s.getServiceItemID() == originalAppt.getServiceItemID()) {
                cmbServiceItem.setSelectedItem(s.getName());
                break;
            }
        }

        // 预约时间预填
        if (originalAppt.getAppointmentDate() != null) {
            txtAppointmentDate.setText(originalAppt.getAppointmentDate().format(dtf));
        } else {
            txtAppointmentDate.setText("");
        }

        // 状态预填（要使用英文值）
        cmbStatus.setSelectedItem(originalAppt.getStatus());
    }

    /** 点击“确认修改”后的处理逻辑 */
    private void btnConfirmAction(ActionEvent e) {
        // 1. 读取并校验“会员姓名”（可留空）
        Integer memberID = null;
        String memberName = txtMemberName.getText().trim();
        if (!memberName.isEmpty()) {
            boolean found = false;
            for (Member m : memberList) {
                if (m.getName().equals(memberName)) {
                    memberID = m.getMemberID();
                    found = true;
                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(
                        this,
                        "所填会员姓名未找到，请确认后重试",
                        "提示",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        }

        // 2. 读取并校验“服务项目”（必选）
        String selServiceName = (String) cmbServiceItem.getSelectedItem();
        if (selServiceName == null || selServiceName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "请从“服务项目”下拉框中选择一个项目",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        Integer serviceItemID = null;
        for (ServiceItem s : serviceItemList) {
            if (s.getName().equals(selServiceName)) {
                serviceItemID = s.getServiceItemID();
                break;
            }
        }
        if (serviceItemID == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "所选服务项目不存在，请重新选择",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 3. 读取并校验“预约时间”（必填，格式 yyyy-MM-dd HH:mm）
        String apptDateStr = txtAppointmentDate.getText().trim();
        if (apptDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "请填写“预约时间”（格式：YYYY-MM-DD HH:mm）",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        LocalDateTime apptDate;
        try {
            apptDate = LocalDateTime.parse(apptDateStr, dtf);
        } catch (DateTimeParseException ex2) {
            JOptionPane.showMessageDialog(
                    this,
                    "“预约时间”格式不正确，应为 YYYY-MM-DD HH:mm",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 4. 读取并校验“状态”（必选，且仅限英文值）
        String status = (String) cmbStatus.getSelectedItem();
        if (status == null || status.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "请在“状态”下拉框中选择一个值",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (!status.equals("Scheduled") && !status.equals("Completed") && !status.equals("Cancelled")) {
            JOptionPane.showMessageDialog(
                    this,
                    "状态只能是 Scheduled、Completed 或 Cancelled",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 5. 将修改后的字段写回 originalAppt（保留 appointmentID、createdAt 不变）
        originalAppt.setMemberID(memberID);
        originalAppt.setServiceItemID(serviceItemID);
        originalAppt.setAppointmentDate(apptDate);
        originalAppt.setStatus(status);
        // createdAt 保持原值

        // 6. 调用 Service.update(...)
        try {
            boolean ok = apptService.update(originalAppt);
            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "修改成功",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "修改失败，请重试",
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "修改时发生错误：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void btnCancelAction(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        lblTitle = new JLabel();
        lblAppointmentID = new JLabel();
        txtAppointmentID = new JTextField();
        lblMemberName = new JLabel();
        txtMemberName = new JTextField();
        lblServiceItem = new JLabel();
        cmbServiceItem = new JComboBox<>();
        lblAppointmentDate = new JLabel();
        txtAppointmentDate = new JTextField();
        lblStatus = new JLabel();
        cmbStatus = new JComboBox<>();
        btnConfirm = new JButton();
        btnCancel = new JButton();

        //======== this ========
        setTitle("修改预约");
        setModal(true);
        setResizable(false);
        setPreferredSize(new Dimension(420, 380));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- lblTitle ----
        lblTitle.setText("修改预约");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD | Font.ITALIC, lblTitle.getFont().getSize() + 8f));
        contentPane.add(lblTitle);
        lblTitle.setBounds(140, 15, 140, 30);

        //---- lblAppointmentID ----
        lblAppointmentID.setText("预约 ID：");
        {
            Font f = lblAppointmentID.getFont();
            lblAppointmentID.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblAppointmentID);
        lblAppointmentID.setBounds(40, 65, 80, 30);

        //---- txtAppointmentID ----
        txtAppointmentID.setEditable(false);
        contentPane.add(txtAppointmentID);
        txtAppointmentID.setBounds(130, 65, 220, 30);

        //---- lblMemberName ----
        lblMemberName.setText("会员姓名：");
        {
            Font f = lblMemberName.getFont();
            lblMemberName.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblMemberName);
        lblMemberName.setBounds(40, 105, 80, 30);

        //---- txtMemberName ----
        contentPane.add(txtMemberName);
        txtMemberName.setBounds(130, 105, 220, 30);

        //---- lblServiceItem ----
        lblServiceItem.setText("服务项目：");
        {
            Font f = lblServiceItem.getFont();
            lblServiceItem.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblServiceItem);
        lblServiceItem.setBounds(40, 145, 80, 30);

        //---- cmbServiceItem ----
        contentPane.add(cmbServiceItem);
        cmbServiceItem.setBounds(130, 145, 220, 30);

        //---- lblAppointmentDate ----
        lblAppointmentDate.setText("预约时间：");
        {
            Font f = lblAppointmentDate.getFont();
            lblAppointmentDate.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblAppointmentDate);
        lblAppointmentDate.setBounds(40, 185, 80, 30);

        //---- txtAppointmentDate ----
        contentPane.add(txtAppointmentDate);
        txtAppointmentDate.setBounds(130, 185, 220, 30);

        //---- lblStatus ----
        lblStatus.setText("状态：");
        {
            Font f = lblStatus.getFont();
            lblStatus.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblStatus);
        lblStatus.setBounds(40, 225, 80, 30);

        //---- cmbStatus ----
        cmbStatus.setModel(new DefaultComboBoxModel<>(new String[]{
                "Scheduled", "Completed", "Cancelled"
        }));
        contentPane.add(cmbStatus);
        cmbStatus.setBounds(130, 225, 220, 30);

        //---- btnConfirm ----
        btnConfirm.setText("确认修改");
        {
            Font f = btnConfirm.getFont();
            btnConfirm.setFont(f.deriveFont(Font.BOLD, f.getSize() + 2f));
        }
        btnConfirm.addActionListener(e -> btnConfirmAction(e));
        contentPane.add(btnConfirm);
        btnConfirm.setBounds(100, 275, 90, 40);

        //---- btnCancel ----
        btnCancel.setText("取消");
        {
            Font f = btnCancel.getFont();
            btnCancel.setFont(f.deriveFont(Font.BOLD, f.getSize() + 2f));
        }
        btnCancel.addActionListener(e -> btnCancelAction(e));
        contentPane.add(btnCancel);
        btnCancel.setBounds(220, 275, 90, 40);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width  = Math.max(bounds.x + bounds.width,  preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width  += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblTitle;
    private JLabel lblAppointmentID;
    private JTextField txtAppointmentID;
    private JLabel lblMemberName;
    private JTextField txtMemberName;
    private JLabel lblServiceItem;
    private JComboBox<String> cmbServiceItem;
    private JLabel lblAppointmentDate;
    private JTextField txtAppointmentDate;
    private JLabel lblStatus;
    private JComboBox<String> cmbStatus;
    private JButton btnConfirm;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
