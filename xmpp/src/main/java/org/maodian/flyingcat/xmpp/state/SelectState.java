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
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flyingcat.holder.XMLInputFactoryHolder;
import org.maodian.flyingcat.holder.XMLOutputFactoryHolder;
import org.maodian.flyingcat.xmpp.StreamError;
import org.maodian.flyingcat.xmpp.XmppException;

/**
 * @author Cole Wen
 *
 */
public class SelectState implements State {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.state.State#step(org.maodian.flycat.xmpp.state.XmppContext, java.lang.String)
   */
  @Override
  public Result step(XmppContext context, String xml) {
    String fragment = context.wrapStreamTag(xml);
    try (Reader reader = new StringReader(fragment);
        StringWriter writer = new StringWriter();) {
      try {
        XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
        XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
        State nextState = doHandle(context, xmlsr, xmlsw);
        Result result = new DefaultResult(nextState, writer.toString());
        return result;
      } catch (XMLStreamException e) {
        throw new XmppException(e, StreamError.BAD_FORMAT);
      }
    } catch (IOException ioe) {
      // close a StringReader/StringWriter should not cause IOException, though
      throw new XmppException(ioe, StreamError.INTERNAL_SERVER_ERROR);
    }
  }
  
  private State doHandle(XmppContext context, XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException {
    //skip stream tag
    xmlsr.nextTag();
    //to first level tag of stream
    xmlsr.nextTag();
    QName qName = xmlsr.getName();
    Command cmd = context.lookup(qName);
    return cmd.execute(xmlsr, xmlsw);
  }

}
