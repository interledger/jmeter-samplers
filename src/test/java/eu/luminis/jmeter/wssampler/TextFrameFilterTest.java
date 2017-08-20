/*
 * Copyright 2016, 2017 Peter Doornbosch
 *
 * This file is part of JMeter-WebSocket-Samplers, a JMeter add-on for load-testing WebSocket applications.
 *
 * JMeter-WebSocket-Samplers is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * JMeter-WebSocket-Samplers is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.luminis.jmeter.wssampler;

import eu.luminis.websocket.BinaryFrame;
import eu.luminis.websocket.EndOfStreamException;
import eu.luminis.websocket.TextFrame;
import eu.luminis.websocket.WebSocketClient;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.*;

public class TextFrameFilterTest {

    MockWebSocketClientCreator mocker = new MockWebSocketClientCreator();

    private SampleResult result;

    @Before
    public void setUp() {
        result = new SampleResult();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void plainTextFilterShouldDiscardAnyTextFrame() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void plainTextFilterShouldRetainBinaryFrame() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new BinaryFrame(new byte[0]));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isBinary());
    }

    @Test
    public void filterEqualsShouldKeepUnequalFrame() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.Equals);
        textFrameFilter.setMatchValue("bye");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterEqualsShouldDiscardEqualFrame() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.Equals);
        textFrameFilter.setMatchValue("hello world");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterNotEqualsShouldKeepEqualFrame() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotEquals);
        textFrameFilter.setMatchValue("hello world");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterNotEqualsShouldDiscardUnequalFrame() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotEquals);
        textFrameFilter.setMatchValue("goodbye everybody");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterContainsShouldKeepFrameThatDoesNotContainMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.Contains);
        textFrameFilter.setMatchValue("bye");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterContainsShouldDiscardFrameThatContainMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello and goodbye, cruel world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.Contains);
        textFrameFilter.setMatchValue("bye");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterNotContainsShouldKeepFrameThatContainsMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotContains);
        textFrameFilter.setMatchValue("lo wo");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterNotContainsShouldDiscardFrameThatDoesNotContainMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello and goodbye, cruel world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotContains);
        textFrameFilter.setMatchValue("WTF?");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterStartsWithShouldKeepFrameThatDoesNotStartWithMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.StartsWith);
        textFrameFilter.setMatchValue("world");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterStartsWithShouldDiscardFrameThatStartsWithMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.StartsWith);
        textFrameFilter.setMatchValue("hello");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterNotStartsWithShouldKeepFrameThatStartsWithMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotStartsWith);
        textFrameFilter.setMatchValue("hel");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterNotStartsWithShouldDiscardFrameThatDoesNotStartWithMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotStartsWith);
        textFrameFilter.setMatchValue("world");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterEndsWithShouldKeepFrameThatDoesNotEndWithMatchValue()  throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.EndsWith);
        textFrameFilter.setMatchValue("world!");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterEndsWithShouldDiscardFrameThatEndWithMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.EndsWith);
        textFrameFilter.setMatchValue("world");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterNotEndsWithShouldKeepFrameThatEndsWithMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotEndsWith);
        textFrameFilter.setMatchValue("world");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterNotEndsWithShouldDiscardFrameThatDoesNotEndWithMatchValue() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotEndsWith);
        textFrameFilter.setMatchValue("world??");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterContainsRegexShouldKeepFrameThatDoesNotContainRegexMatch() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world!!!"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.ContainsRegex);
        textFrameFilter.setMatchValue("[lo]+ \\w+$");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterContainsRegexShouldDiscardFrameThatContainsRegexMatch() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.ContainsRegex);
        textFrameFilter.setMatchValue("[lo]+");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterNotContainsRegexShouldKeepFrameThatContainsRegexMatch() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world!!!"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotContainsRegex);
        textFrameFilter.setMatchValue("[lo]+ \\w+");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterNotContainsRegexShouldDiscardFrameThatDoesNotContainRegexMatch() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotContainsRegex);
        textFrameFilter.setMatchValue("NOT[lo]+");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterEqualsRegexShouldKeepFrameThatDoesNotMatchRegex() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world!!!"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.EqualsRegex);
        textFrameFilter.setMatchValue("he[lo]+ \\w+");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterEqualsRegexShouldDiscardFrameThatMatchesRegex() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.EqualsRegex);
        textFrameFilter.setMatchValue("he[lo]+ \\w+");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

    @Test
    public void filterNotEqualsRegexShouldKeepFrameThatMatchesRegex() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotEqualsRegex);
        textFrameFilter.setMatchValue("^he[lo]+ \\w+$");
        assertTrue(textFrameFilter.receiveFrame(wsClient, 1000, result).isText());
    }

    @Test
    public void filterNotEqualsRegexShouldDiscardFrameThatDoesNotMatchRegex() throws IOException {
        WebSocketClient wsClient = mocker.createSingleFrameClient(new TextFrame("Hello world"));

        TextFrameFilter textFrameFilter = new TextFrameFilter();
        textFrameFilter.setComparisonType(ComparisonType.NotEqualsRegex);
        textFrameFilter.setMatchValue("he[lo]+ \\w+");
        exception.expect(EndOfStreamException.class);
        textFrameFilter.receiveFrame(wsClient, 1000, result);
    }

}