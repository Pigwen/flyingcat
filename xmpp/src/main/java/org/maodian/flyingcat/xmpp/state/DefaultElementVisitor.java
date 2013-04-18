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
import org.maodian.flyingcat.im.entity.sql.AccountEntity;
import org.maodian.flyingcat.im.entity.sql.ContactEntity;
import org.maodian.flyingcat.im.repository.sql.AccountRepository;
import org.maodian.flyingcat.im.repository.sql.ContactReporitory;
import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.maodian.flyingcat.xmpp.codec.Encoder;
import org.maodian.flyingcat.xmpp.codec.SecureSslContextFactory;
import org.maodian.flyingcat.xmpp.entity.Auth;
import org.maodian.flyingcat.xmpp.entity.InfoQuery;
import org.maodian.flyingcat.xmpp.entity.JabberID;
import org.maodian.flyingcat.xmpp.entity.Presence;
import org.maodian.flyingcat.xmpp.entity.TLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Cole Wen
 *
 */
public class DefaultElementVisitor implements ElementVisitor, PersistedVisitor {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.Visitor#handleInfoQuery(org.maodian.flyingcat.xmpp.entity.InfoQuery)
   */
  @Override
  public State handleInfoQuery(XmppContext ctx, InfoQuery iq) throws XMLStreamException {
    InfoQuery.Builder iqBuilder = new InfoQuery.Builder(iq.getId(), "result").from("localhost").to(iq.getFrom())
        .language("en");
    Object reqPayload = iq.getPayload();
    Encoder encoder = ctx.getGlobalContext().getEncoder(InfoQuery.class);
    Object rspPayload = null;
    switch (iq.getType()) {
    case InfoQuery.GET:
      rspPayload = ctx.getGlobalContext().getProcessor(reqPayload.getClass()).processGet(ctx, iq);
      break;
    case InfoQuery.SET:
      rspPayload = ctx.getGlobalContext().getProcessor(reqPayload.getClass()).processSet(ctx, iq);
      break;
    case InfoQuery.RESULT:
      return ctx.getGlobalContext().getSelectState();
    default:
      throw new IllegalStateException("Unrecognized state: " + iq.getType());
    }
    
    iqBuilder.payload(rspPayload);
    
    StringWriter writer = new StringWriter();
    XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
    encoder.encode(iqBuilder.build(), xmlsw);
    ctx.flush(writer.toString());
    return ctx.getGlobalContext().getSelectState();
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.Visitor#handlePresence(org.maodian.flyingcat.xmpp.entity.Presence)
   */
  @Override
  public State handlePresence(XmppContext ctx, Presence p) throws XMLStreamException {
    if (p.isSignalAvailability()) {
      ctx.broadcastPresence();
    } else {
      JabberID from = ctx.getJabberID();
      
      // normalize both from/to to bareJID
      JabberID f = JabberID.fromString(from.toBareJID());
      JabberID t = JabberID.fromString(p.getTo().toBareJID());
      Presence presence = new Presence();
      presence.setFrom(f);
      presence.setTo(t);
      presence.setId(p.getId());
      presence.setType(p.getType());
      
      ctx.send(presence.getTo(), presence);
    }
    return ctx.getGlobalContext().getSelectState();
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
    return xmppCtx.getGlobalContext().getTlsStreamState();
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
    return ctx.getGlobalContext().getAuthenticatedStreamState();
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.PersistedVisitor#persistPresenceSubscription(org.maodian.flyingcat.xmpp.state.XmppContext, org.maodian.flyingcat.xmpp.entity.PersistedVisitee)
   */
  @Override
  @Transactional
  public void persistPresenceSubscription(XmppContext ctx, Presence p) {
    if (!p.isSignalAvailability()) {
      String from = p.getFrom().getUid();
      String to = p.getTo().getUid();
      AccountRepository repo = ctx.getIMSession().getAccountRepository();
      ContactReporitory contactRepo = ctx.getIMSession().getContactRepository();

      // check if user has add contact into the roster
      AccountEntity user = repo.findByUid(from);
      ContactEntity fCont = user.getContactsMap().get(to);
      if (fCont == null) {
        fCont = new ContactEntity(to, to);
        fCont.setOwner(user);
      }

      // check if contact has added user into the roster
      AccountEntity targetUser = repo.findByUid(to);
      ContactEntity rCont = targetUser.getContact(from);
      if (rCont == null) {
        rCont = new ContactEntity(from, from);
        rCont.setOwner(targetUser);
      }
      switch (p.getType()) {
      case SUBSCRIBE:
        fCont.setOutbound(true);
        rCont.setInbound(true);
        contactRepo.save(fCont);
        contactRepo.save(rCont);
        break;
      case SUBSCRIBED:
        fCont.setInbound(false);
        fCont.setSubFrom(true);
        rCont.setOutbound(false);
        rCont.setSubTo(true);
        contactRepo.save(fCont);
        contactRepo.save(rCont);
        break;
      case UNSUBSCRIBE:
      case UNSUBSCRIBED:
      default:
        throw new RuntimeException("should not reach here");
      }
    }
  }
}
