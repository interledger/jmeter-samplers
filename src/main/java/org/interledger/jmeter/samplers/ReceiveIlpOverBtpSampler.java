package org.interledger.jmeter.samplers;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.slf4j.LoggerFactory;

public class ReceiveIlpOverBtpSampler extends AbstractIlpOverBtpSampler {

    private static final Slf4jLogger log = new Slf4jLogger(LoggerFactory.getLogger(ReceiveIlpOverBtpSampler.class));

    public ReceiveIlpOverBtpSampler() {
        super();
        super.setName("Receive ILP over BTP Sampler");
    }

    @Override
    protected SampleResult doSample(BtpClient client) {

        long requestId = Long.parseLong(getBtpRequestId());

        //DEBUG
        if(requestId == 2) {
            log.info("Starting to receive messages for " + JMeterContextService.getContext().getThread().getThreadName()
                    + ". There are " + client.getResults() + " results to collect.");
        }

        if(!client.hasResult(requestId)) {
            log.error("Attempting to get response for request that was never sent.");
        }

        SampleResult sendResult = client.receive(requestId, readTimeout);
        checkBtpClientState();

        return sendResult;
    }

    @Override
    protected org.apache.log.Logger getLogger() {
        return log;
    }

    public String toString() {
        return "ILP+BTP Resp sampler '" + getName() + "'";
    }

}