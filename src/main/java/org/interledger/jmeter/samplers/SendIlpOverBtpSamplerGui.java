package org.interledger.jmeter.samplers;

import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import java.awt.*;


public class SendIlpOverBtpSamplerGui extends AbstractSamplerGui {

    private SendIlpOverBtpSamplerGuiPanel settingsPanel;

    public SendIlpOverBtpSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        settingsPanel = new SendIlpOverBtpSamplerGuiPanel();
        add(settingsPanel, BorderLayout.CENTER);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        settingsPanel.clearGui();
    }

    @Override
    public String getStaticLabel() {
        return "ILP Prepare over BTP";
    }

    @Override
    public String getLabelResource() {
        return null;
    }

    @Override
    public TestElement createTestElement() {
        SendIlpOverBtpSampler element = new SendIlpOverBtpSampler();
        configureTestElement(element);  // Essential because it sets some basic JMeter properties (e.g. the link between sampler and gui class)
        return element;
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof SendIlpOverBtpSampler) {
            SendIlpOverBtpSampler sampler = (SendIlpOverBtpSampler) element;
            settingsPanel.btpRequestIdField.setText(sampler.getBtpRequestId());
            settingsPanel.btpResponseTimeoutField.setText(sampler.getReadTimeout());
            settingsPanel.getIlpPacketForm().setAddress(sampler.getAddress());
            settingsPanel.getIlpPacketForm().setAmount(sampler.getAmount());
            settingsPanel.getIlpPacketForm().setExpiry(sampler.getExpiry());
            settingsPanel.getIlpPacketForm().setCondition(sampler.getCondition());
            settingsPanel.getIlpPacketForm().setFulfillment(sampler.getFulfillment());
            settingsPanel.getIlpPacketForm().setConditionFromFulfillment(sampler.getDeriveConditionFromFulfillment());
            settingsPanel.getIlpPacketForm().setData(sampler.getData());
        }
    }

    @Override
    public void modifyTestElement(TestElement element) {
        configureTestElement(element);
        if (element instanceof SendIlpOverBtpSampler) {
            SendIlpOverBtpSampler sampler = (SendIlpOverBtpSampler) element;
            sampler.setBtpRequestId(settingsPanel.btpRequestIdField.getText());
            sampler.setReadTimeout(settingsPanel.btpResponseTimeoutField.getText());
            sampler.setAddress(settingsPanel.getIlpPacketForm().getAddress());
            sampler.setAmount(settingsPanel.getIlpPacketForm().getAmount());
            sampler.setExpiry(settingsPanel.getIlpPacketForm().getExpiry());
            sampler.setCondition(settingsPanel.getIlpPacketForm().getCondition());
            sampler.setFulfillment(settingsPanel.getIlpPacketForm().getFulfillment());
            sampler.setDeriveConditionFromFulfillment(settingsPanel.getIlpPacketForm().getConditionFromFulfillment());
            sampler.setData(settingsPanel.getIlpPacketForm().getData());

        }
    }

}