package org.interledger.jmeter.samplers;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.interledger.btp.BtpSubProtocols;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SendIlpOverBtpSampler extends AbstractIlpOverBtpSampler {

    private static final Slf4jLogger log = new Slf4jLogger(LoggerFactory.getLogger(SendIlpOverBtpSampler.class));

    public SendIlpOverBtpSampler() {
        super();
        super.setName("Send ILP over BTP Sampler");
    }

    @Override
    protected SampleResult doSample(BtpClient btpClient) {

        SerializableIlpPreparePacket ilpPacket;

        try {
            ilpPacket = getIlpPrepare();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        BtpSubProtocols btpSubProtocols = getBtpSubprotocols(ilpPacket.toByteArray());
        SerializableBtpPacket btpMessage = SerializableBtpPacket.newBtpMessage(Long.parseLong(getBtpRequestId()), btpSubProtocols);

        checkBtpClientState();

        SampleResult sendResult = btpClient.send(btpMessage);

        // TODO add flag to set synchronous send (i.e. behave like auth sampler)
        if(sendResult.getEndTime() == 0) {
            sendResult.sampleEnd();
            sendResult.setSuccessful(true);
            sendResult.setResponseCode("Sent");
            sendResult.setResponseMessage("Sent BTP request " + btpMessage.getRequestId() +
                    " on " + JMeterContextService.getContext().getThread().getThreadName());
        }

        return sendResult;
    }

    @Override
    protected org.apache.log.Logger getLogger() {
        return log;
    }

    public String toString() {
        return "ILP+BTP Send Sampler '" + getName() + "'";
    }

}