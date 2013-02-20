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

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.maodian.flycat.xmpp.Bind;
import org.maodian.flycat.xmpp.StreamError;
import org.maodian.flycat.xmpp.XmppException;
import org.maodian.flycat.xmpp.XmppNamespace;

/**
 * @author Cole Wen
 *
 */
public class BindCodec implements Decoder {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Decoder#decode(java.lang.String)
   */
  @Override
  public Bind decode(XMLStreamReader xmlsr) {
    try {
      xmlsr.nextTag();
      xmlsr.require(XMLStreamConstants.START_ELEMENT, XmppNamespace.BIND, "resource");
      Bind bind = new Bind();
      bind.setResource(xmlsr.getElementText());
      return bind;
    } catch (XMLStreamException e) {
      throw new XmppException(e, StreamError.INVALID_XML);
    }
  }

}
