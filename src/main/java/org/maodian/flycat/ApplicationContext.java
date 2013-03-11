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
package org.maodian.flycat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.maodian.flycat.xmpp.Bind;
import org.maodian.flycat.xmpp.InfoQuery;
import org.maodian.flycat.xmpp.Session;
import org.maodian.flycat.xmpp.XmppNamespace;
import org.maodian.flycat.xmpp.codec.BindCodec;
import org.maodian.flycat.xmpp.codec.Decoder;
import org.maodian.flycat.xmpp.codec.Encoder;
import org.maodian.flycat.xmpp.codec.InfoQueryCodec;
import org.maodian.flycat.xmpp.codec.InfoQueryProcessor;
import org.maodian.flycat.xmpp.codec.SessionCodec;
import org.maodian.flycat.xmpp.state.ContextAwareCommand;
import org.maodian.flycat.xmpp.state.InfoQueryCommand;
import org.maodian.flycat.xmpp.state.SASLCommand;
import org.maodian.flycat.xmpp.state.TLSCommand;

/**
 * @author Cole Wen
 *
 */
public class ApplicationContext {
  private static final ApplicationContext INSTANCE = new ApplicationContext();
  private Map<QName, Decoder> decoderMap = new ConcurrentHashMap<>();
  private Map<Class<?>, Encoder> encoderMap = new ConcurrentHashMap<>();
  private Map<Class<?>, InfoQueryProcessor> processorMap = new ConcurrentHashMap<>();
  private Map<QName, Class<? extends ContextAwareCommand>> cmdMap = new ConcurrentHashMap<>(); 
  
  public static ApplicationContext getInstance() {
    return INSTANCE;
  }
  
  private ApplicationContext() {
    InfoQueryCodec infoQueryCodec = new InfoQueryCodec();
    BindCodec bindCodec = new BindCodec();
    SessionCodec sessionCodec = new SessionCodec();
    
    decoderMap.put(new QName(XmppNamespace.CLIENT_CONTENT, "iq"), infoQueryCodec);
    decoderMap.put(new QName(XmppNamespace.BIND, "bind"), bindCodec);
    decoderMap.put(new QName(XmppNamespace.SESSION, "session"), sessionCodec);
    
    encoderMap.put(InfoQuery.class, infoQueryCodec);
    encoderMap.put(Bind.class, bindCodec);
    encoderMap.put(Session.class, sessionCodec);
    
    processorMap.put(Session.class, sessionCodec);
    processorMap.put(Bind.class, bindCodec);
    
    cmdMap.put(new QName(XmppNamespace.TLS, "starttls"), TLSCommand.class);
    cmdMap.put(new QName(XmppNamespace.SASL, "auth"), SASLCommand.class);
    cmdMap.put(new QName(XmppNamespace.CLIENT_CONTENT, "iq"), InfoQueryCommand.class);
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
}
