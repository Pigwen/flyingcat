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
package org.maodian.flyingcat.xmpp.codec;

import javax.inject.Inject;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flyingcat.xmpp.ApplicationContext;
import org.maodian.flyingcat.xmpp.XmppNamespace;
import org.maodian.flyingcat.xmpp.entity.InfoQuery;
import org.maodian.flyingcat.xmpp.entity.Session;
import org.maodian.flyingcat.xmpp.state.StreamError;
import org.maodian.flyingcat.xmpp.state.XmppContext;
import org.maodian.flyingcat.xmpp.state.XmppException;

/**
 * @author Cole Wen
 *
 */
public class SessionCodec extends AbstractCodec implements InfoQueryProcessor {

  /**
   * @param applicationContext
   */
  @Inject
  public SessionCodec(ApplicationContext applicationContext) {
    super(applicationContext);
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Decoder#decode(javax.xml.stream.XMLStreamReader)
   */
  @Override
  public Object decode(XMLStreamReader xmlsr) {
    try {
      xmlsr.require(XMLStreamConstants.START_ELEMENT, XmppNamespace.SESSION, "session");
      return new Session();
    } catch (XMLStreamException e) {
      throw new XmppException(e, StreamError.INVALID_XML);
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Encoder#encode(java.lang.Object, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  public void encode(Object object, XMLStreamWriter xmlsw) throws XMLStreamException {
    throw new UnsupportedOperationException("Session doesnot need to be encode");
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Processor#process(java.lang.Object)
   */
  @Override
  public Object processGet(XmppContext context, InfoQuery iq) {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.InfoQueryProcessor#processSet(org.maodian.flycat.xmpp.state.XmppContext, org.maodian.flycat.xmpp.InfoQuery)
   */
  @Override
  public Object processSet(XmppContext context, InfoQuery iq) {
    return null;
  }

}
