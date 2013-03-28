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

import javax.xml.stream.XMLStreamException;

import org.maodian.flyingcat.xmpp.entity.Auth;
import org.maodian.flyingcat.xmpp.entity.InfoQuery;
import org.maodian.flyingcat.xmpp.entity.Presence;
import org.maodian.flyingcat.xmpp.entity.TLS;

/**
 * @author Cole Wen
 *
 */
public interface Visitor {
  State handleInfoQuery(XmppContext ctx, InfoQuery iq) throws XMLStreamException;
  void handlePresence(Presence p);
  /**
   * @param ctx
   * @param visitor
   * @return
   */
  State handleTLS(XmppContext ctx, TLS tls) throws XMLStreamException;
  
  State handleSASL(XmppContext ctx, Auth auth) throws XMLStreamException;
}
