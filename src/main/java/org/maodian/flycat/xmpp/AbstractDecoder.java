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
package org.maodian.flycat.xmpp;

import javax.xml.namespace.QName;

import org.maodian.flycat.ApplicationContext;
import org.maodian.flycat.xmpp.StanzaError.Type;
import org.maodian.flycat.xmpp.codec.Decoder;

/**
 * @author Cole Wen
 *
 */
public abstract class AbstractDecoder implements Decoder {

  protected Decoder findDecoder(QName key, Stanzas stanzas) {
    Decoder decoder = ApplicationContext.getInstance().getDecoder(key);
    //TODO: here we should distinguish service_unavailable and feature_not_implemented
    if (decoder == null) {
      throw new XmppException("The ", new StanzaError(stanzas, StanzaErrorCondition.SERVICE_UNAVAILABLE, Type.CANCEL));
    }
    return decoder;
  }
}
