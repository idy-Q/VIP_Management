/*
 * Created by JFormDesigner on YYYY-MM-DD
 */

package io.github.idyq.view.Panel.CardSettings;

import io.github.idyq.model.entity.CardSettings;
import io.github.idyq.service.VIPTransaction.CardSettingsService;
import io.github.idyq.service.VIPTransaction.CardSettingsServiceImpl;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;

/**
 * 修改充值方案 对话框
 */
public class UpdateSettingsDialog extends JDialog {
    private final CardSettingsService settingsService = new CardSettingsServiceImpl();
    private CardSettings currentSettings;

    public UpdateSettingsDialog(Window owner, CardSettings settings) {
        super(owner);
        this.currentSettings = settings;
        initComponents();
        // 将传入的实体预填到文本框
        prefillFields();
    }

    /** 点击“确认修改”按钮后的事件逻辑 */
    private void btnConfirmAction(ActionEvent e) {
        // 1. 从文本框读取并校验
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

        // 2. 更新实体对象
        currentSettings.setPlanName(planName);
        currentSettings.setChargeAmount(chargeAmt);
        currentSettings.setBonusAmount(bonusAmt);
        currentSettings.setEffectivePeriod(period);

        // 3. 调用 Service 更新数据库
        try {
            boolean ok = settingsService.update(currentSettings);
            if (ok) {
                JOptionPane.showMessageDialog(this, "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // 关闭对话框
            } else {
                JOptionPane.showMessageDialog(this, "修改失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "修改出错：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 点击“退出”按钮，直接关闭对话框 */
    private void btnExitAction(ActionEvent e) {
        this.dispose();
    }


    /** 把传入的实体字段值预先填到文本框 */
    private void prefillFields() {
        txtSettingID.setText(String.valueOf(currentSettings.getSettingID()));
        txtPlanName.setText(currentSettings.getPlanName());
        txtChargeAmount.setText(String.valueOf(currentSettings.getChargeAmount()));
        txtBonusAmount.setText(String.valueOf(currentSettings.getBonusAmount()));
        Integer ep = currentSettings.getEffectivePeriod();
        txtEffectivePeriod.setText(ep != null ? String.valueOf(ep) : "");
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        labelTitle = new JLabel();
        labelID = new JLabel();
        txtSettingID = new JTextField();
        labelPlanName = new JLabel();
        txtPlanName = new JTextField();
        labelCharge = new JLabel();
        txtChargeAmount = new JTextField();
        labelBonus = new JLabel();
        txtBonusAmount = new JTextField();
        labelPeriod = new JLabel();
        txtEffectivePeriod = new JTextField();
        btnConfirm = new JButton();
        btnExit = new JButton();

        //======== this ========
        setTitle("修改充值方案");
        setPreferredSize(new Dimension(450, 410));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- labelTitle ----
        labelTitle.setText("修改充值方案");
        labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        labelTitle.setFont(labelTitle.getFont().deriveFont(Font.BOLD | Font.ITALIC, labelTitle.getFont().getSize() + 8f));
        contentPane.add(labelTitle);
        labelTitle.setBounds(130, 20, 180, 30);

        //---- labelID ----
        labelID.setText("方案 ID：");
        labelID.setFont(labelID.getFont().deriveFont(labelID.getFont().getSize() + 2f));
        contentPane.add(labelID);
        labelID.setBounds(60, 80, 80, 30);

        contentPane.add(txtSettingID);
        txtSettingID.setBounds(150, 80, 100, 30);
        txtSettingID.setEditable(false); // 只读

        //---- labelPlanName ----
        labelPlanName.setText("方案名称：");
        labelPlanName.setFont(labelPlanName.getFont().deriveFont(labelPlanName.getFont().getSize() + 2f));
        contentPane.add(labelPlanName);
        labelPlanName.setBounds(60, 130, 80, 30);
        contentPane.add(txtPlanName);
        txtPlanName.setBounds(150, 130, 200, 30);

        //---- labelCharge ----
        labelCharge.setText("支付金额：");
        labelCharge.setFont(labelCharge.getFont().deriveFont(labelCharge.getFont().getSize() + 2f));
        contentPane.add(labelCharge);
        labelCharge.setBounds(60, 180, 80, 30);
        contentPane.add(txtChargeAmount);
        txtChargeAmount.setBounds(150, 180, 120, 30);

        //---- labelBonus ----
        labelBonus.setText("赠送金额：");
        labelBonus.setFont(labelBonus.getFont().deriveFont(labelBonus.getFont().getSize() + 2f));
        contentPane.add(labelBonus);
        labelBonus.setBounds(60, 230, 80, 30);
        contentPane.add(txtBonusAmount);
        txtBonusAmount.setBounds(150, 230, 120, 30);

        //---- labelPeriod ----
        labelPeriod.setText("有效期(天)：");
        labelPeriod.setFont(labelPeriod.getFont().deriveFont(labelPeriod.getFont().getSize() + 2f));
        contentPane.add(labelPeriod);
        labelPeriod.setBounds(60, 280, 90, 30);
        contentPane.add(txtEffectivePeriod);
        txtEffectivePeriod.setBounds(160, 280, 80, 30);

        //---- btnConfirm ----
        btnConfirm.setText("确认修改");
        btnConfirm.setFont(btnConfirm.getFont().deriveFont(Font.BOLD, btnConfirm.getFont().getSize() + 2f));
        btnConfirm.addActionListener(e -> btnConfirmAction(e));
        contentPane.add(btnConfirm);
        btnConfirm.setBounds(new Rectangle(new Point(100, 330), btnConfirm.getPreferredSize()));

        //---- btnExit ----
        btnExit.setText("退出");
        btnExit.setFont(btnExit.getFont().deriveFont(Font.BOLD, btnExit.getFont().getSize() + 2f));
        btnExit.addActionListener(e -> btnExitAction(e));
        contentPane.add(btnExit);
        btnExit.setBounds(new Rectangle(new Point(250, 330), btnExit.getPreferredSize()));

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
    private JLabel labelID;
    private JTextField txtSettingID;
    private JLabel labelPlanName;
    private JTextField txtPlanName;
    private JLabel labelCharge;
    private JTextField txtChargeAmount;
    private JLabel labelBonus;
    private JTextField txtBonusAmount;
    private JLabel labelPeriod;
    private JTextField txtEffectivePeriod;
    private JButton btnConfirm;
    private JButton btnExit;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
