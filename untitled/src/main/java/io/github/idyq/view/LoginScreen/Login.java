/*
 * Created by JFormDesigner on Tue May 20 23:15:18 CST 2025
 */

package io.github.idyq.view.LoginScreen;

import com.formdev.flatlaf.FlatIntelliJLaf;
import io.github.idyq.service.login.AdminService;
import io.github.idyq.view.HomeScreen.MainApp;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;

/**
 * @author guox100
 */
public class Login extends JFrame{
    public Login() {
        initComponents();
    }

    public static void main(String[] args) {
        try {
            // 1.1 切换到 FlatLaf IntelliJ 主题
            FlatIntelliJLaf.setup();

            // 1.2 自定义“标题栏”的背景色和前景色，使其和左侧导航相同：#EEF4F9
            //    — 活动时标题栏背景
            UIManager.put("TitlePane.activeBackground", new Color(0xEEF4F9));
            //    — 不活动时标题栏背景（选一个略显灰度的同系色）
            UIManager.put("TitlePane.inactiveBackground", new Color(0xEEF4F9));
            //    — 标题文字以及按钮的前景色
            UIManager.put("TitlePane.activeForeground", Color.BLACK);
            UIManager.put("TitlePane.inactiveForeground", new Color(0x777777));
            //    — 鼠标悬停在标题栏按钮时的背景色
            UIManager.put("TitlePane.buttonHoverBackground", new Color(0xC8D4DD));
            //    — 鼠标按下标题栏按钮时的背景色
            UIManager.put("TitlePane.buttonPressedBackground", new Color(0xAABCC7));
            //    — 标题栏按钮的圆角半径
            UIManager.put("TitlePane.buttonArc", 6);
            //    — 将标题栏图标（最小化/最大化/关闭）设为稍深色，以便在浅背景上看清
            UIManager.put("TitlePane.buttonForeground", new Color(0x333333));
            UIManager.put("TitlePane.buttonHoverForeground", new Color(0x333333));
        } catch (Exception ex) {
            ex.printStackTrace();
            // 如果加载失败，就使用系统默认 L&F
        }

        SwingUtilities.invokeLater(() ->{
            Login loginFrame=new Login();
            loginFrame.setVisible(true);
            loginFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        });
    }

    // 登陆
    private void loginImpl(ActionEvent e) {
        // TODO add your code here
        performLogin();
    }

    // 注册单击
    private void registrationLabelMouseClicked(MouseEvent e) {
        // TODO add your code here
        performRegistration(e);
    }

    // 注册悬停
    private void registrationLabelMouseEntered(MouseEvent e) {
        // TODO add your code here
        performHovering();
    }

    // 鼠标移出
    private void registrationLabelMouseExited(MouseEvent e) {
        // TODO add your code here
        performHovered();
    }

    // 取消
    private void btnCancelAction(ActionEvent e) {
        // TODO add your code here
        performCancel();
    }

