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

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.maodian.flyingcat.holder.XMLOutputFactoryHolder;
import org.maodian.flyingcat.im.IMException;
import org.maodian.flyingcat.im.IMSession;
import org.maodian.flyingcat.im.Type;
import org.maodian.flyingcat.im.Verb;
import org.maodian.flyingcat.im.entity.Account;
import org.maodian.flyingcat.xmpp.GlobalContext;
import org.maodian.flyingcat.xmpp.codec.Encoder;
import org.maodian.flyingcat.xmpp.entity.JabberID;
import org.maodian.flyingcat.xmpp.state.StreamState.OpeningStreamState;

/**
 * 
 * @author Cole Wen
 * 
 */
public class DefaultXmppContext implements XmppContext {
  private final GlobalContext appCtx;
  private final IMSession imSession;
  private ChannelHandlerContext nettyCtx;
  private State state;
  private JabberID jid;
  private String streamTag;
  private XmppContextManager xmppCtxMgr;
  private Set<XmppContextListener> listeners;

  /**
   * @param nettyCtx
   * @param appCtx
   */
  public DefaultXmppContext(GlobalContext appCtx, IMSession imSession) {
    this.appCtx = appCtx;
    this.imSession = imSession;
    listeners = new CopyOnWriteArraySet<>();
    state = new OpeningStreamState();
  }

  @Override
  public void setState(State state) {
    this.state = state;
  }

  @Override
  public void setStreamTag(String streamTag) {
    this.streamTag = streamTag;
  }

  @Override
  public ChannelHandlerContext getNettyChannelHandlerContext() {
    return nettyCtx;
  }

  @Override
  public void setNettyChannelHandlerContext(ChannelHandlerContext nettyCtx) {
    this.nettyCtx = nettyCtx;
  }

  public void setXmppContextManger(XmppContextManager xmppCtxMgr) {
    this.xmppCtxMgr = xmppCtxMgr;
    listeners.add((XmppContextListener) this.xmppCtxMgr);
  }
  
  public void setListeners(Set<XmppContextListener> listeners) {
    this.listeners.addAll(listeners);
  }

  @Override
  public String wrapStreamTag(String xml) {
    return streamTag + xml;
  }

  @Override
  public void login(String username, String password) {
    try {
      preLogin();
      imSession.login(username, password);
      setJabberID(JabberID.fromString(username + "@localhost"));
      postLogin();
    } catch (IMException e) {
      throw new XmppException(e, SASLError.NOT_AUTHORIZED);
    }
  }

  private void preLogin() {
    for (XmppContextListener listener : listeners) {
      listener.onPreLogin(this);
    }
  }

  private void postLogin() {
    for (XmppContextListener listener : listeners) {
      listener.onPostLogin(this);
    }
  }

  @Override
  public void destroy() {
    preDestroy();
    if (imSession != null) {
      imSession.destroy();
    }
    postDestroy();
  }

  private void preDestroy() {
    for (XmppContextListener listener : listeners) {
      listener.onPreDestroy(this);
    }
  }

  private void postDestroy() {
    for (XmppContextListener listener : listeners) {
      listener.onPostDestroy(this);
    }
  }

  @Override
  public Command lookup(QName qName) {
    Class<? extends ContextAwareCommand> cmd = appCtx.getCommand(qName);
    if (cmd == null) {
      throw new RuntimeException("No suitable Command for QName: " + qName.toString());
    }
    try {
      ContextAwareCommand instance = cmd.newInstance();
      instance.setXmppContext(this);
      return instance;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Require a public no argument constructor", e);
    }
  }

  @Override
  public void parseXML(final String xml) {
    Result result = state.step(this, xml);
    state = result.getNextState();
  }

  @Override
  public IMSession getIMSession() {
    return imSession;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.maodian.flyingcat.xmpp.state.XmppContext#getApplicationContext()
   */
  @Override
  public GlobalContext getApplicationContext() {
    return appCtx;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.maodian.flyingcat.xmpp.state.XmppContext#flush(java.lang.String)
   */
  @Override
  public void flush(String str) {
    if (StringUtils.isNotBlank(str)) {
      nettyCtx.write(str).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      nettyCtx.flush();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.maodian.flyingcat.xmpp.state.XmppContext#setJabberID(org.maodian.flyingcat
   * .xmpp.entity.JabberID)
   */
  @Override
  public void setJabberID(JabberID jid) {
    this.jid = jid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.maodian.flyingcat.xmpp.state.XmppContext#getJabberID()
   */
  @Override
  public JabberID getJabberID() {
    return jid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.maodian.flyingcat.xmpp.state.XmppContext#send(org.maodian.flyingcat
   * .xmpp.entity.JabberID, java.lang.Object)
   */
  @Override
  public void send(JabberID to, Object payload) {
    Account ta = (Account) imSession.action(Verb.RETRIEVE, Type.PERSON, to.getUid());
    if (ta == null) {
      throw new RuntimeException("User does not existed");
    }
    preSend(payload);
    xmppCtxMgr.transfer(getJabberID(), to, payload);
    postSend(payload);
  }
  
  private void preSend(Object payload) {
    
  }
  
  private void postSend(Object payload) {
    for (XmppContextListener listener : listeners) {
      listener.onPostSend(this, payload);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.maodian.flyingcat.xmpp.state.XmppContext#receive(org.maodian.flyingcat
   * .xmpp.entity.JabberID, java.lang.Object)
   */
  @Override
  public void receive(JabberID from, Object payload) {
    Encoder encoder = appCtx.getEncoder(payload.getClass());
    try (Writer writer = new StringWriter();) {
      XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
      encoder.encode(payload, xmlsw);
      flush(writer.toString());
    } catch (XMLStreamException | IOException e) {
      throw new RuntimeException("Error when receiving payload", e);
    }
  }
}
