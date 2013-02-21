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
package org.maodian.flycat.xmpp.codec;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.maodian.flycat.xmpp.XmppNamespace;

/**
 * @author Cole Wen
 *
 */
public interface Decoder {
  public static final Map<QName, Decoder> CONTAINER = new HashMap<QName, Decoder>() {
    {
      put(new QName(XmppNamespace.CLIENT_CONTENT, "iq"), new InfoQueryCodec());
      put(new QName(XmppNamespace.BIND, "bind"), new BindCodec());
      put(new QName(XmppNamespace.SESSION, "session"), new SessionCodec());
    }
  };

  public Object decode(XMLStreamReader xmlsr);
}