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

import java.io.StringWriter;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flyingcat.holder.XMLOutputFactoryHolder;
import org.maodian.flyingcat.im.IMSession;
import org.maodian.flyingcat.im.entity.SimpleUser;
import org.maodian.flyingcat.im.entity.SimpleUser.SubState;
import org.maodian.flyingcat.xmpp.codec.Encoder;
import org.maodian.flyingcat.xmpp.entity.JabberID;
import org.maodian.flyingcat.xmpp.entity.Presence;
import org.maodian.flyingcat.xmpp.entity.Presence.PresenceType;

/**
 * @author Cole Wen
 *
 */
public class SubscriptionRequestPipeline implements Pipeline<XmppContext> {

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.Pipeline#process(java.lang.Object)
   */
  @Override
  public void process(XmppContext ctx) throws XMLStreamException {
    IMSession session = ctx.getIMSession();
    Collection<SimpleUser> srList = session.getAccountRepository().getUnreadSubscription(ctx.getJabberID().getUid());
    for (SimpleUser sr : srList) {
      JabberID from = JabberID.createInstance(sr.getUsername(), "localhost", null);
      JabberID to = ctx.getJabberID();
      Presence p = new Presence();
      p.setFrom(from);
      p.setTo(to);
      if (sr.getSubState() == SubState.NONE || sr.getSubState() == SubState.TO) {
        p.setType(PresenceType.SUBSCRIBE);
      } else {
        p.setType(PresenceType.UNSUBSCRIBE);
      }
      StringWriter writer = new StringWriter();
      XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
      Encoder encoder = ctx.getApplicationContext().getEncoder(Presence.class);
      encoder.encode(p, xmlsw);
      ctx.flush(writer.toString());
    }
  }

}
