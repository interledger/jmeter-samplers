package org.interledger.jmeter.functions;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Digest Encode Function that provides computing of different SHA-XXX, can
 * store result in a variable.
 * Algorithm names can be specified using MessageDigest Algorithms names at
 * <a href=
 * "https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html"
 * >StandardNames</a>
 * 
 * @since 4.0
 */
public class DigestEncodeBinaryFunction extends AbstractFunction {

    private static final Logger log = LoggerFactory.getLogger(DigestEncodeBinaryFunction.class);
    private static final String UTF_8 = "UTF-8";

    /**
     * The algorithm names in this section can be specified when generating an
     * instance of MessageDigest: MD5 SHA-1 SHA-256 SHA-384 SHA-512
     */
    private static final List<String> desc = new LinkedList<>();
    private static final String KEY = "__digestHex";

    // Number of parameters expected - used to reject invalid calls
    private static final int MIN_PARAMETER_COUNT = 2;
    private static final int MAX_PARAMETER_COUNT = 3;

    static {
        desc.add(JMeterUtils.getResString("algorithm_string"));
        desc.add("Binary data to be hashed (as hex string)");
        desc.add(JMeterUtils.getResString("function_name_paropt"));
    }

    private CompoundVariable[] values;

    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) throws InvalidVariableException {
        String digestAlgorithm = values[0].execute();
        String stringToEncode = values[1].execute();
        String encodedString = null;
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
            md.update(Hex.decodeHex(stringToEncode));
            byte[] bytes = md.digest();
            encodedString = new String(Hex.encodeHex(bytes));
            addVariableValue(encodedString, values, 2);
        } catch (NoSuchAlgorithmException | DecoderException e) {
            log.error("Error calling {} function with value {}, digest algorithm {}, ", KEY, stringToEncode,
                    digestAlgorithm, e);
        }
        return encodedString;
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, MIN_PARAMETER_COUNT, MAX_PARAMETER_COUNT);
        values = parameters.toArray(new CompoundVariable[parameters.size()]);
    }

    @Override
    public String getReferenceKey() {
        return KEY;
    }

    @Override
    public List<String> getArgumentDesc() {
        return desc;
    }
}
