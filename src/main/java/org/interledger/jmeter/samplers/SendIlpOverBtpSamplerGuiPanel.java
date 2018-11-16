package org.interledger.jmeter.samplers;

import javax.swing.*;

public class SendIlpOverBtpSamplerGuiPanel extends AbstractBtpSamplerGuiPanel {

    private IlpPacketForm ilpPacketForm;

    public SendIlpOverBtpSamplerGuiPanel() {
        init();
    }

    @Override
    protected void init() {
        super.init();

        ilpPacketForm = new IlpPacketForm();
        this.add(ilpPacketForm.getPanel());
        ilpPacketForm.getPanel().setAlignmentX(JComponent.LEFT_ALIGNMENT);
    }

    @Override
    protected void clearGui() {
        super.clearGui();
        ilpPacketForm.clearGui();
    }


    public IlpPacketForm getIlpPacketForm() {
        return ilpPacketForm;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.getContentPane().add(new SendIlpOverBtpSamplerGuiPanel());
        frame.setVisible(true);
    }
}