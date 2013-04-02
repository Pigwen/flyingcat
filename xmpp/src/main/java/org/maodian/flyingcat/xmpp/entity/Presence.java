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
package org.maodian.flyingcat.xmpp.entity;

import javax.xml.stream.XMLStreamException;

import org.maodian.flyingcat.xmpp.state.ElementVisitor;
import org.maodian.flyingcat.xmpp.state.PersistedVisitor;
import org.maodian.flyingcat.xmpp.state.State;
import org.maodian.flyingcat.xmpp.state.XmppContext;

/**
 * @author Cole Wen
 * 
 */
public class Presence implements ElementVisitee, PersistedVisitee {
  private JabberID to;
  private PresenceType type;
  private String id;
  private JabberID from;

  public Presence() {

  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public JabberID getFrom() {
    return from;
  }

  public void setFrom(JabberID from) {
    this.from = from;
  }

  public JabberID getTo() {
    return to;
  }

  public PresenceType getType() {
    return type;
  }

  public void setTo(JabberID to) {
    this.to = to;
  }

  public void setType(PresenceType type) {
    this.type = type;
  }

  public enum PresenceType {
    SUBSCRIBE, UNSUBSCRIBE, SUBSCRIBED, UNSUBSCRIBED;

    public static PresenceType fromString(String str) {
      String type = str.toUpperCase();
      switch (type) {
      case "SUBSCRIBE":
        return SUBSCRIBE;
      case "UNSUBSCRIBE":
        return UNSUBSCRIBE;
      case "SUBSCRIBED":
        return SUBSCRIBED;
      case "UNSUBSCRIBED":
        return UNSUBSCRIBED;
      default:
        // TODO: STANZAS error
        throw new RuntimeException("JID not wellformed");
      }
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.entity.Visitee#accept(org.maodian.flyingcat.xmpp.state.XmppContext, org.maodian.flyingcat.xmpp.state.Visitor)
   */
  @Override
  public State acceptElementVisitor(XmppContext ctx, ElementVisitor visitor) throws XMLStreamException {
    return visitor.handlePresence(ctx, this);
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.entity.PersistedVisitee#acceptPersistedVisitor(org.maodian.flyingcat.xmpp.state.XmppContext, org.maodian.flyingcat.xmpp.state.PersistedVisitor)
   */
  @Override
  public void acceptPersistedVisitor(XmppContext ctx, PersistedVisitor visitor) {
    visitor.persistPresenceSubscription(ctx, this);
  }
}
