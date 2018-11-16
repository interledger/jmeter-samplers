package org.interledger.jmeter.samplers;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import eu.luminis.jmeter.wssampler.BinaryUtils;
import eu.luminis.jmeter.wssampler.GuiUtils;
import eu.luminis.jmeter.wssampler.JMeterUtils;
import org.interledger.core.InterledgerAddress;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class IlpPacketForm {

    public static void main(String[] args) {
        JFrame frame = new JFrame("IlpPacketForm");
        frame.setContentPane(new IlpPacketForm().ilpPacketPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel ilpPacketPanel;
    private JTextField amountField;
    private JTextField addressField;
    private JTextField conditionField;
    private JTextField expiryField;
    private JTextField fulfillmentField;
    private JCheckBox fromFulfillmentCheckBox;
    private JTextArea dataField;
    private JLabel addressLabel;
    public JLabel messageField;
    public JLabel conditionLabel;
    public JLabel fulfillmentLabel;
    public JLabel amountLabel;
    public JLabel expiryLabel;
    public JLabel dataLabel;
    public JComboBox typeSelect;
    public JLabel typeLabel;

    public IlpPacketForm() {
        $$$setupUI$$$();

        messageField.setForeground(GuiUtils.getLookAndFeelColor("TextField.errorForeground"));

        addressField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                checkAddress();
            }
        });
        amountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                checkAmount();
            }
        });
        expiryField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                checkExpiry();
            }
        });
        conditionField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                checkCondition();
            }
        });
        fulfillmentField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                checkFulfillment();
            }
        });
        dataField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                checkData();
            }
        });

        fromFulfillmentCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableFulfillmentField(fromFulfillmentCheckBox.isSelected());
            }
        });
        typeSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableGui((String) typeSelect.getSelectedItem());
            }
        });
    }

    public JPanel getPanel() {
        return ilpPacketPanel;
    }

    public void clearGui() {
        fulfillmentField.setText("");
        dataField.setText("");
        fromFulfillmentCheckBox.setSelected(false);
        enableFulfillmentField(false);
    }

    private void enableGui(String selectedItem) {
        if ("PREPARE".equals(selectedItem)) {
            addressLabel.setEnabled(true);
            addressField.setEnabled(true);
            amountLabel.setEnabled(true);
            amountField.setEnabled(true);
            expiryLabel.setEnabled(true);
            expiryField.setEnabled(true);
            fromFulfillmentCheckBox.setEnabled(true);
            enableFulfillmentField(fromFulfillmentCheckBox.isSelected());
        }

        if ("FULFILL".equals(selectedItem)) {
            addressLabel.setEnabled(false);
            addressField.setEnabled(false);
            amountLabel.setEnabled(false);
            amountField.setEnabled(false);
            expiryLabel.setEnabled(false);
            expiryField.setEnabled(false);
            enableFulfillmentField(true);
        }
    }

    public void enableFulfillmentField(boolean enable) {
        conditionLabel.setEnabled(!enable);
        conditionField.setEnabled(!enable);
        fulfillmentField.setEnabled(enable);
        fulfillmentLabel.setEnabled(enable);
    }

    public String getAddress() {
        return addressField.getText();
    }

    public void setAddress(String address) {
        addressField.setText(address);
    }

    public String getAmount() {
        return amountField.getText();
    }

    public void setAmount(String amount) {
        amountField.setText(amount);
    }

    public String getExpiry() {
        return expiryField.getText();
    }

    public void setExpiry(String file) {
        expiryField.setText(file);
    }

    public String getCondition() {
        return conditionField.getText();
    }

    public void setCondition(String file) {
        conditionField.setText(file);
    }

    public boolean getConditionFromFulfillment() {
        return fromFulfillmentCheckBox.isSelected();
    }

    public void setConditionFromFulfillment(boolean enable) {
        fromFulfillmentCheckBox.setSelected(enable);
        enableFulfillmentField(enable);
    }

    public String getFulfillment() {
        return fulfillmentField.getText();
    }

    public void setFulfillment(String file) {
        fulfillmentField.setText(file);
    }

    public String getData() {
        return dataField.getText();
    }

    public void setData(String requestData) {
        dataField.setText(requestData);
    }

    private void checkAddress() {
        try {
            InterledgerAddress.of(JMeterUtils.stripJMeterVariables(addressField.getText()));
            messageField.setText(" ");
        } catch (Exception notNumber) {
            messageField.setText("Warning: ILP Address looks invalid (JMeter variables like ${var} are allowed).");
        }
    }

    private void checkAmount() {
        try {
            Integer.parseInt(JMeterUtils.stripJMeterVariables(amountField.getText()));
            messageField.setText(" ");
        } catch (Exception notNumber) {
            messageField.setText("Warning: Amount must be a valid integer. (JMeter variables like ${var} allowed).");
        }
    }

    private void checkExpiry() {
        try {
            Integer.parseInt(JMeterUtils.stripJMeterVariables(expiryField.getText()));
            messageField.setText(" ");
        } catch (Exception notNumber) {
            messageField.setText("Warning: Expiry must be a valid integer. (JMeter variables like ${var} allowed).");
        }
    }

    private void checkCondition() {
        if (!fromFulfillmentCheckBox.isSelected()) {
            try {
                byte[] condition = BinaryUtils.parseBinaryString(JMeterUtils.stripJMeterVariables(conditionField.getText()));
                if (condition.length != 32) {
                    messageField.setText("Warning: Condition must 32 bytes. (Ignore if using variables)");
                }
                messageField.setText(" ");
            } catch (NumberFormatException notNumber) {
                messageField.setText("Warning: Condition must be expressed as hex. (JMeter variables like ${var} are allowed).");
            }
        }
    }

    private void checkFulfillment() {
        if (fromFulfillmentCheckBox.isSelected()) {
            try {
                byte[] fulfillment = BinaryUtils.parseBinaryString(JMeterUtils.stripJMeterVariables(fulfillmentField.getText()));
                if (fulfillment.length != 32) {
                    messageField.setText("Warning: Fulfillment must 32 bytes. (Ignore if using variables)");
                }
                messageField.setText(" ");
            } catch (NumberFormatException notNumber) {
                messageField.setText("Warning: Fulfillment must be expressed as hex. (JMeter variables like ${var} are allowed).");
            }
        }
    }

    private void checkData() {
        try {
            BinaryUtils.parseBinaryString(JMeterUtils.stripJMeterVariables(dataField.getText()));
            messageField.setText(" ");
        } catch (NumberFormatException notNumber) {
            messageField.setText("Warning: Request data must be expressed as hex. (JMeter variables like ${var} are allowed).");
        }
    }

    public void setData(IlpPacketForm data) {
        amountField.setText(data.getAmount());
        addressField.setText(data.getAddress());
        fulfillmentField.setText(data.getFulfillment());
        dataField.setText(data.getData());
    }

    public void getData(IlpPacketForm data) {
        data.setAmount(amountField.getText());
        data.setAddress(addressField.getText());
        data.setFulfillment(fulfillmentField.getText());
        data.setData(dataField.getText());
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        ilpPacketPanel = new JPanel();
        ilpPacketPanel.setLayout(new FormLayout("fill:87px:noGrow,fill:12px:noGrow,left:121dlu:noGrow,fill:102px:noGrow,fill:93px:noGrow,fill:314px:noGrow", "center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow"));
        ilpPacketPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), "ILP Packet"));
        addressLabel = new JLabel();
        addressLabel.setText("Address: ");
        CellConstraints cc = new CellConstraints();
        ilpPacketPanel.add(addressLabel, cc.xy(1, 9));
        amountLabel = new JLabel();
        amountLabel.setText("Amount: ");
        ilpPacketPanel.add(amountLabel, cc.xy(1, 11));
        conditionLabel = new JLabel();
        conditionLabel.setText("Condition: ");
        ilpPacketPanel.add(conditionLabel, cc.xy(1, 13));
        expiryField = new JTextField();
        expiryField.setColumns(10);
        ilpPacketPanel.add(expiryField, cc.xy(6, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        dataLabel = new JLabel();
        dataLabel.setText("Data: ");
        ilpPacketPanel.add(dataLabel, cc.xy(1, 19));
        expiryLabel = new JLabel();
        expiryLabel.setText("Expiry (ms): ");
        ilpPacketPanel.add(expiryLabel, cc.xy(5, 11));
        fulfillmentLabel = new JLabel();
        fulfillmentLabel.setText("Fulfillment: ");
        ilpPacketPanel.add(fulfillmentLabel, cc.xy(1, 17));
        fromFulfillmentCheckBox = new JCheckBox();
        fromFulfillmentCheckBox.setText("From fulfillment");
        ilpPacketPanel.add(fromFulfillmentCheckBox, cc.xyw(1, 15, 3));
        final JScrollPane scrollPane1 = new JScrollPane();
        ilpPacketPanel.add(scrollPane1, cc.xyw(2, 19, 5, CellConstraints.FILL, CellConstraints.FILL));
        dataField = new JTextArea();
        dataField.setLineWrap(true);
        dataField.setRows(5);
        scrollPane1.setViewportView(dataField);
        addressField = new JTextField();
        ilpPacketPanel.add(addressField, cc.xyw(3, 9, 4, CellConstraints.FILL, CellConstraints.DEFAULT));
        amountField = new JTextField();
        ilpPacketPanel.add(amountField, cc.xy(3, 11, CellConstraints.FILL, CellConstraints.DEFAULT));
        conditionField = new JTextField();
        conditionField.setMinimumSize(new Dimension(70, 30));
        ilpPacketPanel.add(conditionField, cc.xyw(3, 13, 4, CellConstraints.FILL, CellConstraints.DEFAULT));
        messageField = new JLabel();
        messageField.setText(" ");
        ilpPacketPanel.add(messageField, cc.xyw(3, 7, 4));
        fulfillmentField = new JTextField();
        ilpPacketPanel.add(fulfillmentField, cc.xyw(3, 17, 4, CellConstraints.FILL, CellConstraints.DEFAULT));
        typeSelect = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("PREPARE");
        defaultComboBoxModel1.addElement("FULFILL");
        typeSelect.setModel(defaultComboBoxModel1);
        ilpPacketPanel.add(typeSelect, cc.xy(3, 3));
        typeLabel = new JLabel();
        typeLabel.setOpaque(true);
        typeLabel.setText("Type:");
        ilpPacketPanel.add(typeLabel, cc.xy(1, 3));
        addressLabel.setLabelFor(addressField);
        amountLabel.setLabelFor(amountField);
        conditionLabel.setLabelFor(conditionField);
        dataLabel.setLabelFor(dataField);
        expiryLabel.setLabelFor(expiryField);
        fulfillmentLabel.setLabelFor(fulfillmentField);
        typeLabel.setLabelFor(typeSelect);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return ilpPacketPanel;
    }
}
