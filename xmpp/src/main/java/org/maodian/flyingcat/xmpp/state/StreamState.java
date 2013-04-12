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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.RandomStringUtils;
import org.maodian.flyingcat.holder.XMLInputFactoryHolder;
import org.maodian.flyingcat.holder.XMLOutputFactoryHolder;
import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.springframework.stereotype.Component;



/**
 * Handle <em>Stream Negotation</em> phase.
 * 
 * @author Cole Wen
 * @see State
 * @see AbstractState
 */
public abstract class StreamState implements State {
  private static final BigDecimal SUPPORTED_VERSION = new BigDecimal("1.0");

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.state.State#step(org.maodian.flycat.xmpp.state.XmppContext, java.lang.String)
   */
  @Override
  public Result step(XmppContext context, String xml) {
    try (Reader reader = new StringReader(xml);
        StringWriter writer = new StringWriter();) {
      try {
        XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
        XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
        doHandle(context, xmlsr, xmlsw);
        context.setStreamTag(xml);
        context.flush(writer.toString());
        Result result = new DefaultResult(context.getGlobalContext().getSelectState());
        return result;
      } catch (XMLStreamException e) {
        throw new XmppException(e, StreamError.BAD_FORMAT);
      }
    } catch (IOException ioe) {
      // close a StringReader/StringWriter should not cause IOException, though
      throw new XmppException(ioe, StreamError.INTERNAL_SERVER_ERROR);
    }
  }
  
  private void doHandle(XmppContext context, XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException {
    xmlsr.nextTag();
    QName qname = new QName(XmppNamespace.STREAM, "stream");
    if (!xmlsr.getName().equals(qname)) {
      throw new XmppException(StreamError.INVALID_NAMESPACE)
          .set("QName", xmlsr.getName());
    }
    
    // throw exception if client version > 1.0
    BigDecimal version = new BigDecimal(xmlsr.getAttributeValue("", "version"));
    if (version.compareTo(SUPPORTED_VERSION) > 0) {
      throw new XmppException(StreamError.UNSUPPORTED_VERSION);
    }
    
    xmlsw.writeStartDocument();
    xmlsw.writeStartElement("stream", "stream", XmppNamespace.STREAM);
    xmlsw.writeNamespace("stream", XmppNamespace.STREAM);
    xmlsw.writeDefaultNamespace(XmppNamespace.CLIENT_CONTENT);
    
    xmlsw.writeAttribute("id", RandomStringUtils.randomAlphabetic(32));
    xmlsw.writeAttribute("version", "1.0");
    xmlsw.writeAttribute("from", "localhost");
    xmlsw.writeAttribute(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI, "lang", "en");
    String from = xmlsr.getAttributeValue(null, "from");
    if (from != null) {
      xmlsw.writeAttribute("to", from);
    }
    
    // features
    xmlsw.writeStartElement(XmppNamespace.STREAM, "features");
    writeFeatures(xmlsw);
    xmlsw.writeEndElement();
  }

  /**
   * @param xmlsw
   * @throws XMLStreamException
   */
  protected abstract void writeFeatures(XMLStreamWriter xmlsw) throws XMLStreamException;
  
  @Component
  public static class OpeningStreamState extends StreamState {
    /**
     * @param xmlsw
     * @throws XMLStreamException
     */
    protected void writeFeatures(XMLStreamWriter xmlsw) throws XMLStreamException {
      xmlsw.writeStartElement("starttls");
      xmlsw.writeDefaultNamespace(XmppNamespace.TLS);
      xmlsw.writeEmptyElement("required");
      xmlsw.writeEndElement();
    }
  }
  
  @Component
  public static class TLSStreamState extends StreamState {

    /* (non-Javadoc)
     * @see org.maodian.flycat.xmpp.state.StreamState#writeFeatures(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    protected void writeFeatures(XMLStreamWriter xmlsw) throws XMLStreamException {
      xmlsw.writeStartElement("mechanisms");
      xmlsw.writeDefaultNamespace(XmppNamespace.SASL);
      xmlsw.writeStartElement("mechanism");
      xmlsw.writeCharacters("PLAIN");
      xmlsw.writeEndElement();
      xmlsw.writeEndElement();
    }
  }
  
  @Component
  public static class AuthenticatedStreamState extends StreamState {

    /* (non-Javadoc)
     * @see org.maodian.flycat.xmpp.state.StreamState#writeFeatures(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    protected void writeFeatures(XMLStreamWriter xmlsw) throws XMLStreamException {
      xmlsw.writeStartElement("bind");
      xmlsw.writeDefaultNamespace(XmppNamespace.BIND);
      xmlsw.writeEmptyElement("required");
      xmlsw.writeEndElement();
      
      // see http://xmpp.org/rfcs/rfc6121.html#intro-summary
      xmlsw.writeEmptyElement("session");
      xmlsw.writeDefaultNamespace(XmppNamespace.SESSION);      
    }
    
  }
}
