package io.github.idyq.view.Panel.ServiceItem;

import io.github.idyq.model.entity.ServiceItem;
import io.github.idyq.service.serviceitem.ServiceItemService;
import io.github.idyq.service.serviceitem.ServiceItemServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * 修改服务项目对话框
 */
public class UpdateServiceItemDialog extends JDialog {
    private final ServiceItemService itemService = new ServiceItemServiceImpl();
    private final ServiceItem originalItem;

    public UpdateServiceItemDialog(Window owner, ServiceItem item) {
        super(owner);
        this.originalItem = item;
        initComponents();
        loadFieldsFromServiceItem();
    }

    /** 把原始 ServiceItem 的各字段预填到文本框 */
    private void loadFieldsFromServiceItem() {
        txtItemID.setText(String.valueOf(originalItem.getServiceItemID()));
        txtItemName.setText(originalItem.getName());
        txtDescription.setText(originalItem.getDescription());
        txtPrice.setText(String.valueOf(originalItem.getPrice()));
        txtDuration.setText(String.valueOf(originalItem.getDuration()));
    }

    /** 点击“确认保存”时，把修改的字段写回 originalItem，然后调用 update(...) */
    private void btnConfirmAction(ActionEvent e) {
        // 1. 读取并校验“项目名称”（必填）
        String itemName = txtItemName.getText().trim();
        if (itemName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写“项目名称”", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 2. “描述”可留空
        String desc = txtDescription.getText().trim();

        // 3. 读取并校验“价格”
        double price;
        try {
            price = Double.parseDouble(txtPrice.getText().trim());
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex2) {
            JOptionPane.showMessageDialog(this, "“价格”应为非负数", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 4. 读取并校验“时长”
        int duration;
        try {
            duration = Integer.parseInt(txtDuration.getText().trim());
            if (duration < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex2) {
            JOptionPane.showMessageDialog(this, "“时长”应为非负整数", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 5. 把新值写回 originalItem
        originalItem.setName(itemName);
        originalItem.setDescription(desc);
        originalItem.setPrice(price);
        originalItem.setDuration(duration);


        // 6. 调用 Service.update(...)
        try {
            boolean ok = itemService.update(originalItem);
            if (ok) {
                JOptionPane.showMessageDialog(this, "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "修改失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "修改时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnCancelAction(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        lblTitle = new JLabel();
        lblItemID = new JLabel();
        txtItemID = new JTextField();
        lblItemName = new JLabel();
        txtItemName = new JTextField();
        lblDescription = new JLabel();
        txtDescription = new JTextField();
        lblPrice = new JLabel();
        txtPrice = new JTextField();
        lblDuration = new JLabel();
        txtDuration = new JTextField();
        btnConfirm = new JButton();
        btnCancel = new JButton();

        //======== this ========
        setTitle("修改服务项目");
        setModal(true);
        setResizable(false);
        setPreferredSize(new Dimension(400, 380));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- lblTitle ----
        lblTitle.setText("修改服务项目");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD | Font.ITALIC, lblTitle.getFont().getSize() + 8f));
        contentPane.add(lblTitle);
        lblTitle.setBounds(110, 15, 180, 30);

        //---- lblItemID ----
        lblItemID.setText("项目 ID：");
        {
            Font f = lblItemID.getFont();
            lblItemID.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblItemID);
        lblItemID.setBounds(40, 65, 80, 30);

        //---- txtItemID ----
        txtItemID.setEditable(false);
        contentPane.add(txtItemID);
        txtItemID.setBounds(130, 65, 220, 30);

        //---- lblItemName ----
        lblItemName.setText("项目名称：");
        {
            Font f = lblItemName.getFont();
            lblItemName.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblItemName);
        lblItemName.setBounds(40, 105, 80, 30);

        //---- txtItemName ----
        contentPane.add(txtItemName);
        txtItemName.setBounds(130, 105, 220, 30);

        //---- lblDescription ----
        lblDescription.setText("描述：");
        {
            Font f = lblDescription.getFont();
            lblDescription.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblDescription);
        lblDescription.setBounds(40, 145, 80, 30);

        //---- txtDescription ----
        contentPane.add(txtDescription);
        txtDescription.setBounds(130, 145, 220, 30);

        //---- lblPrice ----
        lblPrice.setText("价格(元)：");
        {
            Font f = lblPrice.getFont();
            lblPrice.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblPrice);
        lblPrice.setBounds(40, 185, 80, 30);

        //---- txtPrice ----
        contentPane.add(txtPrice);
        txtPrice.setBounds(130, 185, 220, 30);

        //---- lblDuration ----
        lblDuration.setText("时长(分钟)：");
        {
            Font f = lblDuration.getFont();
            lblDuration.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblDuration);
        lblDuration.setBounds(40, 225, 90, 30);

        //---- txtDuration ----
        contentPane.add(txtDuration);
        txtDuration.setBounds(130, 225, 220, 30);

        //---- btnConfirm ----
        btnConfirm.setText("确认保存");
        {
            Font f = btnConfirm.getFont();
            btnConfirm.setFont(f.deriveFont(Font.BOLD, f.getSize() + 2f));
        }
        btnConfirm.addActionListener(e -> btnConfirmAction(e));
        contentPane.add(btnConfirm);
        btnConfirm.setBounds(90, 280, 100, 40);

        //---- btnCancel ----
        btnCancel.setText("取消");
        {
            Font f = btnCancel.getFont();
            btnCancel.setFont(f.deriveFont(Font.BOLD, f.getSize() + 2f));
        }
        btnCancel.addActionListener(e -> btnCancelAction(e));
        contentPane.add(btnCancel);
        btnCancel.setBounds(220, 280, 100, 40);

        {
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width  = Math.max(bounds.x + bounds.width,  preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width  += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblTitle;
    private JLabel lblItemID;
    private JTextField txtItemID;
    private JLabel lblItemName;
    private JTextField txtItemName;
    private JLabel lblDescription;
    private JTextField txtDescription;
    private JLabel lblPrice;
    private JTextField txtPrice;
    private JLabel lblDuration;
    private JTextField txtDuration;
    private JButton btnConfirm;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
