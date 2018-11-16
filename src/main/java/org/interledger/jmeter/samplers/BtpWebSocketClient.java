package org.interledger.jmeter.samplers;

import eu.luminis.websocket.BinaryFrame;
import eu.luminis.websocket.Frame;
import eu.luminis.websocket.WebSocketClient;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BtpWebSocketClient implements BtpClient {

    private static final Logger log = LoggerFactory.getLogger(BtpWebSocketClient.class);

    private final Object listenMutex = new Object();
    private final WebSocketClient client;
    private final Map<Long, SampleResult> requests;
    private final Map<Long, BlockingQueue<SampleResult>> results;

    private Thread listener;
    private final int readTimeout;
    private boolean listening = false;

    public BtpWebSocketClient(WebSocketClient client, int readTimeout) {
        this.client = client;
        this.readTimeout = readTimeout;

        this.requests = new ImmutableMap<>();
        this.results = new ImmutableMap<>();
    }

    @Override
    public void listen() {

        synchronized (listenMutex) {
            this.listening = true;

            log.info("Starting response listener for " + Thread.currentThread().getName());
            this.listener = new Thread(() -> {
                while (listening) {
                    if (client.isConnected() && hasPendingResponses()) {
                        readNext();
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            listening = false;
                        }
                    }
                }
            }, JMeterContextService.getContext().getThread().getThreadName() + " Response Listener");
            listener.start();
        }
    }


    @Override
    public void stopListening() {
        synchronized (listenMutex) {
            if(listening) {
                log.info("Stopping response listener for " + Thread.currentThread().getName());
                if(hasPendingResponses()) {
                    log.warn("Stopping listener with " + getPendingResponses() + " pending responses on " + listener.getName());
                }
                listening = false;
            }
            if(listener != null) {
                listener.interrupt();
            }
        }
    }

    @Override
    public boolean isListening() {
        return listening;
    }

    @Override
    public SampleResult send(SerializableBtpPacket message) {

        SampleResult result = new SampleResult();
        result.setSampleLabel("BTP Send (" + message.getPrimarySubProtocol().getProtocolName() + ")");

        // Create blocking queue to put result on when response is received
        BlockingQueue<SampleResult> resultQueue = results.get(message.getRequestId());
        if(resultQueue == null) {
            resultQueue = new LinkedBlockingQueue<>();
            results.put(message.getRequestId(), resultQueue);
            log.debug("There are " + results.size() + " result queues.");
        }

        try {

            result.setSamplerData(result.getSamplerData() + message.toString() + "\n\n");
            result.setDataType(SampleResult.BINARY);

            // Serialize before starting the timer
            byte[] data = message.toByteArray();
            log.trace("Sending BTP message with id: " + message.getRequestId());

            result.sampleStart();
            requests.put(message.getRequestId(), (SampleResult) result.clone());

            Frame sentFrame = client.sendBinaryFrame(data);
            result.setSentBytes(sentFrame.getSize());

            result.setSuccessful(true);
            result.setResponseCode("Sent");
            result.setResponseMessage("Sent BTP request " + message.getRequestId() +
                    " on " + JMeterContextService.getContext().getThread().getThreadName());

        } catch (Exception e) {

            log.error("Error sending BTP message with id: " + message.getRequestId());
            if(result.getStartTime() == 0)
                result.sampleStart();
            result.sampleEnd();
            result.setSampleLabel("Async Receive ILP Prepare over BTP");
            result.setSentBytes(0);
            result.setResponseCode("Send Error");
            result.setResponseMessage("Unable to send request. " + e.getMessage());
            result.setSuccessful(false);

            //Put the result on the result queue as we'll never get a response
            resultQueue.add((SampleResult) result.clone());
        }

        return result;
    }

    @Override
    public SampleResult receive(long requestId, int readTimeout) {


        SampleResult receiveResult = new SampleResult();
        receiveResult.setSampleLabel("BTP Receive");
        receiveResult.sampleStart();

        BlockingQueue<SampleResult> resultQueue = results.get(requestId);
        if(resultQueue == null) {
            log.error("Error loading result queue for request id " + requestId);
            receiveResult.sampleEnd();
            receiveResult.setResponseCode("Unmatched Response");
            receiveResult.setResponseMessage("Attempted to get a response with id " + requestId + " but there was no result queue.");
            return receiveResult;
        }

        try {
            //Block until we get the result from the receive loop or timeout
            SampleResult responseResult = resultQueue.poll(readTimeout, TimeUnit.MILLISECONDS);

            if(responseResult == null) {
                receiveResult.sampleEnd();
                receiveResult.setResponseCode("Receive Timed Out");
                receiveResult.setResponseMessage("Timed out waiting for response with id " + requestId);
                return receiveResult;
            }

            return  responseResult;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            receiveResult.sampleEnd();
            receiveResult.setResponseCode("Receive Interrupted");
            receiveResult.setResponseMessage("Interrupted waiting for response from queue. Request ID = " + requestId);
            return receiveResult;
        }
    }

    public int getPendingResponses() {
        return requests.size();
    }

    public int getResults() {
        return results.size();
    }

    public boolean hasResult(long requestId) {
        return results.containsKey(requestId);
    }

    @Override
    public boolean hasPendingResponses() {
        return getPendingResponses() > 0;
    }

    public boolean hasQueuedResults() {
        return getQueuedResults() > 0;
    }

    public int getQueuedResults() {
        return results.values().stream().reduce(0, (count, queue) -> count + queue.size(), Integer::sum);
    }

    private void readNext() {
        Frame responseFrame = null;
        try {
            //The following call blocks until a frame is received or the socket times out
            responseFrame = client.receiveFrame(readTimeout);
            log.trace("Got incoming " + responseFrame.getTypeAsString() + " frame of " +
                    responseFrame.getSize() + " bytes on BTP WebSocket Client listener thread.");
        } catch (SocketTimeoutException e) {
            // Not an error condition, we'll simply loop again while (listening == true)
            log.debug("Timeout attempting to read from WebSocket.", e);
            return;
        } catch (IOException e) {
            log.error("I/O Exception reading frame from WebSocket.", e);
            // TODO Permanent error? Should we end the test
            return;
        }

        if(responseFrame == null) {
            log.error("No frame read from WebSocket.");
            return;
        }

        if(!responseFrame.isBinary()) {
            log.error("Unexpected frame. Expecting binary frame and got " + responseFrame.getTypeAsString());
            return;
        }

        byte[] responseData = ((BinaryFrame) responseFrame).getBinaryData();

        SerializableBtpPacket responseMessage = null;
        SampleResult sendResult = null;
        BlockingQueue<SampleResult> resultQueue = null;
        try {
            responseMessage = SerializableBtpPacket.from(responseData);
            sendResult = requests.remove(responseMessage.getRequestId());
            resultQueue = results.get(responseMessage.getRequestId());

            log.trace("Got BTP response with id " + responseMessage.getRequestId());
        } catch (Exception e) {
            log.error("Couldn't deserialize response as BTP message", e);
        }

        if(sendResult == null) {
            log.error("Unable to locate the original send result for request id " + responseMessage.getRequestId());
            return;
        }

        if(resultQueue == null) {
            log.error("Unable to locate the result queue for request id " + responseMessage.getRequestId());
            return;
        }

        sendResult.sampleEnd();
        sendResult.setSampleLabel("BTP Receive");
        sendResult.setHeadersSize(sendResult.getHeadersSize() + responseFrame.getSize() - responseFrame.getPayloadSize());
        sendResult.setBodySize(sendResult.getBodySizeAsLong() + responseFrame.getPayloadSize());
        sendResult.setSamplerData(sendResult.getSamplerData() + responseMessage.toString() + "\n\n");
        sendResult.setResponseData(responseData);
        sendResult.setResponseCode("Received");
        sendResult.setResponseMessage("Received BTP request " + responseMessage.getRequestId() + " on " + Thread.currentThread().getName());
        sendResult.setSuccessful(true);
        resultQueue.add(sendResult);

        log.trace("Updated result for BTP request " + responseMessage.getRequestId() + ". There are now " + requests.size() + " responses pending.");
    }


    class ImmutableMap<K,V> extends ConcurrentHashMap<K,V> {
        @Override
        public void clear() {
            throw new RuntimeException("Something tried to clear the map!");
        }
    }
}
