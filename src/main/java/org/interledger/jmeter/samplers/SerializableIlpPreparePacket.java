package org.interledger.jmeter.samplers;

import org.interledger.core.*;
import org.interledger.core.asn.framework.InterledgerCodecContextFactory;
import org.interledger.encoding.asn.framework.CodecContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.MILLIS;

public class SerializableIlpPreparePacket implements InterledgerPreparePacket {

    private static final CodecContext ilpCodecs = InterledgerCodecContextFactory.oer();

    private final byte[] bytes;
    private final InterledgerPreparePacket packet;
    private final InterledgerFulfillment fulfillment;

    public static SerializableIlpPreparePacket from(InterledgerAddress address, BigInteger amount, Instant expiry,
                                                    InterledgerCondition condition, InterledgerFulfillment fulfillment,
                                                    byte[] data) {
        InterledgerPreparePacket packet = InterledgerPreparePacket.builder()
                .destination(address)
                .amount(amount)
                .executionCondition(condition)
                .expiresAt(expiry)
                .data(data)
                .build();

        return new SerializableIlpPreparePacket(packet, fulfillment);
    }

    public SerializableIlpPreparePacket(InterledgerPreparePacket packet, InterledgerFulfillment fulfillment) {

        this.packet = packet;
        this.fulfillment = fulfillment;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ilpCodecs.write(packet, baos);
        } catch (IOException e) {
            // Impossible
            throw new RuntimeException(e);
        }
        bytes = baos.toByteArray();
    }

    public InterledgerFulfillment getFulfillment() {
        return fulfillment;
    }

    public String toString() {
        return packet.toString();
    }

    public byte[] toByteArray() {
        return bytes;
    }

    @Override
    public BigInteger getAmount() {
        return packet.getAmount();
    }

    @Override
    public Instant getExpiresAt() {
        return packet.getExpiresAt();
    }

    @Override
    public InterledgerCondition getExecutionCondition() {
        return packet.getExecutionCondition();
    }

    @Override
    public InterledgerAddress getDestination() {
        return packet.getDestination();
    }

    @Override
    public byte[] getData() {
        return packet.getData();
    }
}