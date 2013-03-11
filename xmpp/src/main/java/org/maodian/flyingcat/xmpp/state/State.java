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

/**
 * The <code>State</code> interface represents a State that a {@link XmppContext} can transit to.
 * <p>
 * The XmppContext (or client connection) can transit to different <code>State</code> upon in which 
 * phase current XmppContext (or client connection) is.
 * Different <code>State</code> can be used to handle different input from the client and send response
 * to client accordingly. For instance, the {@link StreamState} is used to handle the the 
 * <em>Stream Negotiation</em>, while {@link ResourceBindState} is used to handle the 
 * <em>Resource Binding</em>
 * <p>
 * It is supposed to use the static factory methods of {@link States} class to create <code>State</code>
 * object.
 * <p>
 * The <code>XmppContext</code> class, <code>State</code> interface (and its implemented classes)
 * employ the <a href='http://en.wikipedia.org/wiki/State_pattern'>State Pattern</a>.
 * 
 * @author Cole Wen
 * @see AbstractState
 * @see XmppContext
 * @see States
 */
public interface State {

  /**
   * handle the xml sent by client. The return value of this method will be sent to client.
   * <p>
   * If this method return null or "" (empty string), nothing will be sent to client.
   * 
   * @param context XmppContext object belongs to current connection
   * @param xml xml data sent by client
   * @return
   */
  Result step(XmppContext context, String xml);
}
