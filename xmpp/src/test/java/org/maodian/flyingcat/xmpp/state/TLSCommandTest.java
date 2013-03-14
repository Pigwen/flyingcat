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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Test;
import org.maodian.flyingcat.holder.XMLInputFactoryHolder;
import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.maodian.flyingcat.xmpp.state.StreamState.TLSStreamState;

/**
 * @author Cole Wen
 * 
 */
public class TLSCommandTest extends CommandTest {
  private ChannelHandlerContext nettyContext;
  private ChannelPipeline pipeline;
  
  @Override
  public void doSetup() {
    cmd = new TLSCommand();
    cmd.setXmppContext(context);
    nettyContext = mock(ChannelHandlerContext.class);
    pipeline = mock(ChannelPipeline.class);
    
    when(context.getNettyChannelHandlerContext()).thenReturn(nettyContext);
    when(nettyContext.pipeline()).thenReturn(pipeline);
  }
  
  @Test
  public void testSuccess() throws Exception {
    String inXML = "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>";
    Writer writer = new StringWriter();
    XMLStreamReader xmlsr1 = newXMLStreamReader(new StringReader(inXML));
    XMLStreamWriter xmlsw = newXMLStreamWriter(writer);
    // skip the start_document
    xmlsr1.nextTag();
    State state = cmd.execute(xmlsr1, xmlsw);
    Reader reader = new StringReader(writer.toString());
    XMLStreamReader xmlsr2 = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
    xmlsr2.next();
    QName qname = new QName(XmppNamespace.TLS, "proceed");
    assertEquals(qname, xmlsr2.getName());
    assertTrue(state instanceof TLSStreamState);
  }
  
  @Test
  public void testInvalidNamespaceOfStartTLS() throws Exception {
    String inXML = "<starttls xmlns='invalid-namespace'/>";
    expectXmppException(cmd, inXML, StreamError.INVALID_NAMESPACE);
  }
}
