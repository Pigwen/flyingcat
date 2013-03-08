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
package org.maodian.flycat.xmpp.state;

import java.nio.charset.StandardCharsets;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.maodian.flycat.xmpp.SASLError;
import org.maodian.flycat.xmpp.StreamError;
import org.maodian.flycat.xmpp.XmppException;
import org.maodian.flycat.xmpp.XmppNamespace;
import org.maodian.flycat.xmpp.state.StreamState.AuthenticatedStreamState;

/**
 * @author Cole Wen
 *
 */
public class SASLCommand extends ContextAwareCommand {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.state.Command#execute(javax.xml.stream.XMLStreamReader, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  public State execute(XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException {
    QName qname = new QName(XmppNamespace.SASL, "auth");
    if (!qname.equals(xmlsr.getName())) {
      throw new XmppException(StreamError.INVALID_NAMESPACE)
          .set("QName", xmlsr.getName());
    }
    
    String mechanism = xmlsr.getAttributeValue("", "mechanism");
    if (StringUtils.equals("PLAIN", mechanism)) {
      String base64Data = xmlsr.getElementText();
      
      if (!Base64.isBase64(base64Data)) {
        throw new XmppException(SASLError.INCORRECT_ENCODING);
      }
      
      byte[] value = Base64.decodeBase64(base64Data);
      String text = new String(value, StandardCharsets.UTF_8);
      
      // apply PLAIN SASL mechanism whose rfc locates at http://tools.ietf.org/html/rfc4616
      int[] nullPosition = {-1, -1};
      int nullIndex = 0;
      for (int i = 0; i < text.length(); ++i) {
        if (text.codePointAt(i) == 0) {
          // a malicious base64 value may contain more than two null character
          if (nullIndex > 1) {
            throw new XmppException(SASLError.MALFORMED_REQUEST);
          }
          nullPosition[nullIndex++] = i;
        }
      }
      
      if (nullPosition[0] == -1 || nullPosition[1] == -1) {
        throw new XmppException("The format is invalid", SASLError.MALFORMED_REQUEST);
      }
      String authzid = StringUtils.substring(text, 0, nullPosition[0]);
      String authcid = StringUtils.substring(text, nullPosition[0] + 1, nullPosition[1]);
      String password = StringUtils.substring(text, nullPosition[1] + 1);
      
      if (authzid.getBytes(StandardCharsets.UTF_8).length > 255 || authcid.getBytes(StandardCharsets.UTF_8).length > 255
          || password.getBytes(StandardCharsets.UTF_8).length > 255) {
        throw new XmppException("authorization id, authentication id and password should be equal or less than 255 bytes", 
            SASLError.MALFORMED_REQUEST);
      }
      
      getXmppContext().setBareJID(authcid + "@localhost");
      xmlsw.setPrefix("", XmppNamespace.SASL);
      xmlsw.writeEmptyElement(XmppNamespace.SASL, "success");
      xmlsw.writeDefaultNamespace(XmppNamespace.SASL);
      xmlsw.writeEndDocument();
      return new AuthenticatedStreamState();
    } else {
      throw new XmppException(SASLError.INVALID_MECHANISM).set("mechanism", mechanism);
    }
  }

}
