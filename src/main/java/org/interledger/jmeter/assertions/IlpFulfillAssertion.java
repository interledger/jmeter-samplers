package org.interledger.jmeter.assertions;

import eu.luminis.jmeter.wssampler.BinaryUtils;
import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractScopedAssertion;

public class IlpFulfillAssertion extends AbstractScopedAssertion implements Assertion {

    @Override
    public AssertionResult getResult(SampleResult sampleResult) {
        byte[] responseData = sampleResult.getResponseData();
        byte[] comparisonValue = BinaryUtils.parseBinaryString(getComparisonValue());

        AssertionResult result = new AssertionResult(getName());
        return result;
    }

    public String getComparisonValue() {
        return getPropertyAsString("compareValue");
    }

    public void setComparisonValue(String comparisonValue) {
        setProperty("compareValue", comparisonValue);
    }

}