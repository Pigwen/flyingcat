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

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flyingcat.xmpp.SecureSslContextFactory;
import org.maodian.flyingcat.xmpp.StreamError;
import org.maodian.flyingcat.xmpp.XmppException;
import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.maodian.flyingcat.xmpp.state.StreamState.TLSStreamState;

/**
 * @author Cole Wen
 *
 */
public class TLSCommand extends ContextAwareCommand {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.state.Command#execute(javax.xml.stream.XMLStreamReader, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  public State execute(XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException {
    QName qname = new QName(XmppNamespace.TLS, "starttls");
    if (!xmlsr.getName().equals(qname)) {
      throw new XmppException(StreamError.INVALID_NAMESPACE)
          .set("QName", xmlsr.getName());
    }
    
    ChannelHandlerContext ctx = getXmppContext().getNettyChannelHandlerContext();
    SSLEngine engine = SecureSslContextFactory.getServerContext().createSSLEngine();
    engine.setUseClientMode(false);
    ctx.pipeline().addFirst("ssl", new SslHandler(engine, true));
    
    xmlsw.writeEmptyElement("", "proceed", XmppNamespace.TLS);
    xmlsw.setPrefix("", XmppNamespace.TLS);
    xmlsw.writeNamespace("", XmppNamespace.TLS);
    xmlsw.writeEndDocument();
    return new TLSStreamState();
  }

}