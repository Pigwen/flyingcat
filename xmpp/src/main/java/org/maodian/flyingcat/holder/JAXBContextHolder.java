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
package org.maodian.flyingcat.holder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * 
 * @author Cole Wen
 *
 */
public final class JAXBContextHolder {
  static final JAXBContext context = createJAXBContext();
  
  private static final JAXBContext createJAXBContext() {
    try {
      return JAXBContext.newInstance("ietf.params.xml.ns.xmpp_sasl"
          + ":ietf.params.xml.ns.xmpp_stanzas"
          + ":ietf.params.xml.ns.xmpp_streams"
          + ":ietf.params.xml.ns.xmpp_tls"
          + ":jabber.client"
          + ":jabber.server"
          + ":org.jabber.etherx.streams");
    } catch (JAXBException e) {
      throw new RuntimeException("Creating JAXBContext has failed", e);
    }
  }
  
  public static final JAXBContext getJAXBContext() {
    return context;
  }
}
