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
package org.maodian.flyingcat.xmpp.extensions.xep0077;

import javax.xml.namespace.QName;

import org.maodian.flyingcat.xmpp.GlobalContext;
import org.maodian.flyingcat.xmpp.InjectableExtension;

/**
 * @author Cole Wen
 *
 */
public class InBandRegistrationExtension extends InjectableExtension {

  /* (non-Javadoc)
   * @see org.maodian.flycat.Extension#register(org.maodian.flycat.ApplicationContext)
   */
  @Override
  public void register(GlobalContext ctx) {
    org.springframework.context.ApplicationContext injector = getInjector();
    RegistrationCodec codec = injector.getBean(RegistrationCodec.class);
    
    ctx.registerDecoder(new QName(InBandRegistration.REGISTER, "query"), codec);
    ctx.registerEncoder(Registration.class, codec);
    ctx.registerProcessor(Registration.class, codec);
  }

}
