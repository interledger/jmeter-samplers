package org.interledger.jmeter.samplers;

import eu.luminis.jmeter.wssampler.SamplingAbortedException;
import eu.luminis.jmeter.wssampler.WebsocketSampler;
import eu.luminis.websocket.Frame;
import eu.luminis.websocket.WebSocketClient;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;

import java.io.IOException;

public abstract class AbstractBtpSampler extends WebsocketSampler {

    protected static final ThreadLocal<BtpClient> threadLocalCachedBtpConnection = new ThreadLocal<>();

    public AbstractBtpSampler() {
    }

    protected BtpClient prepareBtpClient() {
        BtpClient client = threadLocalCachedBtpConnection.get();
        if(client == null) {
            WebSocketClient ws = threadLocalCachedConnection.get();
            if(ws == null) {
                throw new RuntimeException("No WebSocket available for BTP Client.");
            }
            client = new BtpWebSocketClient(ws, readTimeout);
            threadLocalCachedBtpConnection.set(client);
            client.listen();
        }
        return client;
    }

    protected void checkBtpClientState() {
        BtpClient client = threadLocalCachedBtpConnection.get();
        if(client != null) {
            if(!client.hasPendingResponses()) {
                client.stopListening();
            } else if (!client.isListening()) {
                client.listen();
            }
        }
    }

    @Override
    protected WebSocketClient prepareWebSocketClient(SampleResult result) {
        return threadLocalCachedConnection.get();
    }

    protected SampleResult createSampleResultError(String error) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setResponseCode("Sampler error");
        result.setResponseMessage("Sampler error: " + error);
        return result;
    }

    /**
     * NO OP - Replaced by doSample(BtpWebSocketClient)
     */
    @Override
    protected final Frame doSample(WebSocketClient client, SampleResult result) {
        return null;
    }

    /**
     * Use the client to send and/or receive a message.
     *
     * <p>If this returns null then the sampler will continue without waiting for a response.</p>
     *
     * @param client The BTP client
     * @return The SampleResult after receiving a response or null if the response will be collected later
     * @throws IOException
     * @throws InterruptedException
     */
    protected abstract SampleResult doSample(BtpClient client);

    @Override
    public SampleResult sample(Entry entry) {

        String validationError = validateArguments();
        if (validationError != null) {
            return createSampleResultError(validationError);
        }

        readTimeout = Integer.parseInt(getReadTimeout());
        BtpClient btpClient = prepareBtpClient();

        if (btpClient == null) {
            getLogger().error("Unable to load BTP client");
            return createSampleResultError("Couldn't load BTP Client.");
        }

        try {
            return doSample(btpClient);
        }
        catch (Exception error) {
            getLogger().error("Unhandled error in sampler '"  + getName() + "'.", error);
            return createSampleResultError(error.getMessage());
        }

    }

    protected String validateArguments() {
        String errorMsg = validateReadTimeout(getReadTimeout());
        return errorMsg;
    }


    public String getBtpRequestId() {
        return getPropertyAsString("btpRequestId");
    }

    public void setBtpRequestId(String requestId) {
        setProperty("btpRequestId", requestId);
    }

}
