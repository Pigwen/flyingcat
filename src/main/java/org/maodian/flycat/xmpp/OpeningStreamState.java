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
package org.maodian.flycat.xmpp;

import java.math.BigDecimal;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.RandomStringUtils;


/**
 * Handle <em>Stream Negotation</em> phase.
 * 
 * @author Cole Wen
 * @see State
 * @see AbstractState
 */
public class OpeningStreamState extends AbstractState {
  private static final BigDecimal SUPPORTED_VERSION = new BigDecimal("1.0");
  private final FeatureType featureType;
  
  public OpeningStreamState(FeatureType featureType) {
    this.featureType = featureType;
  }

  @Override
  protected void postHandle(XmppContext context) {
    context.setStreamTag(cachedXML.toString());
  }
  
  @Override
  protected State nextState() {
    switch (featureType) {
    case STARTTLS:
      return States.newStartTLSState();
    case SASL:
      return States.newSASLState();
    case RESOURCE_BIND:
      return States.newResourceBindState();
    default:
      throw new XmppException("The code should not reach here", StreamError.INTERNAL_SERVER_ERROR);
    }
  }
  
  @Override
  protected void doHandle(XmppContext context, XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException {
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
    
    // only send xml declaration at the first time
    if (featureType == FeatureType.STARTTLS) {
      xmlsw.writeStartDocument();
    }
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
    switch (featureType) {
    case STARTTLS:
      xmlsw.writeStartElement("starttls");
      xmlsw.writeDefaultNamespace(XmppNamespace.TLS);
      xmlsw.writeEmptyElement("required");
      xmlsw.writeEndElement();
      break;
    case SASL:
      xmlsw.writeStartElement("mechanisms");
      xmlsw.writeDefaultNamespace(XmppNamespace.SASL);
      xmlsw.writeStartElement("mechanism");
      xmlsw.writeCharacters("PLAIN");
      xmlsw.writeEndElement();
      xmlsw.writeEndElement();
      break;
    case RESOURCE_BIND:
      xmlsw.writeStartElement("bind");
      xmlsw.writeDefaultNamespace(XmppNamespace.BIND);
      xmlsw.writeEmptyElement("required");
      xmlsw.writeEndElement();
      break;
    default:
      throw new XmppException("The code should not reach here", StreamError.INTERNAL_SERVER_ERROR);
    }
    xmlsw.writeEndElement();
  }
  
  public static enum FeatureType {
    STARTTLS,
    SASL,
    RESOURCE_BIND;
  }
}
