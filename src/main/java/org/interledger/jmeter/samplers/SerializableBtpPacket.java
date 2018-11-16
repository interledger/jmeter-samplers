package org.interledger.jmeter.samplers;

import org.interledger.btp.*;
import org.interledger.btp.asn.framework.BtpCodecContextFactory;
import org.interledger.encoding.asn.framework.CodecContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

public class SerializableBtpPacket implements BtpPacket {

    private static final Logger log = LoggerFactory.getLogger(SerializableBtpPacket.class);
    private static final CodecContext btpCodecs = BtpCodecContextFactory.oer();

    private final byte[] bytes;
    private final BtpPacket packet;

    public static SerializableBtpPacket newBtpMessage(long requestId, BtpSubProtocols subProtocols) {
        BtpMessage message = BtpMessage.builder()
                .requestId(requestId)
                .subProtocols(subProtocols)
                .build();
        return new SerializableBtpPacket(message, null);
    }

    public static SerializableBtpPacket newBtpResponse(long requestId, BtpSubProtocols subProtocols) {
        BtpResponse message = BtpResponse.builder()
                .requestId(requestId)
                .subProtocols(subProtocols)
                .build();
        return new SerializableBtpPacket(message, null);
    }

    public static SerializableBtpPacket from(byte[] data) throws IOException {
        try {
            BtpPacket message = btpCodecs.read(BtpPacket.class, new ByteArrayInputStream(data));
            return new SerializableBtpPacket(message, data);
        } catch (Throwable t) {
            log.error("Error deserializing BTP packet.", t);
            throw t;
        }
    }

    private SerializableBtpPacket(BtpPacket packet, byte[] data) {
        this.packet = packet;
        if(data == null) {
            ByteArrayOutputStream btpMessageBytes = new ByteArrayOutputStream();
            try {
                btpCodecs.write(packet, btpMessageBytes);
            } catch (IOException e) {
                // Impossible
                throw new RuntimeException(e);
            }
            bytes = btpMessageBytes.toByteArray();
        } else {
            bytes = data;
        }
    }

    @Override
    public BtpMessageType getType() {
        return packet.getType();
    }

    @Override
    public long getRequestId() {
        return packet.getRequestId();
    }

    @Override
    public BtpSubProtocols getSubProtocols() {
        return packet.getSubProtocols();
    }

    @Override
    public BtpSubProtocol getPrimarySubProtocol() {
        return packet.getPrimarySubProtocol();
    }

    @Override
    public Optional<BtpSubProtocol> getSubProtocol(String protocolName) {
        return packet.getSubProtocol(protocolName);
    }

    @Override
    public boolean hasSubProtocol(String protocolName) {
        return packet.hasSubProtocol(protocolName);
    }

    public String toString() {
        return packet.toString();
    }

    public byte[] toByteArray() {
        return bytes;
    }
}