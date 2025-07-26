/*
 * Created by JFormDesigner on Wed May 21 01:26:27 CST 2025
 */

package io.github.idyq.view.LoginScreen;

import io.github.idyq.service.login.AdminService;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;

/**
 * @author guox100
 */
public class Registration extends JFrame {
    public Registration() {
        initComponents();
    }

    // 注册
    private void btnRegistrationAction(ActionEvent e) {
        // TODO add your code here
        performRegistration();
    }

    // 注册功能
    private void performRegistration() {
        String username = userTextField.getText().trim();
        char[] pwd1 = passwordField1.getPassword();
        char[] pwd2 = passwordField2.getPassword();

        // 1. 非空校验
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pwd1.length == 0 || pwd2.length == 0) {
            JOptionPane.showMessageDialog(this, "密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. 两次输入一致性校验
        if (!Arrays.equals(pwd1, pwd2)) {
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. 调用 Service 层完成注册
        try {
            new AdminService().registerAdmin(username, pwd1);

            JOptionPane.showMessageDialog(this,
                    "注册成功！",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE
            );
            this.dispose();

        } catch (IllegalArgumentException ie) {
            // 业务校验异常：用户名已存在或输入不合法
            JOptionPane.showMessageDialog(this,
                    ie.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "注册失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            // 清空内存中的密码
            Arrays.fill(pwd1, '\0');
            Arrays.fill(pwd2, '\0');
        }
    }


    // 取消
    private void btnCancelAction(ActionEvent e) {
        // TODO add your code here
        performCancel();
    }

    // 取消功能
    private void performCancel() {
        this.dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        label1 = new JLabel();
        label2 = new JLabel();
        userTextField = new JTextField();
        label3 = new JLabel();
        label4 = new JLabel();
        btnRegistration = new JButton();
        btnCancel = new JButton();
        passwordField1 = new JPasswordField();
        passwordField2 = new JPasswordField();

        //======== this ========
        setTitle("\u6ce8\u518c");
        setPreferredSize(new Dimension(380, 300));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- label1 ----
        label1.setText("\u6ce8\u518c");
        label1.setFont(label1.getFont().deriveFont(Font.BOLD|Font.ITALIC, label1.getFont().getSize() + 8f));
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(label1);
        label1.setBounds(135, 30, 115, 25);

        //---- label2 ----
        label2.setText("\u7528\u6237\u540d\uff1a");
        label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 1f));
        contentPane.add(label2);
        label2.setBounds(55, 75, 70, 30);
        contentPane.add(userTextField);
        userTextField.setBounds(125, 75, 170, 30);

        //---- label3 ----
        label3.setText("\u5bc6\u7801\uff1a");
        label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 1f));
        contentPane.add(label3);
        label3.setBounds(55, 115, 70, 30);

        //---- label4 ----
        label4.setText("\u786e\u8ba4\u5bc6\u7801\uff1a");
        label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() + 1f));
        contentPane.add(label4);
        label4.setBounds(55, 155, 70, 30);

        //---- btnRegistration ----
        btnRegistration.setText("\u6ce8\u518c");
        btnRegistration.addActionListener(e -> btnRegistrationAction(e));
        contentPane.add(btnRegistration);
        btnRegistration.setBounds(new Rectangle(new Point(75, 205), btnRegistration.getPreferredSize()));

        //---- btnCancel ----
        btnCancel.setText("\u53d6\u6d88");
        btnCancel.addActionListener(e -> btnCancelAction(e));
        contentPane.add(btnCancel);
        btnCancel.setBounds(new Rectangle(new Point(195, 205), btnCancel.getPreferredSize()));
        contentPane.add(passwordField1);
        passwordField1.setBounds(125, 115, 170, 30);
        contentPane.add(passwordField2);
        passwordField2.setBounds(125, 155, 170, 30);

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
    private JTextField userTextField;
    private JLabel label3;
    private JLabel label4;
    private JButton btnRegistration;
    private JButton btnCancel;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
