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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Before;

/**
 * @author Cole Wen
 *
 */
public abstract class CommandTest {
  protected XmppContext context;
  protected ContextAwareCommand cmd;
  
  @Before
  public void setup() {
    context = mock(XmppContext.class);
    doSetup();
  }
  
  public abstract void doSetup();
  
  protected XMLStreamReader newXMLStreamReader(Reader reader) throws Exception {
    return XMLInputFactory.newInstance().createXMLStreamReader(reader);
  }
  
  protected XMLStreamWriter newXMLStreamWriter(Writer writer) throws Exception {
    return XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
  }
  
  /**
   * expect an XmppException with the desired XmppError would be thrown while the 
   * State parameter handles the xml string 
   * @param cmd
   * @param xml
   * @param error
   * @throws Exception 
   */
  protected void expectXmppException(Command cmd, String xml, XmppError error) throws Exception  {
    try {
      Writer writer = new StringWriter();
      XMLStreamReader xmlsr = newXMLStreamReader(new StringReader(xml));
      XMLStreamWriter xmlsw = newXMLStreamWriter(writer);
      // skip the start_document
      xmlsr.nextTag();
      cmd.execute(xmlsr, xmlsw);
      fail("Should throw an XmppException");
    } catch (XmppException e) {
      assertSame(error, e.getXmppError());
    }
  }
}
