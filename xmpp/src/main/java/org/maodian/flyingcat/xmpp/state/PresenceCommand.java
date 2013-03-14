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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flyingcat.xmpp.XmppNamespace;

/**
 * @author Cole Wen
 *
 */
public class PresenceCommand extends ContextAwareCommand {

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.Command#execute(javax.xml.stream.XMLStreamReader, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  public State execute(XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException {
    QName qName = new QName(XmppNamespace.CLIENT_CONTENT, "presence");
    if (!xmlsr.getName().equals(qName)) {
      throw new XmppException(StreamError.INVALID_NAMESPACE).set("QName", xmlsr.getName());
    }
    XmppContext ctx = getXmppContext();
    xmlsw.writeEmptyElement("", "presence", XmppNamespace.CLIENT_CONTENT);
    xmlsw.writeAttribute("from", ctx.getBareJID() + "/" + ctx.getResource());
    xmlsw.writeAttribute("to", ctx.getBareJID());
    // hack to solve the output xml without '/>'
    xmlsw.writeEndDocument();
    return new SelectState();
  }

}
