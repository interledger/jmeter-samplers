package org.interledger.jmeter.samplers;

import javax.swing.*;

public class AuthOverBtpSamplerGuiPanel extends AbstractBtpSamplerGuiPanel {

    private BtpAuthForm btpAuthForm;

    public AuthOverBtpSamplerGuiPanel() {
        init();
    }

    @Override
    protected void init() {
        super.init();
        btpAuthForm = new BtpAuthForm();
        this.add(btpAuthForm.btpAuthPanel);
        btpAuthForm.btpAuthPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    }

    @Override
    protected void clearGui() {
        super.clearGui();
        btpAuthForm.clearGui();
    }


    public BtpAuthForm getBtpAuthForm() {
        return btpAuthForm;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.getContentPane().add(new AuthOverBtpSamplerGuiPanel());
        frame.setVisible(true);
    }
}