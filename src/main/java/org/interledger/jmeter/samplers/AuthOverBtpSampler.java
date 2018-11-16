package org.interledger.jmeter.samplers;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.log.Logger;
import org.interledger.btp.BtpSubProtocol;
import org.interledger.btp.BtpSubProtocolContentType;
import org.interledger.btp.BtpSubProtocols;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AuthOverBtpSampler extends AbstractBtpSampler {

    private static final Slf4jLogger log = new Slf4jLogger(LoggerFactory.getLogger(AuthOverBtpSampler.class));

    public AuthOverBtpSampler() {
        super();
        super.setName("Auth over BTP Sampler");
    }

    @Override
    protected SampleResult doSample(BtpClient client) {

        long requestId = Long.parseLong(getBtpRequestId());
        BtpSubProtocols subProtocols = BtpSubProtocols.empty();
        subProtocols.add(BtpSubProtocol.builder()
                .protocolName("auth")
                .contentType(BtpSubProtocolContentType.MIME_APPLICATION_OCTET_STREAM)
                .data(new byte[]{})
                .build());
        subProtocols.add(BtpSubProtocol.builder()
                .protocolName("auth_username")
                .contentType(BtpSubProtocolContentType.MIME_TEXT_PLAIN_UTF8)
                .data(getUsername().getBytes(StandardCharsets.UTF_8))
                .build());
        subProtocols.add(BtpSubProtocol.builder()
                .protocolName("auth_token")
                .contentType(BtpSubProtocolContentType.MIME_TEXT_PLAIN_UTF8)
                .data(getToken().getBytes(StandardCharsets.UTF_8))
                .build());

        SerializableBtpPacket btpMessage = SerializableBtpPacket.newBtpMessage(requestId, subProtocols);

        SampleResult sendResult = client.send(btpMessage);
        if (!sendResult.isSuccessful()) {
            sendResult.setSampleLabel("Auth over BTP");
            return sendResult;
        }

        //Wait for response
        SampleResult receiveResult  = client.receive(requestId, readTimeout);
        receiveResult.setSampleLabel("Auth over BTP");
        return receiveResult;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    public String toString() {
        return "Auth over BTP sampler '" + getName() + "'";
    }

    public String getUsername() {
        return getPropertyAsString("username");
    }

    public void setUsername(String username) {
        setProperty("username", username);
    }

    public String getToken() {
        return getPropertyAsString("token");
    }

    public void setToken(String username) {
        setProperty("token", username);
    }
}