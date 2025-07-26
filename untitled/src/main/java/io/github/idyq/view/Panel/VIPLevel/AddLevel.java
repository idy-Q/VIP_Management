/*
 * Created by JFormDesigner on Tue Jun 03 23:33:20 CST 2025
 */

package io.github.idyq.view.Panel.VIPLevel;

import io.github.idyq.model.entity.MemberLevel;
import io.github.idyq.service.VIP.MemberLevelService;
import io.github.idyq.service.VIP.MemberLevelServiceImpl;
import io.github.idyq.util.SqliteJdbcUtil;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;

/**
 * @author guox100
 */
public class AddLevel extends JDialog {
    public AddLevel(Window owner) {
        super(owner);
        initComponents();
    }

    private void button1Action(ActionEvent e) {
        // TODO add your code here
        MemberLevel level = new MemberLevel();
        level.setLevelName(txtLevelName.getText().trim());
        level.setMinPoints(Integer.parseInt(txtMinPoints.getText().trim()));
        level.setDiscountRate(Double.parseDouble(txtDiscountRate.getText().trim()));
        level.setBenefits(txtBenefits.getText().trim());

        try (Connection conn = SqliteJdbcUtil.getConnection()) {
            MemberLevelService svc = new MemberLevelServiceImpl();
            boolean ok = svc.create(level);
            if (ok) {
                JOptionPane.showMessageDialog(this, "新增等级成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "新增失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "出错：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void button2Action(ActionEvent e) {
        // TODO add your code here
        this.dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        label5 = new JLabel();
        label1 = new JLabel();
        txtLevelName = new JTextField();
        label2 = new JLabel();
        txtMinPoints = new JTextField();
        label3 = new JLabel();
        txtDiscountRate = new JTextField();
        label4 = new JLabel();
        button1 = new JButton();
        button2 = new JButton();
        scrollPaneBenefits = new JScrollPane();
        txtBenefits = new JTextField();

        //======== this ========
        setPreferredSize(new Dimension(520, 330));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- label5 ----
        label5.setText("\u6dfb\u52a0\u4f1a\u5458\u7b49\u7ea7");
        label5.setHorizontalAlignment(SwingConstants.CENTER);
        label5.setFont(label5.getFont().deriveFont(Font.BOLD|Font.ITALIC, label5.getFont().getSize() + 8f));
        label5.setBackground(Color.black);
        label5.setForeground(new Color(0xff9900));
        contentPane.add(label5);
        label5.setBounds(170, 20, 155, 30);

        //---- label1 ----
        label1.setText("\u7b49\u7ea7\u79f0\u53f7\uff1a");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 2f));
        contentPane.add(label1);
        label1.setBounds(50, 80, 85, 35);
        contentPane.add(txtLevelName);
        txtLevelName.setBounds(140, 80, 150, 34);

        //---- label2 ----
        label2.setText("\u6700\u4f4e\u79ef\u5206\uff1a");
        label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 2f));
        contentPane.add(label2);
        label2.setBounds(50, 120, 85, 35);
        contentPane.add(txtMinPoints);
        txtMinPoints.setBounds(140, 120, 150, 34);

        //---- label3 ----
        label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 2f));
        label3.setText("\u53ef\u4eab\u6298\u6263\uff1a");
        contentPane.add(label3);
        label3.setBounds(50, 160, 85, 35);
        contentPane.add(txtDiscountRate);
        txtDiscountRate.setBounds(140, 160, 150, 34);

        //---- label4 ----
        label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() + 2f));
        label4.setText("\u6743\u76ca\u8bf4\u660e\uff1a");
        contentPane.add(label4);
        label4.setBounds(355, 75, 85, 35);

        //---- button1 ----
        button1.setText("\u6dfb\u52a0");
        button1.addActionListener(e -> button1Action(e));
        contentPane.add(button1);
        button1.setBounds(95, 225, 120, button1.getPreferredSize().height);

        //---- button2 ----
        button2.setText("\u9000\u51fa");
        button2.addActionListener(e -> button2Action(e));
        contentPane.add(button2);
        button2.setBounds(285, 225, 120, button2.getPreferredSize().height);

        //======== scrollPaneBenefits ========
        {
            scrollPaneBenefits.setViewportView(txtBenefits);
        }
        contentPane.add(scrollPaneBenefits);
        scrollPaneBenefits.setBounds(315, 110, 150, 85);

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
    private JLabel label5;
    private JLabel label1;
    private JTextField txtLevelName;
    private JLabel label2;
    private JTextField txtMinPoints;
    private JLabel label3;
    private JTextField txtDiscountRate;
    private JLabel label4;
    private JButton button1;
    private JButton button2;
    private JScrollPane scrollPaneBenefits;
    private JTextField txtBenefits;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
