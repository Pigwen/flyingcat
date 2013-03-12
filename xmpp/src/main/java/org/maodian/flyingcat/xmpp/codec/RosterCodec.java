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

import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flyingcat.im.Session;
import org.maodian.flyingcat.im.entity.User;
import org.maodian.flyingcat.xmpp.AbstractCodec;
import org.maodian.flyingcat.xmpp.Contact;
import org.maodian.flyingcat.xmpp.InfoQuery;
import org.maodian.flyingcat.xmpp.Roster;
import org.maodian.flyingcat.xmpp.StreamError;
import org.maodian.flyingcat.xmpp.XmppException;
import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.maodian.flyingcat.xmpp.state.XmppContext;

/**
 * @author Cole Wen
 *
 */
public class RosterCodec extends AbstractCodec implements InfoQueryProcessor {

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.codec.Decoder#decode(javax.xml.stream.XMLStreamReader)
   */
  @Override
  public Object decode(XMLStreamReader xmlsr) {
    try {
      xmlsr.nextTag();
      xmlsr.require(XMLStreamConstants.START_ELEMENT, XmppNamespace.ROSTER, "query");
      return new Roster();
    } catch (XMLStreamException e) {
      throw new XmppException(e, StreamError.INVALID_XML);
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.codec.Encoder#encode(java.lang.Object, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  public void encode(Object object, XMLStreamWriter xmlsw) throws XMLStreamException {
    Roster roster = (Roster) object;
    xmlsw.writeStartElement("", "query", XmppNamespace.ROSTER);
    xmlsw.writeDefaultNamespace(XmppNamespace.ROSTER);
    xmlsw.writeAttribute("ver", roster.getVersion());
    for (Contact c : roster) {
      xmlsw.writeEmptyElement("", "item", XmppNamespace.ROSTER);
      xmlsw.writeAttribute("jid", c.getJabberId());
      xmlsw.writeAttribute("name", c.getName());
    }
    xmlsw.writeEndElement();
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.codec.InfoQueryProcessor#processGet(org.maodian.flyingcat.xmpp.state.XmppContext, org.maodian.flyingcat.xmpp.InfoQuery)
   */
  @Override
  public Object processGet(XmppContext context, InfoQuery iq) {
    Session session = context.createIMSession();
    List<User> contacts = session.getContactList();
    return new Roster(contacts.hashCode() + "", contacts.toArray(new Contact[0]));
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.codec.InfoQueryProcessor#processSet(org.maodian.flyingcat.xmpp.state.XmppContext, org.maodian.flyingcat.xmpp.InfoQuery)
   */
  @Override
  public Object processSet(XmppContext context, InfoQuery iq) {
    return null;
  }

}
