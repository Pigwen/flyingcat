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

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flyingcat.im.IMSession;
import org.maodian.flyingcat.im.entity.SimpleUser;
import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.maodian.flyingcat.xmpp.entity.Contact;
import org.maodian.flyingcat.xmpp.entity.InfoQuery;
import org.maodian.flyingcat.xmpp.entity.Roster;
import org.maodian.flyingcat.xmpp.state.StreamError;
import org.maodian.flyingcat.xmpp.state.XmppContext;
import org.maodian.flyingcat.xmpp.state.XmppException;
import org.springframework.stereotype.Component;

/**
 * @author Cole Wen
 *
 */
@Component
public class RosterCodec extends AbstractCodec implements InfoQueryProcessor {

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.codec.Decoder#decode(javax.xml.stream.XMLStreamReader)
   */
  @Override
  public Object decode(XMLStreamReader xmlsr) {
    try {
      xmlsr.nextTag();
      // check end element for empty tag
      // xmlsr.require(XMLStreamConstants.END_ELEMENT, XmppNamespace.ROSTER, "query");
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
    writeAttributeIfNotBlank(xmlsw, "ver", roster.getVersion());
    for (Contact c : roster) {
      xmlsw.writeEmptyElement("", "item", XmppNamespace.ROSTER);
      writeRequiredAttribute(xmlsw, "jid", c.getJabberId());
      writeAttributeIfNotBlank(xmlsw, "name", c.getName());
      writeAttributeIfNotBlank(xmlsw, "subscription", c.getSubscription());
    }
    xmlsw.writeEndElement();
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.codec.InfoQueryProcessor#processGet(org.maodian.flyingcat.xmpp.state.XmppContext, org.maodian.flyingcat.xmpp.InfoQuery)
   */
  @Override
  public Object processGet(XmppContext context, InfoQuery iq) {
    IMSession session = context.getIMSession();
    // null means get profile of current user
    List<SimpleUser> imContacts = session.getAccountRepository().findByUsername(context.getJabberID().getUid()).getContactList();
    List<Contact> xmppContacts = new ArrayList<>();
    for (SimpleUser su : imContacts) {
      Contact c = new Contact(su.getUsername(), su.getSubState().name().toLowerCase());
      c.setName(su.getNickname());
      xmppContacts.add(c);
    }
    
    
    return new Roster(xmppContacts.hashCode() + "", xmppContacts.toArray(new Contact[0]));
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.codec.InfoQueryProcessor#processSet(org.maodian.flyingcat.xmpp.state.XmppContext, org.maodian.flyingcat.xmpp.InfoQuery)
   */
  @Override
  public Object processSet(XmppContext context, InfoQuery iq) {
    /*Roster roster = (Roster) iq.getPayload();
    if (roster.size() != 1) {
      throw new XmppException("Can only modify one contact each time", new StanzaError(iq, StanzaErrorCondition.BAD_REQUEST, StanzaError.Type.MODIFY));
    }
    Contact c = roster.iterator().next();
    SimpleUser su = new SimpleUser(c.getJabberId(), c.getName());
    IMSession session = context.getIMSession();
    if (StringUtils.equals(c.getSubscription(), Contact.SUB_REMOVE)) {
      session.removeContact(new Account(c.getName()));
    } else {
      session.action(Verb.FOLLOW, Type.PERSON, su);
    }*/
    
    // TODO: implement roster push
    return null;
  }

}
