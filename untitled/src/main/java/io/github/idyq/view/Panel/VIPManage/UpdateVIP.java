/*
 * Created by JFormDesigner on Sun Jun 01 17:36:18 CST 2025
 */

package io.github.idyq.view.Panel.VIPManage;

import java.awt.event.*;

import io.github.idyq.dao.table.MemberDao;
import io.github.idyq.dao.table.impl.MemberDaoImpl;
import io.github.idyq.model.entity.Member;
import io.github.idyq.model.entity.MemberLevel;
import io.github.idyq.service.VIP.MemberLevelService;
import io.github.idyq.service.VIP.MemberLevelServiceImpl;
import io.github.idyq.util.SqliteJdbcUtil;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;

/**
 * @author guox100
 */
public class UpdateVIP extends JDialog {
    private final Member currentMember;

    public UpdateVIP(Window owner, Member currentMember) {
        super(owner);
        this.currentMember = currentMember;
        initComponents();
        prefillForm();
    }

    // 确认功能
    private void button1Action(ActionEvent e) {
        // TODO add your code here
        // 1. 校验必填：姓名、电话不能为空
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "姓名不能为空",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "电话不能为空",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. 从表单取值，覆盖 currentMember 中对应字段
        currentMember.setName(name);
        currentMember.setPhone(phone);
        currentMember.setEmail(txtEmail.getText().trim());
        currentMember.setStatus((String) cmbStatus.getSelectedItem());

        // 性别
        currentMember.setGender((String) cmbGender.getSelectedItem());

        // 出生日期：尝试解析（或留空）
        String bdText = txtBirthDate.getText().trim();
        if (!bdText.isEmpty() && !bdText.equals("YYYY-MM-DD")) {
            try {
                currentMember.setBirthDate(LocalDate.parse(bdText));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "出生日期格式不正确，请使用 YYYY-MM-DD",
                        "提示",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        } else {
            currentMember.setBirthDate(null);
        }

        // 会员等级
        Object levelObj = cmbLevel.getSelectedItem();
        if (levelObj instanceof Integer) {
            currentMember.setLevelID((Integer) levelObj);
        } else if (levelObj instanceof String) {
            try {
                currentMember.setLevelID(Integer.parseInt((String) levelObj));
            } catch (NumberFormatException ignored) {
                // 不合法时保持原值
            }
        }

        // 积分
        String ptsText = txtPoints.getText().trim();
        if (!ptsText.isEmpty()) {
            try {
                currentMember.setPoints(Integer.parseInt(ptsText));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "积分请输入整数",
                        "提示",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        } else {
            currentMember.setPoints(0);
        }

        // 3. 执行数据库更新
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            MemberDao dao = new MemberDaoImpl();
            boolean success = dao.update(conn, currentMember);
            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "会员信息已更新",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
                this.dispose();  // 关闭对话框
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "更新失败，未修改任何记录",
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "更新出错：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // 取消功能
    private void button2Action(ActionEvent e) {
        // TODO add your code here
        this.dispose();
    }

    // 预填充各个表单项
    private void prefillForm() {
        // ID 只读
        textFieldID.setText(String.valueOf(currentMember.getMemberID()));
        textFieldID.setEditable(false);

        txtName.setText(currentMember.getName());
        txtPhone.setText(currentMember.getPhone());
        txtEmail.setText(currentMember.getEmail());

        cmbStatus.setSelectedItem(currentMember.getStatus());

        // 性别
        if (currentMember.getGender() != null) {
            cmbGender.setSelectedItem(currentMember.getGender());
        } else {
            cmbGender.setSelectedItem("Other");
        }

        // 出生日期
        if (currentMember.getBirthDate() != null) {
            txtBirthDate.setText(currentMember.getBirthDate().toString());
        } else {
            txtBirthDate.setText("YYYY-MM-DD");
        }

        // 会员等级
        int lvlID = currentMember.getLevelID();
        // 如果下拉列表有该值，就选中，否则默认选第一个
        ComboBoxModel<?> model = cmbLevel.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Object item = model.getElementAt(i);
            if (item instanceof Integer && ((Integer) item) == lvlID) {
                cmbLevel.setSelectedIndex(i);
                break;
            } else if (item instanceof String) {
                try {
                    if (Integer.parseInt((String) item) == lvlID) {
                        cmbLevel.setSelectedIndex(i);
                        break;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // 积分
        txtPoints.setText(String.valueOf(currentMember.getPoints()));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        label1 = new JLabel();
        label2 = new JLabel();
        txtName = new JTextField();
        label3 = new JLabel();
        txtPhone = new JTextField();
        label4 = new JLabel();
        txtEmail = new JTextField();
        label5 = new JLabel();
        cmbStatus = new JComboBox<>();
        label10 = new JLabel();
        textFieldID = new JTextField();
        label11 = new JLabel();
        cmbGender = new JComboBox<>();
        label12 = new JLabel();
        txtBirthDate = new JTextField();
        label13 = new JLabel();
        cmbLevel = new JComboBox();
        try {
            MemberLevelService levelService = new MemberLevelServiceImpl();
            for (MemberLevel lv : levelService.listAll()) {
                cmbLevel.addItem(lv.getLevelID());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "加载会员等级失败：" + ex.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE
            );
        }
        label14 = new JLabel();
        txtPoints = new JTextField();
        button1 = new JButton();
        button2 = new JButton();

        //======== this ========
        setPreferredSize(new Dimension(595, 390));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- label1 ----
        label1.setText("\u5220\u9664\u539f\u6709\u503c\u5373\u53ef\u4fee\u6539");
        label1.setFont(label1.getFont().deriveFont(Font.BOLD|Font.ITALIC, label1.getFont().getSize() + 4f));
        label1.setForeground(new Color(0x999999));
        contentPane.add(label1);
        label1.setBounds(5, 10, 155, 40);

        //---- label2 ----
        label2.setText("\u59d3\u540d\uff1a");
        label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 2f));
        contentPane.add(label2);
        label2.setBounds(20, 95, 70, 35);
        contentPane.add(txtName);
        txtName.setBounds(95, 95, 150, 34);

        //---- label3 ----
        label3.setText("\u7535\u8bdd\uff1a");
        label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 2f));
        contentPane.add(label3);
        label3.setBounds(20, 135, 70, 35);
        contentPane.add(txtPhone);
        txtPhone.setBounds(95, 135, 150, 34);

        //---- label4 ----
        label4.setText("\u90ae\u7bb1\uff1a");
        label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() + 2f));
        contentPane.add(label4);
        label4.setBounds(20, 175, 70, 35);
        contentPane.add(txtEmail);
        txtEmail.setBounds(95, 175, 150, 34);

        //---- label5 ----
        label5.setText("\u72b6\u6001\uff1a");
        label5.setFont(label5.getFont().deriveFont(label5.getFont().getSize() + 2f));
        contentPane.add(label5);
        label5.setBounds(20, 215, 70, 35);

        //---- cmbStatus ----
        cmbStatus.setModel(new DefaultComboBoxModel<>(new String[] {
            "Active",
            "Inactive",
            "Frozen"
        }));
        contentPane.add(cmbStatus);
        cmbStatus.setBounds(95, 215, 150, 35);

        //---- label10 ----
        label10.setText("\u4f1a\u5458ID\uff1a");
        label10.setFont(label10.getFont().deriveFont(label10.getFont().getStyle() | Font.ITALIC, label10.getFont().getSize() + 3f));
        contentPane.add(label10);
        label10.setBounds(225, 20, 75, 35);
        contentPane.add(textFieldID);
        textFieldID.setBounds(290, 20, 95, 35);

        //---- label11 ----
        label11.setText("\u6027\u522b\uff1a");
        label11.setFont(label11.getFont().deriveFont(label11.getFont().getSize() + 2f));
        contentPane.add(label11);
        label11.setBounds(275, 95, 80, 30);

        //---- cmbGender ----
        cmbGender.setModel(new DefaultComboBoxModel<>(new String[] {
            "M",
            "F",
            "Other"
        }));
        contentPane.add(cmbGender);
        cmbGender.setBounds(375, 95, 168, 35);

        //---- label12 ----
        label12.setText("\u51fa\u751f\u65e5\u671f\uff1a");
        label12.setFont(label12.getFont().deriveFont(label12.getFont().getSize() + 2f));
        contentPane.add(label12);
        label12.setBounds(275, 140, 80, 30);

        //---- txtBirthDate ----
        txtBirthDate.setText("YYYY-MM-DD");
        contentPane.add(txtBirthDate);
        txtBirthDate.setBounds(375, 135, 168, 35);

        //---- label13 ----
        label13.setText("\u7b49\u7ea7ID\uff1a");
        label13.setFont(label13.getFont().deriveFont(label13.getFont().getSize() + 2f));
        contentPane.add(label13);
        label13.setBounds(275, 180, 80, 30);

        //---- cmbLevel ----
        cmbLevel.setPreferredSize(new Dimension(565, 390));
        contentPane.add(cmbLevel);
        cmbLevel.setBounds(375, 175, 168, 35);

        //---- label14 ----
        label14.setText("\u79ef\u5206\uff1a");
        label14.setFont(label14.getFont().deriveFont(label14.getFont().getSize() + 2f));
        contentPane.add(label14);
        label14.setBounds(275, 220, 80, 30);

        //---- txtPoints ----
        txtPoints.setText("0");
        contentPane.add(txtPoints);
        txtPoints.setBounds(375, 215, 168, 35);

        //---- button1 ----
        button1.setText("\u786e\u8ba4");
        button1.setFont(button1.getFont().deriveFont(button1.getFont().getStyle() | Font.BOLD, button1.getFont().getSize() + 3f));
        button1.addActionListener(e -> button1Action(e));
        contentPane.add(button1);
        button1.setBounds(75, 285, 145, 40);

        //---- button2 ----
        button2.setText("\u53d6\u6d88");
        button2.setFont(button2.getFont().deriveFont(button2.getFont().getStyle() | Font.BOLD, button2.getFont().getSize() + 3f));
        button2.addActionListener(e -> button2Action(e));
        contentPane.add(button2);
        button2.setBounds(310, 285, 145, 40);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel label1;
    private JLabel label2;
    private JTextField txtName;
    private JLabel label3;
    private JTextField txtPhone;
    private JLabel label4;
    private JTextField txtEmail;
    private JLabel label5;
    private JComboBox<String> cmbStatus;
    private JLabel label10;
    private JTextField textFieldID;
    private JLabel label11;
    private JComboBox<String> cmbGender;
    private JLabel label12;
    private JTextField txtBirthDate;
    private JLabel label13;
    private JComboBox cmbLevel;
    private JLabel label14;
    private JTextField txtPoints;
    private JButton button1;
    private JButton button2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
