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

import org.apache.shiro.config.Ini;
import org.apache.shiro.guice.ShiroModule;
import org.maodian.flyingcat.im.shiro.InMemoryRealm;

import com.google.inject.Provides;

/**
 * @author Cole Wen
 *
 */
public class XmppShiroModule extends ShiroModule {

  /* (non-Javadoc)
   * @see org.apache.shiro.guice.ShiroModule#configureShiro()
   */
  @Override
  protected void configureShiro() {
    // bind Realm
    bindRealm().to(InMemoryRealm.class);
  }

  @Provides
  Ini loadShiroIni() {
    return Ini.fromResourcePath("classpath:shiro.ini");
  }
}
