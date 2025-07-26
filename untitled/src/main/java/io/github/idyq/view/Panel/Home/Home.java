/*
 * Created by JFormDesigner on Tue May 27 00:39:55 CST 2025
 */

package io.github.idyq.view.Panel.Home;

import io.github.idyq.service.dashboard.DashboardService;
import io.github.idyq.service.dashboard.DashboardServiceImpl;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author guox100
 */
public class Home extends JPanel {
    private ChartPanel chartPanel;
    private final DashboardService dashboardService = new DashboardServiceImpl();
    private final Timer refreshTimer;      // Swing 定时器，用于自动刷新


    public Home() {
        initComponents();
        initChartPanel();

        // 启动时立即刷新一次，确保数据显示
        System.out.println("[Home] 初始化后主动刷新一次仪表盘");
        refreshDashboard();

        // 设置定时器（每5分钟刷新）
        refreshTimer = new Timer(300_000, e -> {
            System.out.println("[Home] 定时器触发自动刷新");
            refreshDashboard();
        });
        refreshTimer.setInitialDelay(0); // 启动后立即触发
        refreshTimer.start();

        // 组件显示时也尝试刷新一次（避免失效）
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                System.out.println("[Home] 面板被显示，刷新仪表盘");
                refreshDashboard();
            }
        });
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents


    // ========== 自定义方法区 ==========

    /**
     * 初始化折线图组件并添加到面板
     */
