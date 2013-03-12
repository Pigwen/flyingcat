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
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.maodian.flyingcat.im.ErrorCode;
import org.maodian.flyingcat.im.IMException;
import org.maodian.flyingcat.im.IMSession;
import org.maodian.flyingcat.im.UserError;
import org.maodian.flyingcat.im.entity.User;
import org.maodian.flyingcat.xmpp.AbstractCodec;
import org.maodian.flyingcat.xmpp.InfoQuery;
import org.maodian.flyingcat.xmpp.StanzaError;
import org.maodian.flyingcat.xmpp.StanzaError.Type;
import org.maodian.flyingcat.xmpp.StanzaErrorCondition;
import org.maodian.flyingcat.xmpp.StreamError;
import org.maodian.flyingcat.xmpp.XmppException;
import org.maodian.flyingcat.xmpp.codec.InfoQueryProcessor;
import org.maodian.flyingcat.xmpp.state.XmppContext;

/**
 * @author Cole Wen
 *
 */
public class RegistrationCodec extends AbstractCodec implements InfoQueryProcessor {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Decoder#decode(javax.xml.stream.XMLStreamReader)
   */
  @Override
  public Object decode(XMLStreamReader xmlsr) {
    try {
      xmlsr.require(XMLStreamConstants.START_ELEMENT, InBandRegistration.REGISTER, "query");
      String username = null;
      String password = null;

      while (xmlsr.nextTag() == XMLStreamConstants.START_ELEMENT) {
        if (xmlsr.getName().equals(new QName(InBandRegistration.REGISTER, "username"))) {
          username = xmlsr.getElementText();
        } else if (xmlsr.getName().equals(new QName(InBandRegistration.REGISTER, "password"))) {
          password = xmlsr.getElementText();
        }
      }
      return new Registration(username, password);
    } catch (XMLStreamException e) {
      throw new XmppException(e, StreamError.INVALID_XML);
    }
  }
  
  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Encoder#encode(java.lang.Object, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  public void encode(Object object, XMLStreamWriter xmlsw) throws XMLStreamException {
    xmlsw.writeStartElement("", "query", InBandRegistration.REGISTER);
    xmlsw.writeDefaultNamespace(InBandRegistration.REGISTER);
    
    xmlsw.writeEmptyElement(InBandRegistration.REGISTER, "username");
    xmlsw.writeEmptyElement(InBandRegistration.REGISTER, "password");
    
    xmlsw.writeEndElement();
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Processor#process(org.maodian.flycat.xmpp.state.XmppContext, java.lang.Object)
   */
  @Override
  public Object processGet(XmppContext context, InfoQuery iq) {
    return new Registration();
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.InfoQueryProcessor#processSet(org.maodian.flycat.xmpp.state.XmppContext, org.maodian.flycat.xmpp.InfoQuery)
   */
  @Override
  public Object processSet(XmppContext context, InfoQuery iq) {
    Registration registration = (Registration) iq.getPayload();
    String username = registration.getUsername();
    String password = registration.getPassword();
    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
      throw new XmppException(new StanzaError(iq, StanzaErrorCondition.NOT_ACCEPTABLE, Type.MODIFY));
    }
    
    IMSession session = context.getIMSession();
    try {
      session.register(new User(username, password));
    } catch (IMException e) {
      ErrorCode errorCode = e.getErrorCode();
      if (errorCode == UserError.DUPLICATED_USERNAME) {
        throw new XmppException(e, new StanzaError(iq, StanzaErrorCondition.CONFLICT, Type.CANCEL));
      }
    }
    return null;
  }

}
