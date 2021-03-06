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

import org.maodian.flyingcat.im.IMSession;
import org.maodian.flyingcat.xmpp.GlobalContext;
import org.maodian.flyingcat.xmpp.entity.JabberID;



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
public interface XmppContext {
  
  void setState(State state);
  
  JabberID getJabberID();
  
  void setStreamTag(String streamTag);
  
  void bind(String resource);

  ChannelHandlerContext getNettyChannelHandlerContext();
  
  void setNettyChannelHandlerContext(ChannelHandlerContext nettyCtx);
  
  GlobalContext getGlobalContext();
  
  String wrapStreamTag(String xml);
  
  void login(String username, String password);
  
  void destroy();

  void parseXML(final String xml);
  
  IMSession getIMSession();
  
  void flush(String str);
  
  void send(JabberID to, Object payload);
  
  void broadcastPresence();

  /**
   * @param from
   * @param payload
   */
  void receive(JabberID from, Object payload);
}
