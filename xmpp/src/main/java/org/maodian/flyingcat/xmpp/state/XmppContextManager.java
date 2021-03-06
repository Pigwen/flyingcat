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

import java.util.Collection;

import org.maodian.flyingcat.xmpp.entity.JabberID;

/**
 * @author Cole Wen
 *
 */
public interface XmppContextManager {

  XmppContext getXmppContext(JabberID jid);
  Collection<XmppContext> getXmppContexts(String uid);
  /**
   * @param jabberID
   * @param target
   * @param payload
   */
  void transfer(JabberID from, JabberID to, Object payload); 
  
  void transfer(JabberID from, Collection<JabberID> to, Object payload);
}
