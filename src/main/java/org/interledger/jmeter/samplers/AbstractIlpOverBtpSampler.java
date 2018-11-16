package org.interledger.jmeter.samplers;

import eu.luminis.jmeter.wssampler.BinaryUtils;
import org.interledger.btp.BtpSubProtocol;
import org.interledger.btp.BtpSubProtocolContentType;
import org.interledger.btp.BtpSubProtocols;
import org.interledger.core.InterledgerAddress;
import org.interledger.core.InterledgerCondition;
import org.interledger.core.InterledgerFulfillment;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import static eu.luminis.jmeter.wssampler.BinaryUtils.parseBinaryString;
import static java.time.temporal.ChronoUnit.MILLIS;

public abstract class AbstractIlpOverBtpSampler extends AbstractBtpSampler {

    public AbstractIlpOverBtpSampler() {
        super();
    }

    protected BtpSubProtocols getBtpSubprotocols(byte[] ilpPacket) {

        BtpSubProtocol ilpBtp = BtpSubProtocol.builder()
                .protocolName("ilp")
                .contentType(BtpSubProtocolContentType.MIME_APPLICATION_OCTET_STREAM)
                .data(ilpPacket)
                .build();

        return BtpSubProtocols.fromPrimarySubProtocol(ilpBtp);
    }

    protected SerializableIlpPreparePacket getIlpPrepare() throws NoSuchAlgorithmException {

        byte[] fulfillmentBytes = parseBinaryString(getFulfillment());
        byte[] conditionBytes;
        if (getDeriveConditionFromFulfillment()) {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            conditionBytes = sha256.digest(fulfillmentBytes);
        } else {
            conditionBytes = parseBinaryString(getCondition());
        }
        byte[] data = BinaryUtils.parseBinaryString(getData());

        return SerializableIlpPreparePacket.from(
                InterledgerAddress.of(getAddress()),
                BigInteger.valueOf(Long.parseLong(getAmount())),
                Instant.now().plus(Integer.parseInt(getExpiry()), MILLIS),
                InterledgerCondition.of(conditionBytes),
                InterledgerFulfillment.of(fulfillmentBytes),
                data);
    }

    public String getAddress() {
        return getPropertyAsString("address");
    }

    public void setAddress(String address) {
        setProperty("address", address);
    }

    public String getAmount() {
        return getPropertyAsString("amount");
    }

    public void setAmount(String amount) {
        setProperty("amount", amount);
    }

    public String getExpiry() {
        return getPropertyAsString("expiry");
    }

    public void setExpiry(String expiry) {
        setProperty("expiry", expiry);
    }

    public String getCondition() {
        return getPropertyAsString("condition");
    }

    public void setCondition(String condition) {
        setProperty("condition", condition);
    }

    public String getFulfillment() {
        return getPropertyAsString("fulfillment");
    }

    public void setFulfillment(String condition) {
        setProperty("fulfillment", condition);
    }

    public boolean getDeriveConditionFromFulfillment() {
        return getPropertyAsBoolean("deriveCondition");
    }

    public void setDeriveConditionFromFulfillment(boolean derive) {
        setProperty("deriveCondition", derive);
    }

    public String getData() {
        return getPropertyAsString("packetData");
    }

    public void setData(String packetData) {
        setProperty("packetData", packetData);
    }

}
