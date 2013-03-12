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

import org.maodian.flyingcat.im.InMemorySession;
import org.maodian.flyingcat.im.Session;
import org.maodian.flyingcat.xmpp.ApplicationContext;



/**
 * The <code>XmppContext</code> class aggregates information for a client connection.
 * 
 * Each client connection owns an <code>XmppContext</code> instance, which keeps per connection
 * information such as the current {@link State}, bareJID/resource of the initial entity.
 * <p>
 * The <code>XmppContext</code> class, <code>State</code> interface (and its implemented classes)
 * employ the <a href='http://en.wikipedia.org/wiki/State_pattern'>State Pattern</a>.
 * 
 * @author Cole Wen
 * @see State
 */
public class XmppContext {
  private final ChannelHandlerContext ctx;
  private State state;
  private String bareJID;
  private String resource;
  private String streamTag;
  
  private XmppContext(ChannelHandlerContext ctx) {
    this.ctx = ctx;
    this.state = States.newOpeningStreamState();
  }

  void setState(State state) {
    this.state = state;
  }
  
  void setBareJID(String bareJID) {
    this.bareJID = bareJID;
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
  
  ChannelHandlerContext getNettyChannelHandlerContext() {
    return ctx;
  }
  
  String wrapStreamTag(String xml) {
    return streamTag + xml;
  }

  public Command lookup(QName qName) {
    Class<? extends ContextAwareCommand> cmd = ApplicationContext.getInstance().getCommand(qName);
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
  
  public Session createIMSession() {
    return new InMemorySession(bareJID);
  }
  
  public static XmppContext newInstance(ChannelHandlerContext ctx) {
    return new XmppContext(ctx);
  }

}
