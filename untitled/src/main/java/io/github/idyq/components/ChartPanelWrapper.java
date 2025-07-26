package io.github.idyq.components;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import javax.swing.*;
import java.awt.*;

public class ChartPanelWrapper extends JPanel {
    private ChartPanel chartPanel;
    private JLabel placeholder;

    public ChartPanelWrapper() {
        initDesignTimeView();
    }

    private void initDesignTimeView() {
        setLayout(new BorderLayout());

        // 修复边框设置 - 使用两个边框参数
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC)), // 外边框（线框）
                BorderFactory.createEmptyBorder(10, 10, 10, 10)    // 内边框（内边距）
        ));

        setBackground(new Color(0xF8F8F8));

        placeholder = new JLabel(
                "<html><div style='text-align:center;color:#666;font-size:12px;padding:10px;'>" +
                        "<b>折线图显示区域</b><br><br>" +
                        "• 设计时预览不可用<br>" +
                        "• 运行时将显示实际图表<br>" +
                        "• 默认尺寸: 600×400" +
                        "</div></html>",
                SwingConstants.CENTER
        );

        add(placeholder, BorderLayout.CENTER);
    }

    public void setChart(JFreeChart chart) {
        removeAll();

        if (chart != null) {
            chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(600, 400));
            add(chartPanel, BorderLayout.CENTER);
        } else {
            // 如果传入null，恢复占位符
            add(placeholder, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}