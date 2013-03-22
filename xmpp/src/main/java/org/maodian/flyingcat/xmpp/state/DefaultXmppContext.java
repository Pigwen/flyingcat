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

import javax.xml.namespace.QName;

import org.maodian.flyingcat.im.IMException;
import org.maodian.flyingcat.im.IMSession;
import org.maodian.flyingcat.xmpp.GlobalContext;
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
  private String username;
  private String bareJID;
  private String resource;
  private String streamTag;
  
  /**
   * @param nettyCtx
   * @param appCtx
   */
  public DefaultXmppContext(GlobalContext appCtx, IMSession imSession) {
    this.appCtx = appCtx;
    this.imSession = imSession;
    state = new OpeningStreamState();
  }

  public void setState(State state) {
    this.state = state;
  }
  
  public void setResource(String resource) {
    if (this.resource != null) {
      throw new IllegalStateException("Resource has already been set");
    }
    this.resource = resource;
  }
  
  public void setStreamTag(String streamTag) {
    this.streamTag = streamTag;
  }

  public String getResource() {
    return resource;
  }

  public String getBareJID() {
    return bareJID;
  }
  
  public String getUsername() {
    return username;
  }
  
  public ChannelHandlerContext getNettyChannelHandlerContext() {
    return nettyCtx;
  }
  
  public void setNettyChannelHandlerContext(ChannelHandlerContext nettyCtx) {
    this.nettyCtx = nettyCtx;
  }
  
  public String wrapStreamTag(String xml) {
    return streamTag + xml;
  }
  
  public void login(String username, String password) {
    try {
      imSession.login(username, password);
      this.username = username;
      this.bareJID = username + "@localhost";
    } catch (IMException e) {
      throw new XmppException(e, SASLError.NOT_AUTHORIZED);
    }
  }
  
  public void destroy() {
    if (imSession != null) {
      imSession.destroy();
    }
  }

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

  public String parseXML(final String xml) {
    Result result = state.step(this, xml);
    state = result.getNextState();
    return result.getData();
  }
  
  public IMSession getIMSession() {
    return imSession;
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.XmppContext#getApplicationContext()
   */
  @Override
  public GlobalContext getApplicationContext() {
    return appCtx;
  }
}
