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
package org.maodian.flyingcat.xmpp.codec;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flyingcat.xmpp.entity.InfoQuery;
import org.maodian.flyingcat.xmpp.entity.InfoQuery.Builder;
import org.maodian.flyingcat.xmpp.state.StanzaErrorCondition;
import org.maodian.flyingcat.xmpp.state.XmppException;
import org.springframework.stereotype.Component;

/**
 * @author Cole Wen
 *
 */
@Component
public class InfoQueryCodec extends AbstractCodec {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Decoder#decode(java.lang.String)
   */
  @Override
  public Object decode(XMLStreamReader xmlsr) {
    try {
      String id = xmlsr.getAttributeValue("", "id");
      String type = xmlsr.getAttributeValue("", "type");
      Builder builder = new Builder(id, type);
      
      String from = xmlsr.getAttributeValue("", "from");
      String to = xmlsr.getAttributeValue("", "to");
      String language = xmlsr.getAttributeValue(XMLConstants.XML_NS_URI, "lang");
      builder.from(from).to(to).language(language);
      
      switch (type) {
      case InfoQuery.GET:
      case InfoQuery.SET:
        if (xmlsr.nextTag() != XMLStreamConstants.START_ELEMENT) {
          throw new XmppException(StanzaErrorCondition.BAD_REQUEST);
        }
        QName key = xmlsr.getName();
        builder.payload(findDecoder(key, builder.build()).decode(xmlsr));
        break;
      case InfoQuery.RESULT:
      case InfoQuery.ERROR:
        throw new IllegalStateException("Since this is a server, it should not dealing with incoming result and error");
      default:
        break;
      }
      return builder.build();
    } catch (XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Encoder#encode(java.lang.Object)
   */
  @Override
  public void encode(Object object, XMLStreamWriter xmlsw) throws XMLStreamException {
    InfoQuery iq = (InfoQuery) object;
    xmlsw.writeStartElement("iq");
    writeRequiredAttribute(xmlsw, "id", iq.getId());
    writeRequiredAttribute(xmlsw, "type", iq.getType());
    
    writeAttributeIfNotBlank(xmlsw, "from", iq.getFrom());
    writeAttributeIfNotBlank(xmlsw, "to", iq.getTo());
    writeAttributeIfNotBlank(xmlsw, XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI, "lang", iq.getLanguage());
    
    Object payload = iq.getPayload();
    if (payload != null) {
      Encoder encoder = findEncoder(payload.getClass());
      encoder.encode(payload, xmlsw);
    }
    
    xmlsw.writeEndDocument();
  }

}
