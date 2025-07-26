/*
 * Created by JFormDesigner on Tue Jun 03 23:33:07 CST 2025
 */

package io.github.idyq.view.Panel.VIPLevel;

import io.github.idyq.model.entity.MemberLevel;
import io.github.idyq.service.VIP.MemberLevelService;
import io.github.idyq.service.VIP.MemberLevelServiceImpl;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;

/**
 * @author guox100
 */
public class UpdateLevel extends JDialog {
    /** 当前正在编辑的 MemberLevel 对象 */
    private final MemberLevel currentLevel;

    /** 业务层，用于更新数据库 */
    private final MemberLevelService levelService = new MemberLevelServiceImpl();

    public UpdateLevel(Window owner , MemberLevel level) {
        super(owner);
        this.currentLevel = level;
        initComponents();

        // 填充当前记录的默认值
        prefillForm();
    }

    private void button1Action(ActionEvent e) {
        // TODO add your code here
        // 1. 校验“等级名称”非空
        String name = txtLevelName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "等级名称不能为空",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. 校验并解析 “最低积分”
        String minPointsText = txtMinPoints.getText().trim();
        int minPoints;
        try {
            minPoints = Integer.parseInt(minPointsText);
            if (minPoints < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "最低积分请输入非负整数",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 3. 校验并解析 “折扣率”
        String discountText = txtDiscountRate.getText().trim();
        double discount;
        try {
            discount = Double.parseDouble(discountText);
            if (discount < 0.0 || discount > 1.0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "折扣率请输入 0.0 到 1.0 之间的小数",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 4. 从文本框获取 “权益说明”
        String benefits = txtBenefits.getText().trim();

        // 5. 将新值写回 currentLevel
        currentLevel.setLevelName(name);
        currentLevel.setMinPoints(minPoints);
        currentLevel.setDiscountRate(discount);
        currentLevel.setBenefits(benefits);

        // 6. 调用 Service 执行更新
        try {
            boolean ok = levelService.update(currentLevel);
            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "修改成功",
                        "提示",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose(); // 关闭对话框
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "修改失败，未更新任何记录",
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "修改过程中出现错误：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void button2Action(ActionEvent e) {
        // TODO add your code here
        this.dispose();
    }


    /** 把 currentLevel 的字段值填充到表单控件 */
    private void prefillForm() {
        txtLevelName.setText(currentLevel.getLevelName());
        txtMinPoints.setText(String.valueOf(currentLevel.getMinPoints()));
        txtDiscountRate.setText(String.valueOf(currentLevel.getDiscountRate()));
        txtBenefits.setText(currentLevel.getBenefits());
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
        button1 = new JButton();
        button2 = new JButton();
        txtBenefits = new JTextField();
        label4 = new JLabel();

        //======== this ========
        setPreferredSize(new Dimension(520, 330));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- label5 ----
        label5.setText("\u4fee\u6539\u4f1a\u5458\u7b49\u7ea7");
        label5.setHorizontalAlignment(SwingConstants.CENTER);
        label5.setFont(label5.getFont().deriveFont(Font.BOLD|Font.ITALIC, label5.getFont().getSize() + 8f));
        label5.setBackground(Color.black);
        label5.setForeground(new Color(0xff9900));
        contentPane.add(label5);
        label5.setBounds(175, 25, 155, 30);

        //---- label1 ----
        label1.setText("\u7b49\u7ea7\u79f0\u53f7\uff1a");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 2f));
        contentPane.add(label1);
        label1.setBounds(60, 85, 85, 35);
        contentPane.add(txtLevelName);
        txtLevelName.setBounds(150, 85, 150, 34);

        //---- label2 ----
        label2.setText("\u6700\u4f4e\u79ef\u5206\uff1a");
        label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 2f));
        contentPane.add(label2);
        label2.setBounds(60, 125, 85, 35);
        contentPane.add(txtMinPoints);
        txtMinPoints.setBounds(150, 125, 150, 34);

        //---- label3 ----
        label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 2f));
        label3.setText("\u53ef\u4eab\u6298\u6263\uff1a");
        contentPane.add(label3);
        label3.setBounds(60, 165, 85, 35);
        contentPane.add(txtDiscountRate);
        txtDiscountRate.setBounds(150, 165, 150, 34);

        //---- button1 ----
        button1.setText("\u786e\u8ba4\u4fee\u6539");
        button1.addActionListener(e -> button1Action(e));
        contentPane.add(button1);
        button1.setBounds(105, 230, 120, 34);

        //---- button2 ----
        button2.setText("\u9000\u51fa");
        button2.addActionListener(e -> button2Action(e));
        contentPane.add(button2);
        button2.setBounds(295, 230, 120, 34);
        contentPane.add(txtBenefits);
        txtBenefits.setBounds(325, 120, 148, 83);

        //---- label4 ----
        label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() + 2f));
        label4.setText("\u6743\u76ca\u8bf4\u660e\uff1a");
        contentPane.add(label4);
        label4.setBounds(365, 80, 85, 35);

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
    private JButton button1;
    private JButton button2;
    private JTextField txtBenefits;
    private JLabel label4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
