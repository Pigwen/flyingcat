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
import java.io.StringWriter;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.RandomStringUtils;
import org.maodian.flyingcat.holder.XMLOutputFactoryHolder;
import org.maodian.flyingcat.xmpp.codec.Encoder;
import org.maodian.flyingcat.xmpp.entity.Contact;
import org.maodian.flyingcat.xmpp.entity.InfoQuery;
import org.maodian.flyingcat.xmpp.entity.Presence;
import org.maodian.flyingcat.xmpp.entity.Roster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Cole Wen
 * 
 */
public class PresenceListener extends AbstractXmppContextListener {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private XmppContextManager xmppCtxMgr;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.maodian.flyingcat.xmpp.state.AbstractXmppContextListener#onPostSend
   * (org.maodian.flyingcat.xmpp.state.XmppContext, java.lang.Object)
   */
  @Override
  public void onPostSend(XmppContext ctx, Object payload) {
    if (payload instanceof Presence) {
      Presence p = (Presence) payload;
      Contact contact = new Contact(p.getTo().toBareJID());
      contact.setAsk("subscribe");
      Roster roster = new Roster();
      roster.addContact(contact);
      
      // send roster push to all available resource
      Collection<XmppContext> allCtx = xmppCtxMgr.getXmppContexts(ctx.getJabberID().getUid());
      for (XmppContext xc : allCtx) {
        String id = RandomStringUtils.randomAlphabetic(32);
        InfoQuery.Builder builder = new InfoQuery.Builder(id, "set");
        InfoQuery iq = builder.to(ctx.getJabberID().toFullJID()).payload(roster).build();
        Encoder encoder = ctx.getApplicationContext().getEncoder(InfoQuery.class);
        
        try (Writer writer = new StringWriter();) {
          XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
          encoder.encode(iq, xmlsw);
          ctx.flush(writer.toString());
        } catch (XMLStreamException | IOException e) {
          log.warn("Error when onPostSend", e);
        }
      }
      
    }
  }

  public void setXmppContextManager(XmppContextManager xmppCtxMgr) {
    this.xmppCtxMgr = xmppCtxMgr;
  }

}
