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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.maodian.flyingcat.holder.XMLInputFactoryHolder;
import org.maodian.flyingcat.xmpp.codec.Decoder;
import org.maodian.flyingcat.xmpp.entity.ElementVisitee;

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
    try (Reader reader = new StringReader(fragment);) {
      try {
        XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
        // skip stream tag
        xmlsr.nextTag();
        xmlsr.nextTag();
        Decoder decoder = context.getApplicationContext().getDecoder(xmlsr.getName());
        ElementVisitee elem = (ElementVisitee) decoder.decode(xmlsr);
        ElementVisitor handler = new DefaultElementVisitor();
        State nextState = elem.acceptElementVisitor(context, handler);
        Result result = new DefaultResult(nextState);
        return result;
      } catch (XMLStreamException e) {
        throw new XmppException(e, StreamError.BAD_FORMAT);
      }
    } catch (IOException ioe) {
      // close a StringReader/StringWriter should not cause IOException, though
      throw new XmppException(ioe, StreamError.INTERNAL_SERVER_ERROR);
    }
  }
}
