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

import javax.xml.namespace.QName;

import org.maodian.flyingcat.xmpp.codec.Decoder;
import org.maodian.flyingcat.xmpp.codec.Encoder;
import org.maodian.flyingcat.xmpp.codec.InfoQueryProcessor;
import org.maodian.flyingcat.xmpp.state.ContextAwareCommand;

/**
 * @author Cole Wen
 *
 */
public interface ApplicationContext {
  void init();
  
  void registerDecoder(QName qname, Decoder decoder);
  
  void registerEncoder(Class<?> clazz, Encoder encoder);
  
  void registerProcessor(Class<?> clazz, InfoQueryProcessor processor);
  
  Decoder getDecoder(QName qname);
  
  Encoder getEncoder(Class<?> clazz);
  
  InfoQueryProcessor getProcessor(Class<?> clazz);

  Class<? extends ContextAwareCommand> getCommand(QName qName);
}
