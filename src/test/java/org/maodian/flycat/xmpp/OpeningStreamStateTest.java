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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Reader;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Before;
import org.junit.Test;
import org.maodian.flycat.holder.XMLInputFactoryHolder;
import org.maodian.flycat.xmpp.OpeningStreamState.FeatureType;

/**
 * @author Cole Wen
 * 
 */
public class OpeningStreamStateTest {
  private XmppContext context;
  
  @Before
  public void setup() {
    context = mock(XmppContext.class);
  }

  @Test
  public void testSuccessWithStartTTLsFeatureType() throws XMLStreamException {
    OpeningStreamState state = new OpeningStreamState(FeatureType.STARTTLS);
    String inXML = "<stream:stream from='cole@localhost' to='localhost' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>";
    String outXML = state.handle(context, inXML);
    
    Reader reader = new StringReader(outXML);
    XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
    assertEquals(XMLStreamConstants.START_DOCUMENT, xmlsr.getEventType());
    assertEquals("1.0", xmlsr.getVersion());
    assertEquals(XMLStreamConstants.START_ELEMENT, xmlsr.next());
    assertEquals("cole@localhost", xmlsr.getAttributeValue("", "to"));
    assertEquals("localhost", xmlsr.getAttributeValue("", "from"));
    assertEquals("1.0", xmlsr.getAttributeValue("", "version"));
    assertEquals("en", xmlsr.getAttributeValue(XMLConstants.XML_NS_URI, "lang"));
    assertNotNull(xmlsr.getAttributeValue("", "id"));
    assertTrue(xmlsr.getAttributeValue("", "id").length() > 0);
  }
}