//    private void initChartPanel() {
//        // 1. 拿到当前月份的所有“日期”序列（从1号到今天）
//        List<java.util.Date> days = dashboardService.getRecentDates();
//
//        // 2. 调用 Service 拿到“日期→充值总额”的 Map 和“日期→消费总额”的 Map
//        Map<java.util.Date, Double> rechargeMap;
//        Map<java.util.Date, Double> consumeMap;
//        try {
//            rechargeMap = dashboardService.getRechargePerDayMap();
//            consumeMap  = dashboardService.getConsumePerDayMap();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            // 如果查询失败，就用空 Map（全部补 0.0）
//            rechargeMap = Collections.emptyMap();
//            consumeMap  = Collections.emptyMap();
//        }
//
//        // 3. 用上面两个 Map 逐日取值，生成与 days 等长的两个列表
//        List<Double> recharge = new ArrayList<>();
//        List<Double> consume  = new ArrayList<>();
//        for (java.util.Date d : days) {
//            // 如果 Map 里没有某天的记录，getOrDefault 会返回 0.0
//            recharge.add(rechargeMap.getOrDefault(d, 0.0));
//            consume .add(consumeMap.getOrDefault(d, 0.0));
//        }
//
//        // 4. 用 days、recharge、consume 造出 ChartPanel
//        chartPanel = createLineChart(days, recharge, consume);
//        chartPanel.setBounds(45, 180, 530, 180);
//        this.add(chartPanel);
//
//        // 5. 然后填充其他卡片数据
//        refreshDashboard();
//    }

    private void initChartPanel() {
        // 初始化空图表数据，防止空指针
        chartPanel = new ChartPanel(null);
        chartPanel.setBounds(45, 180, 530, 180);
        this.add(chartPanel);
    }

    /**
     * 定期或首次调用，刷新所有卡片数值和折线图数据
     */
    public void refreshDashboard() {
        // —— 测试用打印，观察调用时刻的日期列表与对应的充值/消费数据 ——
        List<java.util.Date> days = dashboardService.getRecentDates();

        Map<java.util.Date, Double> rechargeMap;
        Map<java.util.Date, Double> consumeMap;
        try {
            rechargeMap = dashboardService.getRechargePerDayMap();
            consumeMap  = dashboardService.getConsumePerDayMap();
        } catch (SQLException ex) {
            ex.printStackTrace();
            rechargeMap = Collections.emptyMap();
            consumeMap  = Collections.emptyMap();
        }

        // 1. 卡片数值更新
        lblTotalMembers.setText(String.valueOf(dashboardService.getTotalMembers()));
        lblNewMembersThisMonth.setText(String.valueOf(dashboardService.getNewMembersThisMonth()));
        lblActiveMemberRate.setText(String.format("%.1f%%", dashboardService.getActiveMemberRate()));
        lblPendingAppointments.setText(String.valueOf(dashboardService.getPendingAppointments()));
        lblLowBalanceMembers.setText(String.valueOf(dashboardService.getLowBalanceMembers()));

        // 2. 折线图更新：移除旧的 chartPanel
        this.remove(chartPanel);

        // 3. 重新构建折线图数据
        List<Double> recharge = new ArrayList<>();
        List<Double> consume  = new ArrayList<>();
        for (java.util.Date d : days) {
            recharge.add(rechargeMap.getOrDefault(d, 0.0));
            consume.add(consumeMap.getOrDefault(d, 0.0));
        }

        chartPanel = createLineChart(days, recharge, consume);
        chartPanel.setBounds(45, 180, 530, 180);
        this.add(chartPanel);

        // 4. 重绘
        this.revalidate();
        this.repaint();
    }

    /**
     * 工厂方法：根据传入数据（days, recharge, consume）构造 JFreeChart 的 ChartPanel
     */
    private ChartPanel createLineChart(List<java.util.Date> days,
                                       List<Double> recharge,
                                       List<Double> consume) {
        // 构造 JFreeChart 时间序列 (TimeSeries)
        org.jfree.data.time.TimeSeries sR = new org.jfree.data.time.TimeSeries("充值");
        org.jfree.data.time.TimeSeries sC = new org.jfree.data.time.TimeSeries("消费");

        for (int i = 0; i < days.size(); i++) {
            org.jfree.data.time.Day d = new org.jfree.data.time.Day(days.get(i));
            sR.add(d, recharge.get(i));
            sC.add(d, consume.get(i));
        }
        org.jfree.data.time.TimeSeriesCollection dataset = new org.jfree.data.time.TimeSeriesCollection();
        dataset.addSeries(sR);
        dataset.addSeries(sC);

        org.jfree.chart.JFreeChart chart = org.jfree.chart.ChartFactory.createTimeSeriesChart(
                "本月收支趋势",
                "日期",
                "金额(¥)",
                dataset,
                true, true, false
        );

        // 解决中文乱码
        Font titleFont      = new Font("宋体", Font.BOLD, 16);
        Font axisLabelFont  = new Font("宋体", Font.PLAIN, 12);
        Font tickLabelFont  = new Font("宋体", Font.PLAIN, 10);

        chart.getTitle().setFont(titleFont);
        XYPlot plot = (XYPlot) chart.getPlot();

        org.jfree.chart.axis.ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(axisLabelFont);
        domainAxis.setTickLabelFont(tickLabelFont);

        org.jfree.chart.axis.ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(axisLabelFont);
        rangeAxis.setTickLabelFont(tickLabelFont);

        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(tickLabelFont);
        }

        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        return panel;
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        panelVIPCount = new JPanel();
        lblTitle = new JLabel();
        lblTotalMembers = new JLabel();
        panelVIPCount2 = new JPanel();
        lblTitle2 = new JLabel();
        lblNewMembersThisMonth = new JLabel();
        panelVIPCount3 = new JPanel();
        lblTitle3 = new JLabel();
        lblActiveMemberRate = new JLabel();
        panelVIPCount4 = new JPanel();
        lblTitle4 = new JLabel();
        lblPendingAppointments = new JLabel();
        panelVIPCount5 = new JPanel();
        lblTitle5 = new JLabel();
        lblLowBalanceMembers = new JLabel();

        //======== this ========
        setLayout(null);

        //======== panelVIPCount ========
        {
            panelVIPCount.setPreferredSize(new Dimension(150, 100));
            panelVIPCount.setBorder(new TitledBorder("\u603b\u4f1a\u5458\u6570"));
            panelVIPCount.setLayout(new BoxLayout(panelVIPCount, BoxLayout.Y_AXIS));

            //---- lblTitle ----
            lblTitle.setText("\u603b\u4f1a\u5458\u6570");
            lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            lblTitle.setAlignmentX(0.5F);
            panelVIPCount.add(lblTitle);

            //---- lblTotalMembers ----
            lblTotalMembers.setText("0");
            lblTotalMembers.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 32));
            lblTotalMembers.setAlignmentX(0.5F);
            panelVIPCount.add(lblTotalMembers);
        }
        add(panelVIPCount);
        panelVIPCount.setBounds(new Rectangle(new Point(45, 50), panelVIPCount.getPreferredSize()));

        //======== panelVIPCount2 ========
        {
            panelVIPCount2.setPreferredSize(new Dimension(150, 100));
            panelVIPCount2.setBorder(new TitledBorder("\u672c\u6708\u65b0\u589e\u4f1a\u5458"));
            panelVIPCount2.setLayout(new BoxLayout(panelVIPCount2, BoxLayout.Y_AXIS));

            //---- lblTitle2 ----
            lblTitle2.setText("\u672c\u6708\u65b0\u589e\u4f1a\u5458");
            lblTitle2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            lblTitle2.setAlignmentX(0.5F);
            panelVIPCount2.add(lblTitle2);

            //---- lblNewMembersThisMonth ----
            lblNewMembersThisMonth.setText("0");
            lblNewMembersThisMonth.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 32));
            lblNewMembersThisMonth.setAlignmentX(0.5F);
            panelVIPCount2.add(lblNewMembersThisMonth);
        }
        add(panelVIPCount2);
        panelVIPCount2.setBounds(235, 50, 150, 100);

        //======== panelVIPCount3 ========
        {
            panelVIPCount3.setPreferredSize(new Dimension(150, 100));
            panelVIPCount3.setBorder(new TitledBorder("\u6d3b\u8dc3\u4f1a\u5458\u5360\u6bd4"));
            panelVIPCount3.setLayout(new BoxLayout(panelVIPCount3, BoxLayout.Y_AXIS));

            //---- lblTitle3 ----
            lblTitle3.setText("\u6d3b\u8dc3\u4f1a\u5458\u5360\u6bd4%");
            lblTitle3.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            lblTitle3.setAlignmentX(0.5F);
            panelVIPCount3.add(lblTitle3);

            //---- lblActiveMemberRate ----
            lblActiveMemberRate.setText("0.0%");
            lblActiveMemberRate.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 32));
            lblActiveMemberRate.setAlignmentX(0.5F);
            panelVIPCount3.add(lblActiveMemberRate);
        }
        add(panelVIPCount3);
        panelVIPCount3.setBounds(425, 50, 150, 100);

        //======== panelVIPCount4 ========
        {
            panelVIPCount4.setPreferredSize(new Dimension(150, 100));
            panelVIPCount4.setBorder(new TitledBorder("\u5f85\u5904\u7406\u9884\u7ea6 (n)"));
            panelVIPCount4.setLayout(new BoxLayout(panelVIPCount4, BoxLayout.Y_AXIS));

            //---- lblTitle4 ----
            lblTitle4.setText("\u5f85\u5904\u7406\u9884\u7ea6 (n)");
            lblTitle4.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            lblTitle4.setAlignmentX(0.5F);
            panelVIPCount4.add(lblTitle4);

            //---- lblPendingAppointments ----
            lblPendingAppointments.setText("0");
            lblPendingAppointments.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 32));
            lblPendingAppointments.setAlignmentX(0.5F);
            panelVIPCount4.add(lblPendingAppointments);
        }
        add(panelVIPCount4);
        panelVIPCount4.setBounds(95, 375, 150, 100);

        //======== panelVIPCount5 ========
        {
            panelVIPCount5.setPreferredSize(new Dimension(150, 100));
            panelVIPCount5.setBorder(new TitledBorder("\u4f4e\u4f59\u989d\u4f1a\u5458 (n)"));
            panelVIPCount5.setLayout(new BoxLayout(panelVIPCount5, BoxLayout.Y_AXIS));

            //---- lblTitle5 ----
            lblTitle5.setText("\u4f4e\u4f59\u989d\u4f1a\u5458 (n)");
            lblTitle5.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            lblTitle5.setAlignmentX(0.5F);
            panelVIPCount5.add(lblTitle5);

            //---- lblLowBalanceMembers ----
            lblLowBalanceMembers.setText("0");
            lblLowBalanceMembers.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 32));
            lblLowBalanceMembers.setAlignmentX(0.5F);
            panelVIPCount5.add(lblLowBalanceMembers);
        }
        add(panelVIPCount5);
        panelVIPCount5.setBounds(360, 375, 150, 100);

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
    private JPanel panelVIPCount;
    private JLabel lblTitle;
    private JLabel lblTotalMembers;
    private JPanel panelVIPCount2;
    private JLabel lblTitle2;
    private JLabel lblNewMembersThisMonth;
    private JPanel panelVIPCount3;
    private JLabel lblTitle3;
    private JLabel lblActiveMemberRate;
    private JPanel panelVIPCount4;
    private JLabel lblTitle4;
    private JLabel lblPendingAppointments;
    private JPanel panelVIPCount5;
    private JLabel lblTitle5;
    private JLabel lblLowBalanceMembers;

    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
