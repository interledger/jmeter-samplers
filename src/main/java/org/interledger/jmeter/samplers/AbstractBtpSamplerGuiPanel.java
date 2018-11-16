package org.interledger.jmeter.samplers;

import eu.luminis.jmeter.wssampler.GuiUtils;
import eu.luminis.jmeter.wssampler.JErrorMessageLabel;
import eu.luminis.jmeter.wssampler.JMeterUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;

import static javax.swing.BoxLayout.Y_AXIS;


abstract public class AbstractBtpSamplerGuiPanel extends JPanel {

    protected JTextField btpRequestIdField;
    protected JTextField btpResponseTimeoutField;

    protected void init() {
        this.setLayout(new BoxLayout(this, Y_AXIS));
        JPanel btpPanel = createBtpPanel();
        this.add(btpPanel);
        btpPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    }

    protected void clearGui() {
        btpRequestIdField.setText("");
        btpResponseTimeoutField.setText("");
    }

    /**
     * Creates a standard connection (settings) panel, including the choice to setup a new connection or reusing an existing one.
     * @return the connection panel
     */
    protected JPanel createBtpPanel() {

        JPanel btpPanel = new JPanel();
        {
            btpPanel.setLayout(new BoxLayout(btpPanel, Y_AXIS));
            btpPanel.setBorder(BorderFactory.createTitledBorder("BTP"));

            JPanel btpRequestIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            {
                btpRequestIdPanel.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));
                JLabel requestIdLabel = new JLabel("Request ID:");
                btpRequestIdPanel.add(requestIdLabel);
                btpRequestIdField = new JTextField();
                btpRequestIdField.setColumns(10);
                btpRequestIdPanel.add(btpRequestIdField);
                JLabel requestIdErrorLabel = new JErrorMessageLabel();
                requestIdErrorLabel.setForeground(GuiUtils.getLookAndFeelColor("TextField.errorForeground"));
                addIntegerRangeCheck(btpRequestIdField, 1, Integer.MAX_VALUE, requestIdErrorLabel);
                btpRequestIdPanel.add(requestIdErrorLabel);
            }
            btpPanel.add(btpRequestIdPanel);

            JPanel btpResponseTimeoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            {
                btpResponseTimeoutPanel.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 0));
                JLabel responseTimeoutLabel = new JLabel("Response Timeout:");
                btpResponseTimeoutPanel.add(responseTimeoutLabel);
                btpResponseTimeoutField = new JTextField();
                btpResponseTimeoutField.setColumns(10);
                btpResponseTimeoutPanel.add(btpResponseTimeoutField);
                JLabel responseTimeoutErrorLabel = new JErrorMessageLabel();
                responseTimeoutErrorLabel.setForeground(GuiUtils.getLookAndFeelColor("TextField.errorForeground"));
                addIntegerRangeCheck(btpResponseTimeoutField, 0, Integer.MAX_VALUE, responseTimeoutErrorLabel);
                btpResponseTimeoutPanel.add(responseTimeoutErrorLabel);
            }
            btpPanel.add(btpResponseTimeoutPanel);

        }
        return btpPanel;
    }


    protected void addIntegerRangeCheck(final JTextField input, int min, int max, JLabel errorMsgField) {
        input.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkIntegerInRange(e.getDocument(), min, max, input, errorMsgField);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkIntegerInRange(e.getDocument(), min, max, input, errorMsgField);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkIntegerInRange(e.getDocument(), min, max, input, errorMsgField);
            }
        });
    }

    private boolean checkIntegerInRange(Document doc, int min, int max, JTextField field, JLabel errorMsgField) {
        boolean ok = false;
        boolean isNumber = false;

        try {
            String literalContent = JMeterUtils.stripJMeterVariables(doc.getText(0, doc.getLength()));
            if (literalContent.trim().length() > 0) {
                int value = Integer.parseInt(literalContent);
                ok = value >= min && value <= max;
                isNumber = true;
            } else {
                // Could be just a JMeter variable (e.g. ${port}), which should not be refused!
                ok = true;
            }
        }
        catch (NumberFormatException nfe) {
        }
        catch (BadLocationException e) {
            // Impossible
        }
        if (field != null)
            if (ok) {
                field.setForeground(GuiUtils.getLookAndFeelColor("TextField.foreground"));
                if (errorMsgField != null)
                    errorMsgField.setText("");
            }
            else {
                field.setForeground(GuiUtils.getLookAndFeelColor("TextField.errorForeground"));
                if (isNumber && errorMsgField != null)
                    errorMsgField.setText("Value must >= " + min + " and <= " + max);
            }
        return ok;
    }

}