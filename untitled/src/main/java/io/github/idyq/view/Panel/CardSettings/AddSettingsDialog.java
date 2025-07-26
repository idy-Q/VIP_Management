/*
 * Created by JFormDesigner on Thu Jun 05 16:55:05 CST 2025
 */

package io.github.idyq.view.Panel.CardSettings;

import io.github.idyq.model.entity.CardSettings;
import io.github.idyq.service.VIPTransaction.CardSettingsService;
import io.github.idyq.service.VIPTransaction.CardSettingsServiceImpl;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.swing.*;

/**
 * @author guox100
 */
public class AddSettingsDialog extends JDialog {
    private final CardSettingsService settingsService = new CardSettingsServiceImpl();

    public AddSettingsDialog(Window owner) {
        super(owner);
        initComponents();
    }

    /** 点击“确定”按钮后的事件逻辑 */
    private void btnSaveAction(ActionEvent e) {
        String planName = txtPlanName.getText().trim();
        if (planName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写方案名称", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double chargeAmt;
        try {
            chargeAmt = Double.parseDouble(txtChargeAmount.getText().trim());
            if (chargeAmt <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入合法的“支付金额”", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double bonusAmt;
        try {
            bonusAmt = Double.parseDouble(txtBonusAmount.getText().trim());
            if (bonusAmt < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入合法的“赠送金额”", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer period = null;
        String periodText = txtEffectivePeriod.getText().trim();
        if (!periodText.isEmpty()) {
            try {
                int p = Integer.parseInt(periodText);
                if (p <= 0) throw new NumberFormatException();
                period = p;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "“有效期”必须为正整数或留空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // 构造实体
        CardSettings settings = new CardSettings();
        settings.setPlanName(planName);
        settings.setChargeAmount(chargeAmt);
        settings.setBonusAmount(bonusAmt);
        settings.setEffectivePeriod(period);

        // 【2】为 createdAt 字段赋值（解决 NOT NULL 约束）
        settings.setCreatedAt(LocalDateTime.now());

        // 调用 Service 插入数据库
        try {
            boolean ok = settingsService.insert(settings);
            if (ok) {
                JOptionPane.showMessageDialog(this, "新增成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "新增失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "新增出错：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 点击“取消”按钮，直接关闭对话框 */
    private void btnCancelAction(ActionEvent e) {
        this.dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        labelTitle = new JLabel();
        labelPlanName = new JLabel();
        txtPlanName = new JTextField();
        labelCharge = new JLabel();
        txtChargeAmount = new JTextField();
        labelBonus = new JLabel();
        txtBonusAmount = new JTextField();
        labelPeriod = new JLabel();
        txtEffectivePeriod = new JTextField();
        btnSave = new JButton();
        btnCancel = new JButton();

        //======== this ========
        setTitle("新增充值方案");
        setPreferredSize(new Dimension(400, 380));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- labelTitle ----
        labelTitle.setText("新增充值方案");
        labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        labelTitle.setFont(labelTitle.getFont().deriveFont(Font.BOLD | Font.ITALIC, labelTitle.getFont().getSize() + 8f));
        contentPane.add(labelTitle);
        labelTitle.setBounds(110, 20, 180, 30);

        //---- labelPlanName ----
        labelPlanName.setText("方案名称：");
        labelPlanName.setFont(labelPlanName.getFont().deriveFont(labelPlanName.getFont().getSize() + 2f));
        contentPane.add(labelPlanName);
        labelPlanName.setBounds(40, 80, 80, 30);
        contentPane.add(txtPlanName);
        txtPlanName.setBounds(130, 80, 200, 30);

        //---- labelCharge ----
        labelCharge.setText("支付金额：");
        labelCharge.setFont(labelCharge.getFont().deriveFont(labelCharge.getFont().getSize() + 2f));
        contentPane.add(labelCharge);
        labelCharge.setBounds(40, 130, 80, 30);
        contentPane.add(txtChargeAmount);
        txtChargeAmount.setBounds(130, 130, 120, 30);

        //---- labelBonus ----
        labelBonus.setText("赠送金额：");
        labelBonus.setFont(labelBonus.getFont().deriveFont(labelBonus.getFont().getSize() + 2f));
        contentPane.add(labelBonus);
        labelBonus.setBounds(40, 180, 80, 30);
        contentPane.add(txtBonusAmount);
        txtBonusAmount.setBounds(130, 180, 120, 30);

        //---- labelPeriod ----
        labelPeriod.setText("有效期(天)：");
        labelPeriod.setFont(labelPeriod.getFont().deriveFont(labelPeriod.getFont().getSize() + 2f));
        contentPane.add(labelPeriod);
        labelPeriod.setBounds(40, 230, 90, 30);
        contentPane.add(txtEffectivePeriod);
        txtEffectivePeriod.setBounds(140, 230, 80, 30);

        //---- btnSave ----
        btnSave.setText("确定");
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD, btnSave.getFont().getSize() + 2f));
        btnSave.addActionListener(e -> btnSaveAction(e));
        contentPane.add(btnSave);
        btnSave.setBounds(90, 280, 100, 40);

        //---- btnCancel ----
        btnCancel.setText("取消");
        btnCancel.setFont(btnCancel.getFont().deriveFont(Font.BOLD, btnCancel.getFont().getSize() + 2f));
        btnCancel.addActionListener(e -> btnCancelAction(e));
        contentPane.add(btnCancel);
        btnCancel.setBounds(210, 280, 100, 40);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for (int i = 0; i < contentPane.getComponentCount(); i++) {
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
    private JLabel labelTitle;
    private JLabel labelPlanName;
    private JTextField txtPlanName;
    private JLabel labelCharge;
    private JTextField txtChargeAmount;
    private JLabel labelBonus;
    private JTextField txtBonusAmount;
    private JLabel labelPeriod;
    private JTextField txtEffectivePeriod;
    private JButton btnSave;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
