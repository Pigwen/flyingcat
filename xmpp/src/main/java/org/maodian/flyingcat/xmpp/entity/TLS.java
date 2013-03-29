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

import org.maodian.flyingcat.xmpp.state.State;
import org.maodian.flyingcat.xmpp.state.FirstLevelElementVisitor;
import org.maodian.flyingcat.xmpp.state.XmppContext;

/**
 * @author Cole Wen
 *
 */
public class TLS implements FirstLevelElementVisitee {

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.entity.Visitee#accept(org.maodian.flyingcat.xmpp.state.XmppContext, org.maodian.flyingcat.xmpp.state.Visitor)
   */
  @Override
  public State accept(XmppContext ctx, FirstLevelElementVisitor visitor) throws XMLStreamException {
    return visitor.handleTLS(ctx, this); 
  }

}