    // 登陆功能
    private void performLogin() {
        // 获取输入
        String username = userTextField.getText();
        char[] password = PasswordField.getPassword();

        try {
            // 调用Service层验证
            AdminService adminService = new AdminService();
            boolean loginSuccess = adminService.login(username, password);

            if (loginSuccess) {
                // 登录成功处理
                SwingUtilities.invokeLater(() -> {
                    new MainApp().setVisible(true); // 打开主界面
                    this.dispose(); // 关闭登录窗口
                });
            } else {
                // 登录失败提示
                JOptionPane.showMessageDialog(this,
                        "用户名或密码错误",
                        "登录失败",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            // 异常处理
            JOptionPane.showMessageDialog(this,
                    "登录过程中发生错误: " + ex.getMessage(),
                    "系统错误",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // 清除密码内存
            Arrays.fill(password, '\0');
            PasswordField.setText(""); // 清空密码框
        }
    }

    // 注册功能
    private void performRegistration(MouseEvent e) {
        Registration registrationFrame = new Registration();
        // 单例模式避免重复创建窗口
        if (registrationFrame == null || !registrationFrame.isVisible()) {
            registrationFrame = new Registration();

            // 设置窗口位置（居中于登录窗口）
            registrationFrame.setLocationRelativeTo(this);

            // 设置关闭行为（仅关闭当前窗口）
            registrationFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

        // 激活已有窗口（如果存在）
        registrationFrame.toFront();
        registrationFrame.setVisible(true);

        // 可选：添加窗口监听（当注册窗口关闭时执行操作）
        registrationFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 这里可以添加注册成功后的回调逻辑
                System.out.println("注册窗口已关闭");
            }
        });
    }

    // 悬停ing
    private void performHovering() {
        registrationLabel.setForeground(new Color(0, 100, 200)); // 悬停颜色
    }

    // 悬停ed
    private void performHovered() {
        registrationLabel.setForeground(Color.gray); // 恢复颜色
    }

    // 取消功能
    private void performCancel() {
        this.dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        titleLabel = new JLabel();
        label1 = new JLabel();
        userTextField = new JTextField();
        label2 = new JLabel();
        PasswordField = new JPasswordField();
        loginButton = new JButton();
        btnCancel = new JButton();
        registrationLabel = new JLabel();

        //======== this ========
        setMinimumSize(new Dimension(550, 376));
        setPreferredSize(new Dimension(550, 376));
        setTitle("\u7f8e\u53d1\u8fde\u9501\u5e97\u4f1a\u5458\u5361\u4fe1\u606f\u7ba1\u7406\u7cfb\u7edf");
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- titleLabel ----
        titleLabel.setText("\u7ba1\u7406\u5458\u767b\u9646");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("\u534e\u6587\u4e2d\u5b8b", Font.BOLD | Font.ITALIC, 20));
        contentPane.add(titleLabel);
        titleLabel.setBounds(130, 50, 270, 45);

        //---- label1 ----
        label1.setText("\u7528\u6237\u540d\uff1a");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 3f));
        contentPane.add(label1);
        label1.setBounds(130, 120, 75, 35);
        contentPane.add(userTextField);
        userTextField.setBounds(205, 120, 190, 35);

        //---- label2 ----
        label2.setText("\u5bc6   \u7801\uff1a");
        label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 3f));
        contentPane.add(label2);
        label2.setBounds(130, 170, 75, 35);
        contentPane.add(PasswordField);
        PasswordField.setBounds(205, 170, 190, 35);

        //---- loginButton ----
        loginButton.setText("\u767b\u9646");
        loginButton.setFont(loginButton.getFont().deriveFont(loginButton.getFont().getSize() + 2f));
        loginButton.addActionListener(e -> loginImpl(e));
        contentPane.add(loginButton);
        loginButton.setBounds(140, 250, 105, 35);

        //---- btnCancel ----
        btnCancel.setText("\u53d6\u6d88");
        btnCancel.setFont(btnCancel.getFont().deriveFont(btnCancel.getFont().getSize() + 2f));
        btnCancel.addActionListener(e -> btnCancelAction(e));
        contentPane.add(btnCancel);
        btnCancel.setBounds(270, 250, 105, 35);

        //---- registrationLabel ----
        registrationLabel.setText("\u672a\u767b\u5f55\uff1f\u70b9\u6b64\u6ce8\u518c");
        registrationLabel.setFont(registrationLabel.getFont().deriveFont(registrationLabel.getFont().getSize() - 1f));
        registrationLabel.setForeground(Color.gray);
        registrationLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                registrationLabelMouseClicked(e);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                registrationLabelMouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registrationLabelMouseExited(e);
            }
        });
        contentPane.add(registrationLabel);
        registrationLabel.setBounds(295, 215, 95, registrationLabel.getPreferredSize().height);

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
    private JLabel titleLabel;
    private JLabel label1;
    private JTextField userTextField;
    private JLabel label2;
    private JPasswordField PasswordField;
    private JButton loginButton;
    private JButton btnCancel;
    private JLabel registrationLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
