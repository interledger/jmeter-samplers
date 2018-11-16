package org.interledger.jmeter.samplers;

import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import java.awt.*;


public class AuthOverBtpSamplerGui extends AbstractSamplerGui {

    private AuthOverBtpSamplerGuiPanel settingsPanel;

    public AuthOverBtpSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        settingsPanel = new AuthOverBtpSamplerGuiPanel();
        add(settingsPanel, BorderLayout.CENTER);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        settingsPanel.clearGui();
    }

    @Override
    public String getStaticLabel() {
        return "Auth over BTP";
    }

    @Override
    public String getLabelResource() {
        return null;
    }

    @Override
    public TestElement createTestElement() {
        AuthOverBtpSampler element = new AuthOverBtpSampler();
        configureTestElement(element);  // Essential because it sets some basic JMeter properties (e.g. the link between sampler and gui class)
        return element;
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof AuthOverBtpSampler) {
            AuthOverBtpSampler sampler = (AuthOverBtpSampler) element;
            settingsPanel.btpRequestIdField.setText(sampler.getBtpRequestId());
            settingsPanel.btpResponseTimeoutField.setText(sampler.getReadTimeout());
            settingsPanel.getBtpAuthForm().setUsername(sampler.getUsername());
            settingsPanel.getBtpAuthForm().setToken(sampler.getToken());
        }
    }

    @Override
    public void modifyTestElement(TestElement element) {
        configureTestElement(element);
        if (element instanceof AuthOverBtpSampler) {
            AuthOverBtpSampler sampler = (AuthOverBtpSampler) element;
            sampler.setBtpRequestId(settingsPanel.btpRequestIdField.getText());
            sampler.setReadTimeout(settingsPanel.btpResponseTimeoutField.getText());
            sampler.setUsername(settingsPanel.getBtpAuthForm().getUsername());
            sampler.setToken(settingsPanel.getBtpAuthForm().getToken());
        }
    }

}