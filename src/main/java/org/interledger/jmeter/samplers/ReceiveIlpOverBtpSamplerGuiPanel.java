package org.interledger.jmeter.samplers;

import javax.swing.*;

public class ReceiveIlpOverBtpSamplerGuiPanel extends AbstractBtpSamplerGuiPanel {

    public ReceiveIlpOverBtpSamplerGuiPanel() {
        init();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void clearGui() {
        super.clearGui();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.getContentPane().add(new ReceiveIlpOverBtpSamplerGuiPanel());
        frame.setVisible(true);
    }
}