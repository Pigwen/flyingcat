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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.maodian.flyingcat.xmpp.entity.JabberID;
import org.maodian.flyingcat.xmpp.entity.Presence;
import org.maodian.flyingcat.xmpp.entity.Presence.PresenceType;
import org.springframework.stereotype.Component;

/**
 * @author Cole Wen
 *
 */
@Component
public class PresenceCodec extends AbstractCodec {

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.codec.Decoder#decode(javax.xml.stream.XMLStreamReader)
   */
  @Override
  public Object decode(XMLStreamReader xmlsr) {
    Presence p = new Presence();
    String id = xmlsr.getAttributeValue("", "id");
    if (StringUtils.isNotBlank(id)) {
      p.setId(id);
    }
    String t = xmlsr.getAttributeValue("", "to");
    if (StringUtils.isNotBlank(t)) {
      JabberID to = JabberID.fromString(t);
      p.setTo(to);
    }
    String type = xmlsr.getAttributeValue("", "type");
    if (StringUtils.isNotBlank(type)) {
      p.setType(PresenceType.fromString(type));
    }
    return p;
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.codec.Encoder#encode(java.lang.Object, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  public void encode(Object object, XMLStreamWriter xmlsw) throws XMLStreamException {
    Presence p = (Presence) object;
    xmlsw.writeEmptyElement("presence");
    writeAttributeIfNotBlank(xmlsw, "id", p.getId());
    writeRequiredAttribute(xmlsw, "to", p.getTo().toBareJID());
    writeRequiredAttribute(xmlsw, "from", StringUtils.isBlank(p.getFrom().getResource()) ? 
        p.getFrom().toBareJID() : p.getFrom().toFullJID());
    
    // if there is a 'type' attribute, then it's a request, otherwise is a notification
    if (p.getType() != null) {
      writeRequiredAttribute(xmlsw, "type", p.getType().name().toLowerCase());
    }
    xmlsw.writeEndDocument();
  }

}
