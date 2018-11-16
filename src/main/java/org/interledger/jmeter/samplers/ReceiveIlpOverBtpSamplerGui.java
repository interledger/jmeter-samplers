package org.interledger.jmeter.samplers;

import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import java.awt.*;


public class ReceiveIlpOverBtpSamplerGui extends AbstractSamplerGui {

    private ReceiveIlpOverBtpSamplerGuiPanel settingsPanel;

    public ReceiveIlpOverBtpSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        settingsPanel = new ReceiveIlpOverBtpSamplerGuiPanel();
        add(settingsPanel, BorderLayout.CENTER);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        settingsPanel.clearGui();
    }

    @Override
    public String getStaticLabel() {
        return "Async ILP Fulfill over BTP";
    }

    @Override
    public String getLabelResource() {
        return null;
    }

    @Override
    public TestElement createTestElement() {
        ReceiveIlpOverBtpSampler element = new ReceiveIlpOverBtpSampler();
        configureTestElement(element);  // Essential because it sets some basic JMeter properties (e.g. the link between sampler and gui class)
        return element;
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof ReceiveIlpOverBtpSampler) {
            ReceiveIlpOverBtpSampler sampler = (ReceiveIlpOverBtpSampler) element;
            settingsPanel.btpRequestIdField.setText(sampler.getBtpRequestId());
            settingsPanel.btpResponseTimeoutField.setText(sampler.getReadTimeout());
        }
    }

    @Override
    public void modifyTestElement(TestElement element) {
        configureTestElement(element);
        if (element instanceof ReceiveIlpOverBtpSampler) {
            ReceiveIlpOverBtpSampler sampler = (ReceiveIlpOverBtpSampler) element;
            sampler.setBtpRequestId(settingsPanel.btpRequestIdField.getText());
            sampler.setReadTimeout(settingsPanel.btpResponseTimeoutField.getText());
        }
    }

}