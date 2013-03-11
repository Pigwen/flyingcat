/*
 * Copyright 2013 - 2013 Cole Wen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.maodian.flyingcat.xmpp;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.maodian.flyingcat.holder.XMLInputFactoryHolder;
import org.maodian.flyingcat.xmpp.SASLError;
import org.maodian.flyingcat.xmpp.SASLState;
import org.maodian.flyingcat.xmpp.StreamError;
import org.maodian.flyingcat.xmpp.XmppNamespace;

/**
 * @author Cole Wen
 * 
 */
public class SASLStateTest extends StateTest {

  /*
   * (non-Javadoc)
   * 
   * @see org.maodian.flycat.xmpp.StateTest#doSetup()
   */
  @Override
  public void doSetup() {
    state = new SASLState();
  }

  @Test
  public void testPlainMechanismSuccess() throws XMLStreamException {
    String inXML = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>AGp1bGlldAByMG0zMG15cjBtMzA=</auth>";
    String outXML = state.handle(context, inXML);

    Reader reader = new StringReader(outXML);
    XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);

    xmlsr.next();
    QName qname = new QName(XmppNamespace.SASL, "success");
    assertEquals(qname, xmlsr.getName());
  }
  
  @Test
  public void testInvalidNamespaceOfPlainAuth() {
    String inXML = "<auth xmlns='invalid name space' mechanism='PLAIN'>AGp1bGlldAByMG0zMG15cjBtMzA=</auth>";
    expectXmppException(state, inXML, StreamError.INVALID_NAMESPACE);
  }
  
  @Test
  public void testInvalidMechanism() {
    String inXML = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='invalid'>AGp1bGlldAByMG0zMG15cjBtMzA=</auth>";
    expectXmppException(state, inXML, SASLError.INVALID_MECHANISM);
  }
  
  @Test
  public void testIncorrectEncoding() {
    String inXML = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>***</auth>";
    expectXmppException(state, inXML, SASLError.INCORRECT_ENCODING);
  }
  
  @Test
  public void testCredentialIsLongerThan255() {
    String credential = new StringBuilder(RandomStringUtils.randomAlphabetic(256)).append('\u0000')
        .append(RandomStringUtils.randomAlphabetic(256)).append('\u0000')
        .append(RandomStringUtils.randomAlphabetic(256)).toString();
    String inXML = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>"
        + Base64.encodeBase64String(credential.getBytes(StandardCharsets.UTF_8)) + "</auth>";
    expectXmppException(state, inXML, SASLError.MALFORMED_REQUEST);
  }
  
  @Test
  public void testCredentialHasMoreThanTwoUnicodeNullSeperator() {
    String credential = new StringBuilder(RandomStringUtils.randomAlphabetic(256)).append('\u0000')
        .append(RandomStringUtils.randomAlphabetic(256)).append('\u0000')
        .append(RandomStringUtils.randomAlphabetic(256)).append('\u0000').toString();
    String inXML = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>"
        + Base64.encodeBase64String(credential.getBytes(StandardCharsets.UTF_8)) + "</auth>";
    expectXmppException(state, inXML, SASLError.MALFORMED_REQUEST);
  }
  
  @Test
  public void testInvalidXML() {
    testInvalidXML(state);
  }
}
