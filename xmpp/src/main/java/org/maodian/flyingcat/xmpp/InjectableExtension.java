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

import com.google.inject.Injector;

/**
 * @author Cole Wen
 *
 */
public abstract class InjectableExtension implements Extension {
  private Injector injector;
  
  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.Extension#setInjector(com.google.inject.Injector)
   */
  @Override
  public void setInjector(Injector injector) {
    this.injector = injector;
  }
  
  protected Injector getInjector() {
    return injector;
  }
}
