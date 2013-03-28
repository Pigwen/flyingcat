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

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

import java.io.StringWriter;
import java.lang.invoke.MethodHandles;

import javax.net.ssl.SSLEngine;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flyingcat.holder.XMLOutputFactoryHolder;
import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.maodian.flyingcat.xmpp.codec.Encoder;
import org.maodian.flyingcat.xmpp.codec.InfoQueryProcessor;
import org.maodian.flyingcat.xmpp.codec.SecureSslContextFactory;
import org.maodian.flyingcat.xmpp.entity.Auth;
import org.maodian.flyingcat.xmpp.entity.InfoQuery;
import org.maodian.flyingcat.xmpp.entity.Presence;
import org.maodian.flyingcat.xmpp.entity.TLS;
import org.maodian.flyingcat.xmpp.state.StreamState.AuthenticatedStreamState;
import org.maodian.flyingcat.xmpp.state.StreamState.TLSStreamState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Cole Wen
 *
 */
public class FirstLevelElementVisitor implements Visitor {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.Visitor#handleInfoQuery(org.maodian.flyingcat.xmpp.entity.InfoQuery)
   */
  @Override
  public State handleInfoQuery(XmppContext ctx, InfoQuery iq) throws XMLStreamException {
    InfoQuery.Builder iqBuilder = new InfoQuery.Builder(iq.getId(), "result").from("localhost").to(iq.getFrom())
        .language("en");
    Object reqPayload = iq.getPayload();
    InfoQueryProcessor processor = ctx.getApplicationContext().getProcessor(reqPayload.getClass());
    Encoder encoder = ctx.getApplicationContext().getEncoder(InfoQuery.class);
    Object rspPayload = null;
    switch (iq.getType()) {
    case InfoQuery.GET:
      rspPayload = processor.processGet(ctx, iq);
      break;
    case InfoQuery.SET:
      rspPayload = processor.processSet(ctx, iq);
      break;
    case InfoQuery.RESULT:
      throw new IllegalStateException("The server does not support <iq /> type as RESULT");
    default:
      throw new IllegalStateException("Unrecognized state: " + iq.getType());
    }
    
    iqBuilder.payload(rspPayload);
    
    StringWriter writer = new StringWriter();
    XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
    encoder.encode(iqBuilder.build(), xmlsw);
    ctx.flush(writer.toString());
    return new SelectState();
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.Visitor#handlePresence(org.maodian.flyingcat.xmpp.entity.Presence)
   */
  @Override
  public void handlePresence(Presence p) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.Visitor#handleTLS(org.maodian.flyingcat.xmpp.state.XmppContext, org.maodian.flyingcat.xmpp.state.Visitor)
   */
  @Override
  public State handleTLS(XmppContext xmppCtx, TLS tls) throws XMLStreamException {
    ChannelHandlerContext ctx = xmppCtx.getNettyChannelHandlerContext();
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
    
    StringWriter writer = new StringWriter();
    XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
    xmlsw.writeEmptyElement("", "proceed", XmppNamespace.TLS);
    xmlsw.setPrefix("", XmppNamespace.TLS);
    xmlsw.writeNamespace("", XmppNamespace.TLS);
    xmlsw.writeEndDocument();
    xmppCtx.flush(writer.toString());
    return new TLSStreamState();
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.Visitor#handleSASL(org.maodian.flyingcat.xmpp.state.XmppContext, org.maodian.flyingcat.xmpp.entity.Auth)
   */
  @Override
  public State handleSASL(XmppContext ctx, Auth auth) throws XMLStreamException {
    ctx.login(auth.getAuthcid(), auth.getPassword());
    StringWriter writer = new StringWriter();
    XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
    xmlsw.setPrefix("", XmppNamespace.SASL);
    xmlsw.writeEmptyElement(XmppNamespace.SASL, "success");
    xmlsw.writeDefaultNamespace(XmppNamespace.SASL);
    xmlsw.writeEndDocument();
    ctx.flush(writer.toString());
    return new AuthenticatedStreamState();
  }

}