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
package org.maodian.flyingcat.xmpp.state;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.maodian.flyingcat.holder.XMLInputFactoryHolder;
import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.maodian.flyingcat.xmpp.state.StreamState.AuthenticatedStreamState;

/**
 * @author Cole Wen
 *
 */
public class SASLCommandTest extends CommandTest {

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.CommandTest#doSetup()
   */
  @Override
  public void doSetup() {
    cmd = new SASLCommand();
    cmd.setXmppContext(context);
  }

  @Test
  public void testPlainMechanismSuccess() throws Exception {
    String inXML = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>AGp1bGlldAByMG0zMG15cjBtMzA=</auth>";
    
    Writer writer = new StringWriter();
    XMLStreamReader xmlsr1 = newXMLStreamReader(new StringReader(inXML));
    XMLStreamWriter xmlsw = newXMLStreamWriter(writer);
    // skipt start document
    xmlsr1.nextTag();
    State state = cmd.execute(xmlsr1, xmlsw);
    
    Reader reader = new StringReader(writer.toString());
    XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);

    xmlsr.next();
    QName qname = new QName(XmppNamespace.SASL, "success");
    assertEquals(qname, xmlsr.getName());
    assertTrue(state instanceof AuthenticatedStreamState);
  }
  
  @Test
  public void testInvalidNamespaceOfPlainAuth() throws Exception {
    String inXML = "<auth xmlns='invalid name space' mechanism='PLAIN'>AGp1bGlldAByMG0zMG15cjBtMzA=</auth>";
    expectXmppException(cmd, inXML, StreamError.INVALID_NAMESPACE);
  }
  
  @Test
  public void testInvalidMechanism() throws Exception {
    String inXML = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='invalid'>AGp1bGlldAByMG0zMG15cjBtMzA=</auth>";
    expectXmppException(cmd, inXML, SASLError.INVALID_MECHANISM);
  }
  
  @Test
  public void testIncorrectEncoding() throws Exception {
    String inXML = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>***</auth>";
    expectXmppException(cmd, inXML, SASLError.INCORRECT_ENCODING);
  }
  
  @Test
  public void testCredentialIsLongerThan255() throws Exception {
    String credential = new StringBuilder(RandomStringUtils.randomAlphabetic(256)).append('\u0000')
        .append(RandomStringUtils.randomAlphabetic(256)).append('\u0000')
        .append(RandomStringUtils.randomAlphabetic(256)).toString();
    String inXML = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>"
        + Base64.encodeBase64String(credential.getBytes(StandardCharsets.UTF_8)) + "</auth>";
    expectXmppException(cmd, inXML, SASLError.MALFORMED_REQUEST);
  }
  
  @Test
  public void testCredentialHasMoreThanTwoUnicodeNullSeperator() throws Exception {
    String credential = new StringBuilder(RandomStringUtils.randomAlphabetic(256)).append('\u0000')
        .append(RandomStringUtils.randomAlphabetic(256)).append('\u0000')
        .append(RandomStringUtils.randomAlphabetic(256)).append('\u0000').toString();
    String inXML = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>"
        + Base64.encodeBase64String(credential.getBytes(StandardCharsets.UTF_8)) + "</auth>";
    expectXmppException(cmd, inXML, SASLError.MALFORMED_REQUEST);
  }
}
