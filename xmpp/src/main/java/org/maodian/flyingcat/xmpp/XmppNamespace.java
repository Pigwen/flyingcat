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
package org.maodian.flyingcat.xmpp;

public final class XmppNamespace {

  private XmppNamespace() {}
  
  public static final String STREAM = "http://etherx.jabber.org/streams";
  public static final String CLIENT_CONTENT = "jabber:client";
  public static final String TLS = "urn:ietf:params:xml:ns:xmpp-tls";
  public static final String SASL = "urn:ietf:params:xml:ns:xmpp-sasl";
  public static final String BIND = "urn:ietf:params:xml:ns:xmpp-bind";
  public static final String SESSION = "urn:ietf:params:xml:ns:xmpp-session";
  public static final String STANZAS = "urn:ietf:params:xml:ns:xmpp-stanzas";
  public static final String ROSTER = "jabber:iq:roster";
}
