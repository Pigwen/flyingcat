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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Cole Wen
 * 
 */
public class ResourceBindState extends AbstractState {
  
  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.AbstractState#doHandle(org.maodian.flycat.xmpp.XmppContext, javax.xml.stream.XMLStreamReader, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  protected void doHandle(XmppContext context, XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException {
    // skip the stream tag
    xmlsr.nextTag();
    xmlsr.nextTag();
    QName iq = new QName(XmppNamespace.CONTENT, "iq");
    if (!iq.equals(xmlsr.getName())) {
      throw new XmppException(StreamError.INVALID_NAMESPACE).set("QName", iq);
    }
    String type = xmlsr.getAttributeValue("", "type");
    String id = xmlsr.getAttributeValue("", "id");
    if (!StringUtils.equals(type, "set") || StringUtils.isBlank("id")) {
      throw new XmppException(StreamError.BAD_FORMAT);
    }

    xmlsr.nextTag();
    xmlsr.require(XMLStreamConstants.START_ELEMENT, XmppNamespace.BIND, "bind");

    xmlsr.nextTag();
    QName qname = new QName(XmppNamespace.BIND, "resource");
    if (!qname.equals(xmlsr.getName())) {
      throw new XmppException(StreamError.INVALID_NAMESPACE).set("QName", qname);
    }
    String resource = xmlsr.getElementText();
    context.setResource(resource);

    xmlsw.writeStartElement("iq");
    xmlsw.writeAttribute("id", id);
    xmlsw.writeAttribute("type", "result");

    xmlsw.writeStartElement("bind");
    xmlsw.writeDefaultNamespace(XmppNamespace.BIND);

    xmlsw.writeStartElement("jid");
    xmlsw.writeCharacters(context.getBareJID() + "/" + resource);

    xmlsw.writeEndElement();
    xmlsw.writeEndElement();
    xmlsw.writeEndElement();
  }
  
  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.AbstractState#nextState()
   */
  @Override
  protected State nextState() {
    return this;
  }
  
  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.AbstractState#preHandle(org.maodian.flycat.xmpp.XmppContext, java.lang.String)
   */
  @Override
  protected boolean preHandle(XmppContext context, String xml) {
    if (!StringUtils.endsWith(xml, "</iq>")) {
      cachedXML.append(xml);
      return false;
    }
    cachedXML = new StringBuilder(context.wrapStreamTag(cachedXML.append(xml).toString()));
    return true;
  }
}
