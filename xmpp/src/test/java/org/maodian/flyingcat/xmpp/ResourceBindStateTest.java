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
package org.maodian.flyingcat.xmpp;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Reader;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;
import org.maodian.flyingcat.holder.XMLInputFactoryHolder;
import org.maodian.flyingcat.xmpp.ResourceBindState;
import org.maodian.flyingcat.xmpp.StreamError;
import org.maodian.flyingcat.xmpp.XmppNamespace;

/**
 * @author Cole Wen
 *
 */
public class ResourceBindStateTest extends StateTest {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.StateTest#doSetup()
   */
  @Override
  public void doSetup() {
    state = new ResourceBindState();
  }

  @Test
  public void testSuccess() throws XMLStreamException {
    String inXML = "<iq id='tn281v37' type='set'><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>" +
    		"<resource>balcony</resource></bind></iq>";
    String wrappedInXML = "<stream:stream from='juliet@im.example.com' id='gPybzaOzBmaADgxKXu9UClbprp0=' to='im.example.com' "
    		+ "version='1.0' xml:lang='en' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>"
        + inXML + "</stream:stream>";
    
    when(context.wrapStreamTag(inXML)).thenReturn(wrappedInXML);
    when(context.getBareJID()).thenReturn("juliet@im.example.com");
    
    String outXML = state.handle(context, inXML);
    Reader reader = new StringReader(outXML);
    XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
    
    xmlsr.next();
    QName iq = new QName("iq");
    assertEquals(iq, xmlsr.getName());
    assertEquals("tn281v37", xmlsr.getAttributeValue("", "id"));
    assertEquals("result", xmlsr.getAttributeValue("", "type"));
    
    xmlsr.next();
    QName bind = new QName(XmppNamespace.BIND, "bind");
    assertEquals(bind, xmlsr.getName());
    
    xmlsr.next();
    QName jid = new QName(XmppNamespace.BIND, "jid");
    assertEquals(jid, xmlsr.getName());
    assertEquals("juliet@im.example.com/balcony", xmlsr.getElementText());
  }
  
  @Test
  public void testInvalidXML() {
    String inXML = "invalid xml</iq>";
    String wrappedInXML = "<stream:stream from='juliet@im.example.com' id='gPybzaOzBmaADgxKXu9UClbprp0=' to='im.example.com' "
        + "version='1.0' xml:lang='en' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>"
        + inXML + "</stream:stream>";
    when(context.wrapStreamTag(inXML)).thenReturn(wrappedInXML);
    when(context.getBareJID()).thenReturn("juliet@im.example.com");
    
    expectXmppException(state, inXML, StreamError.BAD_FORMAT);
  }
}
