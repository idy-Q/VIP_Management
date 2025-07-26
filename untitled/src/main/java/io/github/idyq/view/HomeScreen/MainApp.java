/*
 * Created by JFormDesigner on Wed May 21 00:42:14 CST 2025
 */

package io.github.idyq.view.HomeScreen;

import com.formdev.flatlaf.FlatIntelliJLaf;
import io.github.idyq.view.Panel.AppointmentManage.AppointmentPanel;
import io.github.idyq.view.Panel.CardSettings.CardSettingsPanel;
import io.github.idyq.view.Panel.Home.Home;
import io.github.idyq.view.Panel.ServiceItem.ServiceItemPanel;
import io.github.idyq.view.Panel.VIPLevel.VIPLevel;
import io.github.idyq.view.Panel.VIPManage.VIPManage;
import io.github.idyq.view.Panel.VIPTransaction.VIPTransaction;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * @author guox100
 */
public class MainApp extends JFrame {
    public static void showWindeow() {
        // ==== 1. 启用 FlatLaf 自装饰标题栏，并设置主题为 IntelliJ 风格 ====
        //    必须在 setLookAndFeel 之前调用下面两行，否则标题栏还是系统默认样式
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

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

        // ==== 2. 启动 Swing 主应用 ====
        SwingUtilities.invokeLater(() -> {
            MainApp ma = new MainApp();
            ma.setVisible(true);
            ma.setDefaultCloseOperation(EXIT_ON_CLOSE);
        });
    }

    // “原始大小”以及“交易页面宽度”
    private Dimension originalSize;
    private Dimension transactionSize;

    private CardLayout cardLayout;
    // 保存对 Home 面板的引用，以便刷新
    private Home homePanel;

    public MainApp() {
        initComponents();
        initSizes();
        initListeners();
        initPages();
        // ==== 程序刚启动时要把所有导航按钮的背景都设为默认色 #EEF4F9 ====
        resetAllNavButtons();
    }

    /**
     * 1. 记录“原始大小”以及“充值/消费时希望的大小”。
     */
    private void initSizes() {
        // 调用 initComponents() 之后，getPreferredSize() 就会有值
        originalSize = getPreferredSize();
        // 比如宽度放到 1060，高度保持不变
        transactionSize = new Dimension(1060, originalSize.height);
    }

    /**
     * 2. 初始化各个页面并把它们添加到 contentPanel 的 CardLayout 中，
     *    然后默认显示 Home 页面。
     */
    private void initPages() {
        cardLayout = (CardLayout) contentPanel.getLayout();

        homePanel = new Home();
        // 统一把 Home 和后续页面的背景都设为 #FAFCFD
        homePanel.setBackground(new Color(0xFAFCFD));
        contentPanel.add(homePanel, "HOME");

        VIPManage vipManagePanel = new VIPManage();
        vipManagePanel.setBackground(new Color(0xFAFCFD));
        contentPanel.add(vipManagePanel, "VIP");

        VIPLevel vipLevelPanel = new VIPLevel();
        vipLevelPanel.setBackground(new Color(0xFAFCFD));
        contentPanel.add(vipLevelPanel, "VIPLEVEL");

        VIPTransaction vipTransPanel = new VIPTransaction();
        vipTransPanel.setBackground(new Color(0xFAFCFD));
        contentPanel.add(vipTransPanel, "VIPTRANSACTION");

        CardSettingsPanel csPanel = new CardSettingsPanel();
        csPanel.setBackground(new Color(0xFAFCFD));
        contentPanel.add(csPanel, "CARDSETTINGS");

        AppointmentPanel apPanel = new AppointmentPanel();
        apPanel.setBackground(new Color(0xFAFCFD));
        contentPanel.add(apPanel, "APPOINTMENTS");

        ServiceItemPanel siPanel = new ServiceItemPanel();
        siPanel.setBackground(new Color(0xFAFCFD));
        contentPanel.add(siPanel, "SERVICEITEM");

        // 默认选中“首页”：
        cardLayout.show(contentPanel, "HOME");
        // 并把按钮“首页”高亮
        highlightButton(btnHome);
    }

    /**
     * 3. 给各个导航按钮绑定监听：切换页面、调整窗口大小、并更新按钮背景颜色。
     */
    private void initListeners() {
        // Helper: 点击某个按钮时，先把所有按钮背景重置为默认（#EEF4F9），
        // 再把当前按钮背景设置为选中色（#E7EBF0）
        ActionListener navListener = e -> {
            // 重置所有按钮背景 → #EEF4F9
            resetAllNavButtons();
            // 把被点击的按钮背景设为选中色 #E7EBF0
            JButton clicked = (JButton) e.getSource();
            highlightButton(clicked);
        };

        // ---- 首页按钮 ----
        btnHome.addActionListener(e -> {
            // 先调整回原始大小
            setSize(originalSize);
            // 切换到首页
            cardLayout.show(contentPanel, "HOME");
            homePanel.refreshDashboard();
            // 更新按钮背景
            navListener.actionPerformed(e);
        });

        // ---- 会员管理 ----
        btnVIPManage.addActionListener(e -> {
            setSize(originalSize);
            cardLayout.show(contentPanel, "VIP");
            navListener.actionPerformed(e);
        });

        // ---- 会员等级 ----
        btnVIPLevel.addActionListener(e -> {
            setSize(originalSize);
            cardLayout.show(contentPanel, "VIPLEVEL");
            navListener.actionPerformed(e);
        });

        // ---- 充值/消费 ----
        btnVIPTransaction.addActionListener(e -> {
            setSize(transactionSize);
            cardLayout.show(contentPanel, "VIPTRANSACTION");
            navListener.actionPerformed(e);
        });

        // ---- 充值优惠 ----
        btnCardSettings.addActionListener(e -> {
            setSize(originalSize);
            cardLayout.show(contentPanel, "CARDSETTINGS");
            navListener.actionPerformed(e);
        });

        // ---- 预约管理 ----
        btnAppointments.addActionListener(e -> {
            setSize(transactionSize);
            cardLayout.show(contentPanel, "APPOINTMENTS");
            navListener.actionPerformed(e);
        });

        // ---- 服务项目 ----
        btnServiceItem.addActionListener(e -> {
            setSize(originalSize);
            cardLayout.show(contentPanel, "SERVICEITEM");
            navListener.actionPerformed(e);
        });
    }

