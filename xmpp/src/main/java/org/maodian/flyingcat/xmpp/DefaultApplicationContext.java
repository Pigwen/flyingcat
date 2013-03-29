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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.maodian.flyingcat.xmpp.codec.BindCodec;
import org.maodian.flyingcat.xmpp.codec.Decoder;
import org.maodian.flyingcat.xmpp.codec.Encoder;
import org.maodian.flyingcat.xmpp.codec.InfoQueryCodec;
import org.maodian.flyingcat.xmpp.codec.InfoQueryProcessor;
import org.maodian.flyingcat.xmpp.codec.PresenceCodec;
import org.maodian.flyingcat.xmpp.codec.RosterCodec;
import org.maodian.flyingcat.xmpp.codec.SASLCodec;
import org.maodian.flyingcat.xmpp.codec.SessionCodec;
import org.maodian.flyingcat.xmpp.codec.TLSCodec;
import org.maodian.flyingcat.xmpp.entity.Bind;
import org.maodian.flyingcat.xmpp.entity.InfoQuery;
import org.maodian.flyingcat.xmpp.entity.Presence;
import org.maodian.flyingcat.xmpp.entity.Roster;
import org.maodian.flyingcat.xmpp.entity.Session;
import org.maodian.flyingcat.xmpp.state.ContextAwareCommand;
import org.maodian.flyingcat.xmpp.state.InfoQueryCommand;
import org.maodian.flyingcat.xmpp.state.PresenceCommand;
import org.maodian.flyingcat.xmpp.state.SASLCommand;
import org.maodian.flyingcat.xmpp.state.TLSCommand;

/**
 * @author Cole Wen
 * 
 */
public class DefaultApplicationContext implements GlobalContext {
  private Map<QName, Decoder> decoderMap = new ConcurrentHashMap<>();
  private Map<Class<?>, Encoder> encoderMap = new ConcurrentHashMap<>();
  private Map<Class<?>, InfoQueryProcessor> processorMap = new ConcurrentHashMap<>();
  private Map<QName, Class<? extends ContextAwareCommand>> cmdMap = new ConcurrentHashMap<>();

  private InfoQueryCodec infoQueryCodec;
  private BindCodec bindCodec;
  private SessionCodec sessionCodec;
  private RosterCodec rosterCodec;
  private TLSCodec tlsCodec;
  private SASLCodec saslCodec;
  private PresenceCodec presenceCodec;

  private boolean init = false;

  DefaultApplicationContext() {

  }

  @PostConstruct
  public void init() {
    if (init == false) {
      decoderMap.put(new QName(XmppNamespace.CLIENT_CONTENT, "iq"), infoQueryCodec);
      decoderMap.put(new QName(XmppNamespace.BIND, "bind"), bindCodec);
      decoderMap.put(new QName(XmppNamespace.SESSION, "session"), sessionCodec);
      decoderMap.put(new QName(XmppNamespace.ROSTER, "query"), rosterCodec);
      decoderMap.put(new QName(XmppNamespace.TLS, "starttls"), tlsCodec);
      decoderMap.put(new QName(XmppNamespace.SASL, "auth"), saslCodec);
      decoderMap.put(new QName(XmppNamespace.CLIENT_CONTENT, "presence"), presenceCodec);

      encoderMap.put(InfoQuery.class, infoQueryCodec);
      encoderMap.put(Bind.class, bindCodec);
      encoderMap.put(Session.class, sessionCodec);
      encoderMap.put(Roster.class, rosterCodec);
      encoderMap.put(Presence.class, presenceCodec);

      processorMap.put(Session.class, sessionCodec);
      processorMap.put(Bind.class, bindCodec);
      processorMap.put(Roster.class, rosterCodec);

      cmdMap.put(new QName(XmppNamespace.TLS, "starttls"), TLSCommand.class);
      cmdMap.put(new QName(XmppNamespace.SASL, "auth"), SASLCommand.class);
      cmdMap.put(new QName(XmppNamespace.CLIENT_CONTENT, "iq"), InfoQueryCommand.class);
      cmdMap.put(new QName(XmppNamespace.CLIENT_CONTENT, "presence"), PresenceCommand.class);
      init = true;
    }
  }

  public void registerDecoder(QName qname, Decoder decoder) {
    decoderMap.put(qname, decoder);
  }

  public void registerEncoder(Class<?> clazz, Encoder encoder) {
    encoderMap.put(clazz, encoder);
  }

  public void registerProcessor(Class<?> clazz, InfoQueryProcessor processor) {
    processorMap.put(clazz, processor);
  }

  public Decoder getDecoder(QName qname) {
    return decoderMap.get(qname);
  }

  public Encoder getEncoder(Class<?> clazz) {
    return encoderMap.get(clazz);
  }

  public InfoQueryProcessor getProcessor(Class<?> clazz) {
    return processorMap.get(clazz);
  }

  public Class<? extends ContextAwareCommand> getCommand(QName qName) {
    return cmdMap.get(qName);
  }

  @Inject
  void setInfoQueryCodec(InfoQueryCodec infoQueryCodec) {
    this.infoQueryCodec = infoQueryCodec;
  }

  @Inject
  void setBindCodec(BindCodec bindCodec) {
    this.bindCodec = bindCodec;
  }

  @Inject
  void setSessionCodec(SessionCodec sessionCodec) {
    this.sessionCodec = sessionCodec;
  }

  @Inject
  void setRosterCodec(RosterCodec rosterCodec) {
    this.rosterCodec = rosterCodec;
  }

  @Inject
  void setTlsCodec(TLSCodec tlsCodec) {
    this.tlsCodec = tlsCodec;
  }

  @Inject
  void setSaslCodec(SASLCodec saslCodec) {
    this.saslCodec = saslCodec;
  }

  @Inject
  public void setPresenceCodec(PresenceCodec presenceCodec) {
    this.presenceCodec = presenceCodec;
  }

}
