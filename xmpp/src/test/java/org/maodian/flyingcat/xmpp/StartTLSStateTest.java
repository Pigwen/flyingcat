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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import java.io.Reader;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;
import org.maodian.flyingcat.holder.XMLInputFactoryHolder;
import org.maodian.flyingcat.xmpp.StartTLSState;
import org.maodian.flyingcat.xmpp.StreamError;
import org.maodian.flyingcat.xmpp.XmppNamespace;

/**
 * @author Cole Wen
 * 
 */
public class StartTLSStateTest extends StateTest {
  private ChannelHandlerContext nettyContext;
  private ChannelPipeline pipeline;

  public void doSetup() {
    state = new StartTLSState();
    nettyContext = mock(ChannelHandlerContext.class);
    pipeline = mock(ChannelPipeline.class);
    
    when(context.getNettyChannelHandlerContext()).thenReturn(nettyContext);
    when(nettyContext.pipeline()).thenReturn(pipeline);
  }
  
  @Test
  public void testSuccess() throws XMLStreamException {
    String inXML = "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>";
    String outXML = state.handle(context, inXML);
    Reader reader = new StringReader(outXML);
    XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
    xmlsr.next();
    QName qname = new QName(XmppNamespace.TLS, "proceed");
    assertEquals(qname, xmlsr.getName());
  }
  
  @Test
  public void testInvalidNamespaceOfStartTLS() throws XMLStreamException {
    String inXML = "<starttls xmlns='invalid-namespace'/>";
    expectXmppException(state, inXML, StreamError.INVALID_NAMESPACE);
  }
  
  @Test
  public void testInvalidXML() {
    testInvalidXML(state);
  }
}