    /**
     * 把所有导航按钮背景都重置为“默认色” #EEF4F9
     */
    private void resetAllNavButtons() {
        Color defaultNavBg = new Color(0xEEF4F9);
        btnHome.setBackground(defaultNavBg);
        btnVIPManage.setBackground(defaultNavBg);
        btnVIPLevel.setBackground(defaultNavBg);
        btnVIPTransaction.setBackground(defaultNavBg);
        btnCardSettings.setBackground(defaultNavBg);
        btnAppointments.setBackground(defaultNavBg);
        btnServiceItem.setBackground(defaultNavBg);
    }

    /**
     * 把指定按钮背景设置为“选中色” #E7EBF0，并且确保它是 OPAQUE
     */
    private void highlightButton(JButton btn) {
        btn.setBackground(new Color(0xE7EBF0));
        btn.setOpaque(true);
    }

    /**
     * 4. initComponents()——JFormDesigner 自动生成的布局。我们在这里只做极少的修改：
     *    - 把 navPanel 背景设为 #EEF4F9
     *    - 把 contentPanel 的背景色初始值设为 #FAFCFD
     */
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        splitPane = new JSplitPane();
        navPanel = new JPanel();
        btnHome = new JButton();
        btnVIPManage = new JButton();
        btnVIPLevel = new JButton();
        btnVIPTransaction = new JButton();
        btnCardSettings = new JButton();
        btnAppointments = new JButton();
        btnServiceItem = new JButton();
        contentPanel = new JPanel();

        //======== this ========
        setTitle("\u7f8e\u53d1\u8fde\u9501\u5e97\u4f1a\u5458\u5361\u4fe1\u606f\u7ba1\u7406\u7cfb\u7edf");
        setPreferredSize(new Dimension(860, 525));
        setMinimumSize(new Dimension(860, 525));
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== splitPane ========
        {
            splitPane.setDividerSize(5);
            splitPane.setDividerLocation(200);

            //======== navPanel ========
            {
                navPanel.setBackground(new Color(0xf5f7fa));
                navPanel.setBorder(null);
                navPanel.setMinimumSize(new Dimension(150, 0));
                navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

                //---- btnHome ----
                btnHome.setText("\u9996\u9875");
                btnHome.setMaximumSize(new Dimension(32676, 34));
                btnHome.setBorder(null);
                navPanel.add(btnHome);

                //---- btnVIPManage ----
                btnVIPManage.setText("\u4f1a\u5458\u7ba1\u7406");
                btnVIPManage.setMaximumSize(new Dimension(32676, 34));
                btnVIPManage.setBorder(null);
                navPanel.add(btnVIPManage);

                //---- btnVIPLevel ----
                btnVIPLevel.setText("\u4f1a\u5458\u7b49\u7ea7");
                btnVIPLevel.setMaximumSize(new Dimension(32768, 34));
                btnVIPLevel.setBorder(null);
                navPanel.add(btnVIPLevel);

                //---- btnVIPTransaction ----
                btnVIPTransaction.setText("\u5145\u503c/\u6d88\u8d39");
                btnVIPTransaction.setMaximumSize(new Dimension(32767, 34));
                btnVIPTransaction.setBorder(null);
                navPanel.add(btnVIPTransaction);

                //---- btnCardSettings ----
                btnCardSettings.setText("\u5145\u503c\u7279\u60e0");
                btnCardSettings.setMaximumSize(new Dimension(32767, 34));
                btnCardSettings.setBorder(null);
                navPanel.add(btnCardSettings);

                //---- btnAppointments ----
                btnAppointments.setText("\u9884\u7ea6\u7ba1\u7406");
                btnAppointments.setMaximumSize(new Dimension(32767, 34));
                btnAppointments.setBorder(null);
                navPanel.add(btnAppointments);

                //---- btnServiceItem ----
                btnServiceItem.setText("\u670d\u52a1\u9879\u76ee");
                btnServiceItem.setMaximumSize(new Dimension(32767, 34));
                btnServiceItem.setBorder(null);
                navPanel.add(btnServiceItem);
            }
            splitPane.setLeftComponent(navPanel);

            //======== contentPanel ========
            {
                contentPanel.setMinimumSize(new Dimension(0, 0));
                contentPanel.setLayout(new CardLayout());
            }
            splitPane.setRightComponent(contentPanel);
        }
        contentPane.add(splitPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JSplitPane splitPane;
    private JPanel navPanel;
    private JButton btnHome;
    private JButton btnVIPManage;
    private JButton btnVIPLevel;
    private JButton btnVIPTransaction;
    private JButton btnCardSettings;
    private JButton btnAppointments;
    private JButton btnServiceItem;
    private JPanel contentPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
