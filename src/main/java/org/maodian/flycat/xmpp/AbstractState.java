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
package org.maodian.flycat.xmpp;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flycat.holder.XMLInputFactoryHolder;
import org.maodian.flycat.holder.XMLOutputFactoryHolder;

/**
 * @author Cole Wen
 *
 */
public abstract class AbstractState implements State {
  protected StringBuilder cachedXML = new StringBuilder();

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.State#handle(org.maodian.flycat.xmpp.XmppContext, java.lang.String)
   */
  @Override
  public String handle(XmppContext context, String xml) {
    if (preHandle(context, xml)) {
      try (Reader reader = new StringReader(cachedXML.toString());
          StringWriter writer = new StringWriter();) {
        try {
          XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
          XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
          doHandle(context, xmlsr, xmlsw);
          postHandle(context);
          
          context.setState(nextState());
          // clear the content of cachedXML upon success
          cachedXML = new StringBuilder();
          return writer.toString();
        } catch (XMLStreamException e) {
          throw new XmppException(e, StreamError.BAD_FORMAT);
        }
      } catch (IOException ioe) {
        // close a StringReader/StringWriter should not cause IOException, though
        throw new XmppException(ioe, StreamError.INTERNAL_SERVER_ERROR);
      }
    }
    return "";
  }
  
  protected boolean preHandle(XmppContext context, String xml) {
    cachedXML.append(xml);
    return true;
  }
  
  protected void postHandle(XmppContext context) {
    
  }
  
  protected abstract State nextState();
  
  protected abstract void doHandle(XmppContext context, XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException;
}
