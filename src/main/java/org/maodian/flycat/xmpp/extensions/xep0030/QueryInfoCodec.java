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
package org.maodian.flycat.xmpp.extensions.xep0030;

import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flycat.xmpp.AbstractCodec;
import org.maodian.flycat.xmpp.StreamError;
import org.maodian.flycat.xmpp.XmppException;
import org.maodian.flycat.xmpp.codec.Processor;
import org.maodian.flycat.xmpp.state.XmppContext;

/**
 * @author Cole Wen
 *
 */
public class QueryInfoCodec extends AbstractCodec implements Processor {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Decoder#decode(javax.xml.stream.XMLStreamReader)
   */
  @Override
  public Object decode(XMLStreamReader xmlsr) {
    try {
      xmlsr.require(XMLStreamConstants.START_ELEMENT, ServiceDiscovery.INFORMATION, "query");
      return new QueryInfo();
    } catch (XMLStreamException e) {
      throw new XmppException(e, StreamError.INVALID_XML);
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.codec.Encoder#encode(java.lang.Object, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  public void encode(Object object, XMLStreamWriter xmlsw) throws XMLStreamException {
    xmlsw.writeStartElement("", "query", ServiceDiscovery.INFORMATION);
    xmlsw.writeDefaultNamespace(ServiceDiscovery.INFORMATION);

    QueryInfo qi = (QueryInfo) object;
    List<Identity> identityList = qi.getIdentityList();
    for (Identity identity : identityList) {
      xmlsw.writeEmptyElement("", "identity", ServiceDiscovery.INFORMATION);
      xmlsw.writeAttribute("category", identity.getCategory());
      xmlsw.writeAttribute("type", identity.getType());
    }
    
    List<Feature> featureList = qi.getFeatureList();
    for (Feature feature : featureList) {
      xmlsw.writeEmptyElement(ServiceDiscovery.INFORMATION, "feature");
      xmlsw.writeAttribute("var", feature.getVar());
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.payload.Processor#process(java.lang.Object)
   */
  @Override
  public Object process(XmppContext context, Object payload) {
    QueryInfo qi = new QueryInfo();
    qi.addIdentity(new Identity("auth", "generic")).addIdentity(new Identity("directory", "user"))
        .addIdentity(new Identity("server", "im")).addFeature(new Feature(ServiceDiscovery.INFORMATION))
        .addFeature(new Feature(ServiceDiscovery.ITEM));
    return qi;
  }

}
