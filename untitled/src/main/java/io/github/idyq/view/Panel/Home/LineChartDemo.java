package io.github.idyq.view.Panel.Home;

import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineChartDemo extends JFrame {

    public LineChartDemo(String title) {
        super(title);

        // 1. 构造数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(200, "充值", "2025-05-01");
        dataset.addValue(150, "消费", "2025-05-01");
        dataset.addValue(220, "充值", "2025-05-02");
        dataset.addValue(180, "消费", "2025-05-02");
        // … 可按实际从数据库加载

        // 2. 创建折线图
        JFreeChart chart = ChartFactory.createLineChart(
                "本月收入／消费趋势",  // 图表标题
                "日期",           // X 轴标签
                "金额 (元)",      // Y 轴标签
                dataset
        );

        // 3. 把图表放到面板
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {
        // 4. 在 EDT 中启动
        javax.swing.SwingUtilities.invokeLater(() -> {
            LineChartDemo example = new LineChartDemo("折线图示例");
            example.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            example.pack();
            example.setLocationRelativeTo(null);
            example.setVisible(true);
        });
    }
}
