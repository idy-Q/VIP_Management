/*
 * Created by JFormDesigner on Fri May 30 00:01:28 CST 2025
 */

package io.github.idyq.view.Panel.VIPManage;

import io.github.idyq.dao.table.MemberDao;
import io.github.idyq.dao.table.impl.MemberDaoImpl;
import io.github.idyq.model.entity.*;
import io.github.idyq.service.VIP.MemberLevelService;
import io.github.idyq.service.VIP.MemberLevelServiceImpl;
import io.github.idyq.util.SqliteJdbcUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;

public class AddVIP extends JDialog {
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            AddVIP a = new AddVIP((Window) null);
//            a.setVisible(true);
//            a.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        });
//    }

    public AddVIP(Window owner) {
        super(owner);
        initComponents();
    }

    // 高级选项
    private void chkAdvancedAction(ActionEvent e) {
        // TODO add your code here
        boolean show = chkAdvanced.isSelected();
        panelAdvanced.setVisible(show);

        // 根据是否展开高级选项来切换窗口大小
        if (show) {
            setSize(new Dimension(400, 590));
        } else {
            setSize(new Dimension(400, 380));
        }
        // 重新居中到父窗口
        setLocationRelativeTo(getOwner());
    }

    // 确认按钮
    private void button1Action(ActionEvent e) {
        // TODO add your code here
        // 1. 校验必填项
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "姓名不能为空",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "电话不能为空",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. 构造 Member 对象，并赋值
        Member m = new Member();
        m.setName(name);
        m.setPhone(phone);
        m.setEmail(txtEmail.getText().trim());
        m.setStatus((String) cmbStatus.getSelectedItem());
        m.setJoinDate(LocalDate.now());

        // 默认值：如果没有展开“高级选项”，则直接使用默认
        m.setGender("Other");
        m.setBirthDate(null);
        m.setLevelID(1);
        m.setPoints(0);

        // 3. 如果“高级选项”面板可见，则覆盖默认值
        if (chkAdvanced.isSelected()) {
            // 性别
            m.setGender((String) cmbGender.getSelectedItem());

            // 出生日期：尝试解析用户输入的格式（YYYY-MM-DD）
            String bdText = txtBirthDate.getText().trim();
            if (!bdText.isEmpty() && !bdText.equals("YYYY-MM-DD")) {
                try {
                    LocalDate bd = LocalDate.parse(bdText);
                    m.setBirthDate(bd);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "出生日期格式不正确，请使用 YYYY-MM-DD",
                            "提示",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            // 等级 ID
            Object levelObj = cmbLevel.getSelectedItem();
            if (levelObj instanceof Integer) {
                m.setLevelID((Integer) levelObj);
            } else if (levelObj instanceof String) {
                // 如果下拉里意外存的是 String（例如“1”），也尝试解析
                try {
                    m.setLevelID(Integer.parseInt((String) levelObj));
                } catch (NumberFormatException ex) {
                    m.setLevelID(1);
                }
            }

            // 积分
            String pointsText = txtPoints.getText().trim();
            if (!pointsText.isEmpty()) {
                try {
                    int pts = Integer.parseInt(pointsText);
                    m.setPoints(pts);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "积分请输入整数",
                            "提示",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }
        }

        // 4. 插入到数据库
        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            MemberDao dao = new MemberDaoImpl();
            boolean ok = dao.insert(conn, m);
            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "新增会员成功",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
                this.dispose(); // 关闭对话框
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "新增会员失败，未插入任何数据",
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "新增会员出错：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // 取消按钮
    private void button2Action(ActionEvent e) {
        // TODO add your code here
        this.dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        label1 = new JLabel();
        txtName = new JTextField();
        txtPhone = new JTextField();
        label2 = new JLabel();
        txtEmail = new JTextField();
        label3 = new JLabel();
        label4 = new JLabel();
        button1 = new JButton();
        button2 = new JButton();
        label5 = new JLabel();
        cmbStatus = new JComboBox<>();
        chkAdvanced = new JCheckBox();
        panelAdvanced = new JPanel();
        label6 = new JLabel();
        cmbGender = new JComboBox<>();
        label7 = new JLabel();
        txtBirthDate = new JTextField();
        label8 = new JLabel();
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
        label9 = new JLabel();
        txtPoints = new JTextField();

        //======== this ========
        setTitle("\u65b0\u589e\u4f1a\u5458");
        setPreferredSize(new Dimension(400, 380));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- label1 ----
        label1.setText("\u59d3\u540d\uff1a");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 2f));
        contentPane.add(label1);
        label1.setBounds(75, 95, 70, 35);
        contentPane.add(txtName);
        txtName.setBounds(150, 95, 150, 35);
        contentPane.add(txtPhone);
        txtPhone.setBounds(150, 135, 150, 35);

        //---- label2 ----
        label2.setText("\u7535\u8bdd\uff1a");
        label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 2f));
        contentPane.add(label2);
        label2.setBounds(75, 135, 70, 35);
        contentPane.add(txtEmail);
        txtEmail.setBounds(150, 175, 150, 35);

        //---- label3 ----
        label3.setText("\u90ae\u7bb1\uff1a");
        label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 2f));
        contentPane.add(label3);
        label3.setBounds(75, 175, 70, 35);

        //---- label4 ----
        label4.setText("\u72b6\u6001\uff1a");
        label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() + 2f));
        contentPane.add(label4);
        label4.setBounds(75, 215, 70, 35);

        //---- button1 ----
        button1.setText("\u786e\u8ba4");
        button1.addActionListener(e -> button1Action(e));
        contentPane.add(button1);
        button1.setBounds(new Rectangle(new Point(90, 275), button1.getPreferredSize()));

        //---- button2 ----
        button2.setText("\u53d6\u6d88");
        button2.addActionListener(e -> button2Action(e));
        contentPane.add(button2);
        button2.setBounds(new Rectangle(new Point(200, 275), button2.getPreferredSize()));

        //---- label5 ----
        label5.setText("\u65b0\u589e\u4f1a\u5458");
        label5.setHorizontalAlignment(SwingConstants.CENTER);
        label5.setFont(label5.getFont().deriveFont(Font.BOLD|Font.ITALIC, label5.getFont().getSize() + 8f));
        label5.setBackground(Color.black);
        label5.setForeground(new Color(0xff9900));
        contentPane.add(label5);
        label5.setBounds(125, 35, 130, 30);

        //---- cmbStatus ----
        cmbStatus.setModel(new DefaultComboBoxModel<>(new String[] {
            "Active",
            "Inactive",
            "Frozen"
        }));
        contentPane.add(cmbStatus);
        cmbStatus.setBounds(150, 215, 150, 35);

        //---- chkAdvanced ----
        chkAdvanced.setText("\u9ad8\u7ea7\u9009\u9879");
        chkAdvanced.addActionListener(e -> chkAdvancedAction(e));
        contentPane.add(chkAdvanced);
        chkAdvanced.setBounds(30, 315, 85, 30);

        //======== panelAdvanced ========
        {
            panelAdvanced.setVisible(false);
            panelAdvanced.setLayout(new GridLayout(4, 2, 8, 8));

            //---- label6 ----
            label6.setText("\u6027\u522b\uff1a");
            label6.setFont(label6.getFont().deriveFont(label6.getFont().getSize() + 2f));
            panelAdvanced.add(label6);

            //---- cmbGender ----
            cmbGender.setModel(new DefaultComboBoxModel<>(new String[] {
                "M",
                "F",
                "Other"
            }));
            panelAdvanced.add(cmbGender);

            //---- label7 ----
            label7.setText("\u51fa\u751f\u65e5\u671f\uff1a");
            label7.setFont(label7.getFont().deriveFont(label7.getFont().getSize() + 2f));
            panelAdvanced.add(label7);

            //---- txtBirthDate ----
            txtBirthDate.setText("YYYY-MM-DD");
            panelAdvanced.add(txtBirthDate);

            //---- label8 ----
            label8.setText("\u7b49\u7ea7ID\uff1a");
            label8.setFont(label8.getFont().deriveFont(label8.getFont().getSize() + 2f));
            panelAdvanced.add(label8);
            panelAdvanced.add(cmbLevel);

            //---- label9 ----
            label9.setText("\u79ef\u5206\uff1a");
            label9.setFont(label9.getFont().deriveFont(label9.getFont().getSize() + 2f));
            panelAdvanced.add(label9);

            //---- txtPoints ----
            txtPoints.setText("0");
            panelAdvanced.add(txtPoints);
        }
        contentPane.add(panelAdvanced);
        panelAdvanced.setBounds(55, 360, 275, 145);

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
    private JTextField txtName;
    private JTextField txtPhone;
    private JLabel label2;
    private JTextField txtEmail;
    private JLabel label3;
    private JLabel label4;
    private JButton button1;
    private JButton button2;
    private JLabel label5;
    private JComboBox<String> cmbStatus;
    private JCheckBox chkAdvanced;
    private JPanel panelAdvanced;
    private JLabel label6;
    private JComboBox<String> cmbGender;
    private JLabel label7;
    private JTextField txtBirthDate;
    private JLabel label8;
    private JComboBox cmbLevel;
    private JLabel label9;
    private JTextField txtPoints;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
