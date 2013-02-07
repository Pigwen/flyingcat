/*
 * #%L
 * flyingcat
 * %%
 * Copyright (C) 2013 Ke Wen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/**
 * 
 */
package org.maodian.flycat.xmpp;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.maodian.flycat.holder.XMLInputFactoryHolder;
import org.maodian.flycat.holder.XMLOutputFactoryHolder;


/**
 * @author Cole Wen
 *
 */
public class ResourceBindState implements State {
  private StringBuilder cachedXML = new StringBuilder();

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.State#handle(org.maodian.flycat.xmpp.XmppContext, java.lang.String)
   */
  @Override
  public String handle(XmppContext context, String xml) {
    if (!StringUtils.endsWith(xml, "</iq>")) {
      cachedXML.append(xml);
      return "";
    }
    String streamWrappedXML = context.wrapStreamTag(cachedXML.append(xml).toString());
    try (Reader reader = new StringReader(streamWrappedXML);
        StringWriter writer = new StringWriter();) {
      try {
        /* parse xml fragement like:
         * 
         * <iq id='wy2xa82b4' type='set'>
         *   <bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>
         *     <resource>balcony</resource>
         *   </bind>
         * </iq>
         */
        XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
        // skip the stream tag
        xmlsr.nextTag();
        xmlsr.nextTag();
        xmlsr.require(XMLStreamConstants.START_ELEMENT, XmppNamespace.CONTENT, "iq");
        String type = xmlsr.getAttributeValue("", "type");
        String id = xmlsr.getAttributeValue("", "id");
        if (StringUtils.equals(type, "set") && StringUtils.isNotBlank("id")) {
          xmlsr.nextTag();
          xmlsr.require(XMLStreamConstants.START_ELEMENT, XmppNamespace.BIND, "bind");
          
          xmlsr.nextTag();
          xmlsr.require(XMLStreamConstants.START_ELEMENT, XmppNamespace.BIND, "resource");
          String resource = xmlsr.getElementText();
          context.setResource(resource);
          
          XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
          xmlsw.writeStartElement("iq");
          xmlsw.writeAttribute("id", id);
          xmlsw.writeAttribute("type", "result");
          
          xmlsw.writeStartElement("bind");
          xmlsw.writeDefaultNamespace(XmppNamespace.BIND);
          
          xmlsw.writeStartElement("jid");
          xmlsw.writeCharacters(context.getBareJID() + "/" + resource);
          
          xmlsw.writeEndElement();
          xmlsw.writeEndElement();
          xmlsw.writeEndElement();
          
          return writer.toString();
        }
        
      } catch (XMLStreamException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Invalid bind resource request");
  }

}
