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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Before;
import org.junit.Test;
import org.maodian.flyingcat.holder.XMLInputFactoryHolder;
import org.maodian.flyingcat.xmpp.OpeningStreamState;
import org.maodian.flyingcat.xmpp.StreamError;
import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.maodian.flyingcat.xmpp.OpeningStreamState.FeatureType;

/**
 * @author Cole Wen
 * 
 */
public class OpeningStreamStateTest extends StateTest {
  
  @Before
  public void doSetup() {
    
  }

  @Test
  public void testSuccessWithStartTTLsFeatureType() throws XMLStreamException {
    state = new OpeningStreamState(FeatureType.STARTTLS);
    String inXML = "<stream:stream from='cole@localhost' to='localhost' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>";
    String outXML = state.handle(context, inXML);
    
    Reader reader = new StringReader(outXML);
    XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
    assertEquals(XMLStreamConstants.START_DOCUMENT, xmlsr.getEventType());
    assertEquals(XMLStreamConstants.START_ELEMENT, xmlsr.next());
    assertStream(xmlsr, "localhost", "cole@localhost", "1.0", "en");
    
    xmlsr.next();
    assertStartTLSIsRequired(xmlsr);
  }
  
  @Test
  public void testSuccessWithSASLFeatureType() throws XMLStreamException {
    state = new OpeningStreamState(FeatureType.SASL);
    String inXML = "<stream:stream from='cole@localhost' to='localhost' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>";
    String outXML = state.handle(context, inXML);
    
    Reader reader = new StringReader(outXML);
    XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
    assertEquals(XMLStreamConstants.START_DOCUMENT, xmlsr.getEventType());
    assertEquals(XMLStreamConstants.START_ELEMENT, xmlsr.next());
    assertStream(xmlsr, "localhost", "cole@localhost", "1.0", "en");
    
    xmlsr.next();
    assertSASLMechanisms(xmlsr);
  }
  
  @Test
  public void testSuccessWithResourceBind() throws XMLStreamException {
    state = new OpeningStreamState(FeatureType.RESOURCE_BIND);
    String inXML = "<stream:stream from='cole@localhost' to='localhost' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>";
    String outXML = state.handle(context, inXML);
    
    Reader reader = new StringReader(outXML);
    XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
    assertEquals(XMLStreamConstants.START_DOCUMENT, xmlsr.getEventType());
    assertEquals(XMLStreamConstants.START_ELEMENT, xmlsr.next());
    assertStream(xmlsr, "localhost", "cole@localhost", "1.0", "en");
    
    xmlsr.next();
    assertResourceBind(xmlsr);
  }
  
  @Test
  public void testIncommingStreamWithoutFromAttribute() throws XMLStreamException {
    state = new OpeningStreamState(FeatureType.STARTTLS);
    String inXML = "<stream:stream to='localhost' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>";
    String outXML = state.handle(context, inXML);
    
    Reader reader = new StringReader(outXML);
    XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
    assertEquals(XMLStreamConstants.START_DOCUMENT, xmlsr.getEventType());
    assertEquals(XMLStreamConstants.START_ELEMENT, xmlsr.next());
    assertStream(xmlsr, "localhost", null, "1.0", "en");
  }
  
  @Test
  public void testInvalidNamespaceOfStream() {
    state = new OpeningStreamState(FeatureType.STARTTLS);
    String inXML = "<stream:stream from='cole@localhost' to='localhost' xmlns='jabber:client' xmlns:stream='invalid_namespace' version='1.0'>";
    expectXmppException(state, inXML, StreamError.INVALID_NAMESPACE);
  }
  
  @Test
  public void testUnsupportedVersion() {
    state = new OpeningStreamState(FeatureType.STARTTLS);
    String inXML = "<stream:stream from='cole@localhost' to='localhost' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.1'>";
    expectXmppException(state, inXML, StreamError.UNSUPPORTED_VERSION);
  }
  
  @Test
  public void testInvalidXML() {
    state = new OpeningStreamState(FeatureType.STARTTLS);
    testInvalidXML(state);
  }
  
  /**
   * expect the next element is a stream node and its attribute is equal to the passed 
   * parameters
   * @param xmlsr
   * @param from
   * @param to
   * @param version
   * @param lang
   */
  private void assertStream(XMLStreamReader xmlsr, String from, String to, String version, String lang) {
    assertEquals(from, xmlsr.getAttributeValue("", "from"));
    assertEquals(to, xmlsr.getAttributeValue("", "to"));
    assertEquals(version, xmlsr.getAttributeValue("", "version"));
    assertEquals(lang, xmlsr.getAttributeValue(XMLConstants.XML_NS_URI, "lang"));
    assertNotNull(xmlsr.getAttributeValue("", "id"));
    assertTrue(xmlsr.getAttributeValue("", "id").length() > 0);
  }
  
  private void assertStartTLSIsRequired(XMLStreamReader xmlsr) throws XMLStreamException {
    QName features = new QName(XmppNamespace.STREAM, "features", "stream");
    assertEquals(features, xmlsr.getName());
    
    xmlsr.next();
    QName starttls = new QName(XmppNamespace.TLS, "starttls");
    assertEquals(starttls, xmlsr.getName());
    
    xmlsr.next();
    QName required = new QName(XmppNamespace.TLS, "required");
    assertEquals(required, xmlsr.getName());
  }
  
  private void assertSASLMechanisms(XMLStreamReader xmlsr) throws XMLStreamException {
    QName features = new QName(XmppNamespace.STREAM, "features", "stream");
    assertEquals(features, xmlsr.getName());
    
    xmlsr.next();
    QName mechanisms = new QName(XmppNamespace.SASL, "mechanisms");
    assertEquals(mechanisms, xmlsr.getName());
    
    xmlsr.next();
    QName plainMech = new QName(XmppNamespace.SASL, "mechanism");
    assertEquals(plainMech, xmlsr.getName());
    assertEquals("PLAIN", xmlsr.getElementText());
  }
  
  private void assertResourceBind(XMLStreamReader xmlsr) throws XMLStreamException {
    QName features = new QName(XmppNamespace.STREAM, "features", "stream");
    assertEquals(features, xmlsr.getName());
    
    xmlsr.next();
    QName starttls = new QName(XmppNamespace.BIND, "bind");
    assertEquals(starttls, xmlsr.getName());
    
    xmlsr.next();
    QName required = new QName(XmppNamespace.BIND, "required");
    assertEquals(required, xmlsr.getName());
  }
}
