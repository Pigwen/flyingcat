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

import org.maodian.flyingcat.im.IMSession;
import org.maodian.flyingcat.im.InMemorySession;
import org.maodian.flyingcat.xmpp.ApplicationContext;
import org.maodian.flyingcat.xmpp.DefaultApplicationContext;
import org.maodian.flyingcat.xmpp.state.DefaultXmppContext;
import org.maodian.flyingcat.xmpp.state.XmppContext;
import org.maodian.flyingcat.xmpp.state.XmppContextFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @author Cole Wen
 * 
 */
public class XmppModule extends AbstractModule {

  /*
   * (non-Javadoc)
   * 
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().implement(XmppContext.class, DefaultXmppContext.class).build(
        XmppContextFactory.class));
    bind(ApplicationContext.class).to(DefaultApplicationContext.class).in(Singleton.class);
    bind(IMSession.class).to(InMemorySession.class);
  }

}
