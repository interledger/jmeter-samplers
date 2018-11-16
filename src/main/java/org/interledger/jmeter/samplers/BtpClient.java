package org.interledger.jmeter.samplers;

import org.apache.jmeter.samplers.SampleResult;

public interface BtpClient {

    void listen();

    void stopListening();

    boolean isListening();

    SampleResult send(SerializableBtpPacket message);

    SampleResult receive(long requestId, int readTimeout);

    boolean hasPendingResponses();

    int getPendingResponses();

    int getResults();

    boolean hasResult(long requestId);

    boolean hasQueuedResults();

    int getQueuedResults();
}
