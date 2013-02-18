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
 * The <code>AbstractState</code> class intends to be extended by {@link State} implementation
 * other than build an {@link State} from scratch.
 * 
 * @author Cole Wen
 * @see State
 */
public abstract class AbstractState implements State {
  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.State#handle(org.maodian.flycat.xmpp.XmppContext, java.lang.String)
   */
  @Override
  public String handle(XmppContext context, String xml) {
    String fragment = preHandle(context, xml);
    try (Reader reader = new StringReader(fragment);
        StringWriter writer = new StringWriter();) {
      try {
        XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
        XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
        doHandle(context, xmlsr, xmlsw);
        postHandle(context, fragment);

        context.setState(nextState());
        return writer.toString();
      } catch (XMLStreamException e) {
        throw new XmppException(e, StreamError.BAD_FORMAT);
      }
    } catch (IOException ioe) {
      // close a StringReader/StringWriter should not cause IOException, though
      throw new XmppException(ioe, StreamError.INTERNAL_SERVER_ERROR);
    }
  }
  
  /**
   * This method provide a chance for implementing class to do some extra work before start
   * handing the xml sent by client.
   * The return value will be used to construct the {@link XMLStreamReader}. 
   * 
   * @param context
   * @param xml
   * @return The return value will be used to construct the {@link XMLStreamReader}. 
   */
  protected String preHandle(XmppContext context, String xml) {
    return xml;
  }
  
  /**
   * This method provide a chance for implementing class to do some cleanup work after complementing
   * handling the xml sent by client.
   * 
   * @param context
   * @param xml
   */
  protected void postHandle(XmppContext context, String xml) {
    
  }
  
  /**
   * Return the next {@link State} the {@link XmppContext} will transit to.
   * 
   * @return
   */
  protected abstract State nextState();
  
  /**
   * Subclass should override this method to do the bussiness logic of handling the xml sent by client.
   * 
   * @param context
   * @param xmlsr
   * @param xmlsw
   * @throws XMLStreamException
   */
  protected abstract void doHandle(XmppContext context, XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException;
}
