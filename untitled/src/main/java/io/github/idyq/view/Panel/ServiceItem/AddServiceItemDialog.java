package io.github.idyq.view.Panel.ServiceItem;

import io.github.idyq.model.entity.ServiceItem;
import io.github.idyq.service.serviceitem.ServiceItemService;
import io.github.idyq.service.serviceitem.ServiceItemServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * 新增服务项目对话框
 */
public class AddServiceItemDialog extends JDialog {
        private final ServiceItemService itemService = new ServiceItemServiceImpl();

    public AddServiceItemDialog(Window owner) {
        super(owner);
        initComponents();
    }

    /** 点击“确认”按钮后的逻辑 */
    private void btnSaveAction(ActionEvent e) {
        // 1. 从文本框中获取用户输入
        String itemName = txtItemName.getText().trim();
        String desc = txtDescription.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String durationStr = txtDuration.getText().trim();

        // 校验：名称和价格、时长均为必填；价格和时长应为正数
        if (itemName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写“项目名称”", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex2) {
            JOptionPane.showMessageDialog(this, "“价格”应为非负数", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int duration;
        try {
            duration = Integer.parseInt(durationStr);
            if (duration < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex2) {
            JOptionPane.showMessageDialog(this, "“时长”应为非负整数", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. 构造 ServiceItem 对象
        ServiceItem si = new ServiceItem();
        si.setName(itemName);
        si.setDescription(desc);
        si.setPrice(price);
        si.setDuration(duration);
        si.setCreatedAt(LocalDateTime.now());

        // 3. 调用 Service.create(...)
        try {
            boolean ok = itemService.create(si);
            if (ok) {
                JOptionPane.showMessageDialog(this, "新增成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "新增失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "新增时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnCancelAction(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        lblTitle = new JLabel();
        lblItemName = new JLabel();
        txtItemName = new JTextField();
        lblDescription = new JLabel();
        txtDescription = new JTextField();
        lblPrice = new JLabel();
        txtPrice = new JTextField();
        lblDuration = new JLabel();
        txtDuration = new JTextField();
        btnSave = new JButton();
        btnCancel = new JButton();

        //======== this ========
        setTitle("新增服务项目");
        setModal(true);
        setResizable(false);
        setPreferredSize(new Dimension(400, 350));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- lblTitle ----
        lblTitle.setText("新增服务项目");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD | Font.ITALIC, lblTitle.getFont().getSize() + 8f));
        contentPane.add(lblTitle);
        lblTitle.setBounds(110, 15, 180, 30);

        //---- lblItemName ----
        lblItemName.setText("项目名称：");
        {
            Font f = lblItemName.getFont();
            lblItemName.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblItemName);
        lblItemName.setBounds(40, 65, 80, 30);

        //---- txtItemName ----
        contentPane.add(txtItemName);
        txtItemName.setBounds(130, 65, 220, 30);

        //---- lblDescription ----
        lblDescription.setText("描述：");
        {
            Font f = lblDescription.getFont();
            lblDescription.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblDescription);
        lblDescription.setBounds(40, 105, 80, 30);

        //---- txtDescription ----
        contentPane.add(txtDescription);
        txtDescription.setBounds(130, 105, 220, 30);

        //---- lblPrice ----
        lblPrice.setText("价格(元)：");
        {
            Font f = lblPrice.getFont();
            lblPrice.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblPrice);
        lblPrice.setBounds(40, 145, 80, 30);

        //---- txtPrice ----
        contentPane.add(txtPrice);
        txtPrice.setBounds(130, 145, 220, 30);

        //---- lblDuration ----
        lblDuration.setText("时长(分钟)：");
        {
            Font f = lblDuration.getFont();
            lblDuration.setFont(f.deriveFont(f.getSize() + 2f));
        }
        contentPane.add(lblDuration);
        lblDuration.setBounds(40, 185, 90, 30);

        //---- txtDuration ----
        contentPane.add(txtDuration);
        txtDuration.setBounds(130, 185, 220, 30);

        //---- btnSave ----
        btnSave.setText("确认保存");
        {
            Font f = btnSave.getFont();
            btnSave.setFont(f.deriveFont(Font.BOLD, f.getSize() + 2f));
        }
        btnSave.addActionListener(e -> btnSaveAction(e));
        contentPane.add(btnSave);
        btnSave.setBounds(90, 240, 100, 40);

        //---- btnCancel ----
        btnCancel.setText("取消");
        {
            Font f = btnCancel.getFont();
            btnCancel.setFont(f.deriveFont(Font.BOLD, f.getSize() + 2f));
        }
        btnCancel.addActionListener(e -> btnCancelAction(e));
        contentPane.add(btnCancel);
        btnCancel.setBounds(220, 240, 100, 40);

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
    private JLabel lblItemName;
    private JTextField txtItemName;
    private JLabel lblDescription;
    private JTextField txtDescription;
    private JLabel lblPrice;
    private JTextField txtPrice;
    private JLabel lblDuration;
    private JTextField txtDuration;
    private JButton btnSave;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
