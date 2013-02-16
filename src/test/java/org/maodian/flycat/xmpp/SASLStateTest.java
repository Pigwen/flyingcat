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

import java.io.Reader;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;
import org.maodian.flycat.holder.XMLInputFactoryHolder;

/**
 * @author Cole Wen
 * 
 */
public class SASLStateTest extends StateTest {

  /*
   * (non-Javadoc)
   * 
   * @see org.maodian.flycat.xmpp.StateTest#doSetup()
   */
  @Override
  public void doSetup() {

  }

  @Test
  public void testPlainMechanismSuccess() throws XMLStreamException {
    state = new SASLState();
    String inXML1 = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>";
    String inXML2 = "AGp1bGlldAByMG0zMG15cjBtMzA=</auth>";

    state.handle(context, inXML1);
    String outXML = state.handle(context, inXML2);

    Reader reader = new StringReader(outXML);
    XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);

    xmlsr.next();
    QName qname = new QName(XmppNamespace.SASL, "success");
    assertEquals(qname, xmlsr.getName());
  }
  
  @Test
  public void testInvalidNamespaceOfPlainAuth() throws XMLStreamException {
    state = new SASLState();
    String inXML1 = "<auth xmlns='invalid name space' mechanism='PLAIN'>";
    String inXML2 = "AGp1bGlldAByMG0zMG15cjBtMzA=</auth>";
    state.handle(context, inXML1);
    expectXmppException(state, inXML2, StreamError.INVALID_NAMESPACE);
  }
}
