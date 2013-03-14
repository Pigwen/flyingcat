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
package org.maodian.flyingcat.di;

import javax.inject.Singleton;

import org.maodian.flyingcat.netty.handler.XMLFragmentDecoder;
import org.maodian.flyingcat.netty.handler.XmppXMLStreamHandler;
import org.maodian.flyingcat.xmpp.ApplicationContext;
import org.maodian.flyingcat.xmpp.XmppServer;
import org.maodian.flyingcat.xmpp.XmppServerInitializer;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * @author Cole Wen
 *
 */
public class XmppModule extends AbstractModule {

  /* (non-Javadoc)
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    bindConstant().annotatedWith(Names.named("ServerPort")).to(5222);
    bind(XmppServer.class).in(Singleton.class);
    bind(ApplicationContext.class).in(Singleton.class);
    bind(XmppServerInitializer.class).in(Singleton.class);
    
    bind(XMLFragmentDecoder.class);
    bind(XmppXMLStreamHandler.class);
  }

}
