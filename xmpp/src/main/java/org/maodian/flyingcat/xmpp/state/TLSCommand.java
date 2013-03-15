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

import java.lang.invoke.MethodHandles;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.maodian.flyingcat.xmpp.codec.SecureSslContextFactory;
import org.maodian.flyingcat.xmpp.state.StreamState.TLSStreamState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Cole Wen
 *
 */
public class TLSCommand extends ContextAwareCommand {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
    SslHandler sslHandler = new SslHandler(engine, true);
    sslHandler.sslCloseFuture().addListener(new ChannelFutureListener() {
      
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        log.info("Close the socket since SSL connection has been closed by client");
        future.channel().close();
      }
    });
    ctx.pipeline().addFirst("ssl", sslHandler);
    
    xmlsw.writeEmptyElement("", "proceed", XmppNamespace.TLS);
    xmlsw.setPrefix("", XmppNamespace.TLS);
    xmlsw.writeNamespace("", XmppNamespace.TLS);
    xmlsw.writeEndDocument();
    return new TLSStreamState();
  }

}
